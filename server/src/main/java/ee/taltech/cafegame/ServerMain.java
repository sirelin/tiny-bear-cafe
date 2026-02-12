package ee.taltech.cafegame;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import ee.taltech.cafegame.ai.NPC;
import ee.taltech.cafegame.packets.*;

import java.io.IOException;
import java.util.*;

public class ServerMain {
    private static final int TCP_PORT = 8080;
    private static final int UDP_PORT = 8081;
    private Server server;
    public static final float PPM = 100f;

    private final Map<Integer, Map<Integer, Player>> gameLobbies = new HashMap<>();
    private final Map<Integer, Map<Integer, NPC>> aiBots = new HashMap<>();
    private final List<Integer> orderTaken = new ArrayList<>();

    public ServerMain() {
        initializeServer();
        addListeners();
    }

    private void initializeServer() {
        server = new Server();
        Network.register(server);
        server.start();
        try {
            server.bind(TCP_PORT, UDP_PORT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to bind server", e);
        }
    }

    private NPC generateSingleNpc(int gameId) {
        aiBots.putIfAbsent(gameId, new HashMap<>());
        Map<Integer, NPC> npcMap = aiBots.get(gameId);

        float spawnX = 415 / PPM;
        float spawnY = 241 / PPM;

        String[] skinFiles = {
                "bear_skins/kollanekaru.png",
                "bear_skins/valgekaru.png",
                "bear_skins/roosakaru.png",
                "bear_skins/panda.png",
                "bear_skins/lillajaakaru.png",
                "bear_skins/punanekaru.png"
        };
        String chosenSkin = skinFiles[new Random().nextInt(skinFiles.length)];

        NPC npc = new NPC(spawnX, spawnY, this);
        npc.setSkinName(chosenSkin);
        npcMap.put(npc.getId(), npc);
        npc.moveThread();

        return npc;
    }

    private void addListeners() {
        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                handleReceivedData(connection, object);
            }

            @Override
            public void disconnected(Connection connection) {
                handleDisconnection(connection);
            }
        });
    }

    private void initialiseNewPlayer(Connection connection, PacketPlayerConnect packet, Map<Integer, Player> playersInLobby) {
        int connectionId = connection.getID();
        int gameId = packet.getGameId();

        Player newPlayer = new Player(packet.getName());
        newPlayer.setId(connectionId);
        newPlayer.setX(packet.getX());
        newPlayer.setY(packet.getY());
        newPlayer.setGameId(gameId);

        playersInLobby.put(connectionId, newPlayer);

        PacketPlayerConnect newPlayerPacket = new PacketPlayerConnect(
                newPlayer.getName(), newPlayer.getId(), gameId, newPlayer.getX(), newPlayer.getY()
        );

        for (Player p : playersInLobby.values()) {
            if (p.getId() != connectionId) {
                server.sendToTCP(p.getId(), newPlayerPacket);
            }
        }

        for (Player p : playersInLobby.values()) {
            if (p.getId() != connectionId) {
                PacketPlayerConnect existingPacket = new PacketPlayerConnect(
                        p.getName(), p.getId(), p.getGameId(), p.getX(), p.getY()
                );
                connection.sendTCP(existingPacket);
            }
        }

        Map<Integer, NPC> npcMap = aiBots.get(gameId);
        if (npcMap != null) {
            for (NPC npc : npcMap.values()) {
                PacketOnSpawnNpc npcPacket = new PacketOnSpawnNpc();
                npcPacket.setId(npc.getId());
                npcPacket.setX(npc.getX());
                npcPacket.setY(npc.getY());
                npcPacket.skinName = npc.getSkinName();
                connection.sendTCP(npcPacket);
            }
        }

        connection.sendTCP(newPlayerPacket);
    }

    private void handleReceivedData(Connection connection, Object object) {
        int connectionId = connection.getID();

        if (object instanceof PacketPlayerDisconnect packet) {
            handleDisconnection(connection);
        }

        if (object instanceof PacketRemoveNpcMessage msg) {
            server.sendToAllTCP(msg);
            aiBots.get(1).remove(msg.npcId);
        }

        if (object instanceof PacketSendCollisionMap packet) {
            for (NPC npc : aiBots.get(1).values()) {
                npc.updateCollisionMap(packet.getCollisionMap());
            }
        }

        if (object instanceof PacketNpcOrderTaken packet) {
            Map<Integer, NPC> npcMap = aiBots.get(1);
            if (npcMap.containsKey(packet.npcId)) {
                orderTaken.add(packet.npcId);
                sendToGameLobby(1, packet);
            }
        }

        if (object instanceof PacketRequestNPC packet) {
            int gameId = 1;
            aiBots.putIfAbsent(gameId, new HashMap<>());
            gameLobbies.putIfAbsent(gameId, new HashMap<>());

            Map<Integer, NPC> npcMap = aiBots.get(gameId);
            Map<Integer, Player> playerMap = gameLobbies.get(gameId);
            int npcLimit = 2 * playerMap.size();

            if (npcMap.size() + 2 <= npcLimit && !packet.isNpcFromFinishedOrder()) {
                for (int i = 0; i < 2; i++) {
                    NPC npc = generateSingleNpc(gameId);
                    PacketOnSpawnNpc npcPacket = new PacketOnSpawnNpc();
                    npcPacket.setId(npc.getId());
                    npcPacket.setX(npc.getX());
                    npcPacket.setY(npc.getY());
                    npcPacket.skinName = npc.getSkinName();
                    sendToGameLobby(gameId, npcPacket);
                    connection.sendTCP(npcPacket);
                }
            } else if (packet.isNpcFromFinishedOrder()) {
                NPC npc = generateSingleNpc(gameId);
                PacketOnSpawnNpc npcPacket = new PacketOnSpawnNpc();
                npcPacket.setId(npc.getId());
                npcPacket.setX(npc.getX());
                npcPacket.setY(npc.getY());
                npcPacket.skinName = npc.getSkinName();
                sendToGameLobby(gameId, npcPacket);
                connection.sendTCP(npcPacket);
            }
        }

        if (object instanceof PacketPlayerConnect packet) {
            int gameId = packet.getGameId();
            gameLobbies.putIfAbsent(gameId, new HashMap<>());
            Map<Integer, Player> playersInLobby = gameLobbies.get(gameId);
            initialiseNewPlayer(connection, packet, playersInLobby);
        }

        if (object instanceof PacketSendCoordinates packet) {
            int gameId = packet.getGameId();
            Map<Integer, Player> playersInLobby = gameLobbies.get(gameId);
            if (playersInLobby != null) {
                Player currentPlayer = playersInLobby.get(packet.getPlayerId());
                if (currentPlayer != null) {
                    currentPlayer.setX(packet.getX());
                    currentPlayer.setY(packet.getY());

                    PacketSendCoordinates updatePacket = new PacketSendCoordinates();
                    updatePacket.setX(currentPlayer.getX());
                    updatePacket.setY(currentPlayer.getY());
                    updatePacket.setPlayerId(currentPlayer.getId());
                    updatePacket.setGameId(gameId);

                    sendToGameLobby(gameId, updatePacket);
                }
            }
        }
    }

    private void sendToGameLobby(int gameId, Object packet) {
        Map<Integer, Player> playersInLobby = gameLobbies.get(gameId);
        if (playersInLobby != null) {
            for (Integer playerId : playersInLobby.keySet()) {
                server.sendToTCP(playerId, packet);
            }
        }
    }

    private void handleDisconnection(Connection connection) {
        int connectionId = connection.getID();
        for (Map.Entry<Integer, Map<Integer, Player>> entry : gameLobbies.entrySet()) {
            Map<Integer, Player> players = entry.getValue();
            if (players.containsKey(connectionId)) {
                players.remove(connectionId);
                PacketPlayerDisconnect packet = new PacketPlayerDisconnect(connectionId, entry.getKey());
                sendToGameLobby(entry.getKey(), packet);

                if (players.isEmpty()) {
                    aiBots.clear();
                }
                return;
            }
        }
    }

    public void moveNpc(int gameId, int npcId, float newX, float newY) {
        Map<Integer, NPC> npcMap = aiBots.get(gameId);
        if (npcMap == null) return;

        NPC npc = npcMap.get(npcId);
        if (npc != null) {
            npc.setX(newX);
            npc.setY(newY);

            PacketOnNpcMove packet = new PacketOnNpcMove();
            packet.setNpcId(npcId);
            packet.setX(newX);
            packet.setY(newY);
            sendToGameLobby(gameId, packet);
        }
    }

    public static void main(String[] args) {
        new ServerMain();
    }
}
