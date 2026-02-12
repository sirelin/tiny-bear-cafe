package ee.taltech.cafegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.cafegame.ai.NPC;
import ee.taltech.cafegame.interactables.Interactables;
import ee.taltech.cafegame.packets.*;
import com.badlogic.gdx.Game;
import ee.taltech.cafegame.player.Player;
import ee.taltech.cafegame.player.PlayerController;
import ee.taltech.cafegame.screens.MenuScreen;
import ee.taltech.cafegame.screens.PlayScreen;

import java.io.IOException;
import java.util.*;

public class ClientMain extends Game {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int TIMEOUT = 5000;
    private static final int TCP_PORT = 8080;
    private static final int UDP_PORT = 8081;

    private Player player;
    private SpriteBatch batch;
    private Texture playerTexture;
    private Texture currentPlayerTexture;
    private int clientId;
    private Client client;
    private PlayerController playerController;
    private Map<Integer, Player> players = new HashMap<>();
    private PlayScreen playScreen;
    private MenuScreen menuScreen;
    private Map<Integer, NPC> aiNpcs = new HashMap<>();
    private Map<Integer, Body> remotePlayerBodies = new HashMap<>();
    private Map<Vector2, Interactables> interactables;
    private float soundVolume = 0.3f;

    public float getSoundVolume() { return soundVolume; }
    public void setSoundVolume(float soundVolume) { this.soundVolume = soundVolume; }
    public SpriteBatch getBatch() { return batch; }
    public Client getClient() { return client; }
    public Player getPlayer() { return player; }
    public Map<Integer, Player> getPlayers() { return players; }
    public Texture getCurrentPlayerTexture() { return currentPlayerTexture; }
    public void setCurrentPlayerTexture(Texture texture) { if (texture != null) currentPlayerTexture = texture; }

    @Override
    public void create() {
        batch = new SpriteBatch();
        player = new Player("Liis");
        initializeClient();
        initializeGraphics();
        currentPlayerTexture = playerTexture;
        playerController = new PlayerController(player, client, interactables);
        playScreen = new PlayScreen(this);
        menuScreen = new MenuScreen(this);
        client.sendTCP(new PacketRequestNPC(1, false));
        setScreen(menuScreen);
    }

    private void initializeClient() {
        client = new Client();
        client.start();
        Network.register(client);
        connectToServer();

        PacketPlayerConnect packet = new PacketPlayerConnect();
        packet.setName("Liis");
        packet.setGameId(1);
        client.sendTCP(packet);

        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                handleReceivedData(connection, object);
            }

            @Override
            public void disconnected(Connection connection) {
                handleDisconnection(connection);
            }
        }));
    }

    private void connectToServer() {
        try {
            client.connect(TIMEOUT, SERVER_ADDRESS, TCP_PORT, UDP_PORT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect to server", e);
        }
    }

    private void initializeGraphics() {
        playerTexture = new Texture("pruunkaru.png");
    }

    private void addNpc(int id, float x, float y, String skinName) {
        if (skinName == null || skinName.isEmpty()) return;

        Gdx.app.postRunnable(() -> {
            Texture skinTexture = new Texture(Gdx.files.internal(skinName));
            Sprite sprite = new Sprite(skinTexture);

            NPC npc = aiNpcs.get(id);
            if (npc != null) {
                npc.setX(x);
                npc.setY(y);
                npc.setSkin(skinTexture);
            } else {
                npc = new NPC(id, x, y, sprite, skinTexture);
                aiNpcs.put(id, npc);
            }

            if (playScreen != null) {
                npc.setWaitingTexture(playScreen.waitingOrderTexture);
            }
        });
    }

    private void handleReceivedData(Connection connection, Object object) {
        if (object instanceof PacketPlayerConnect packet) {
            if (packet.getPlayerId() == connection.getID()) {
                player.setGameId(packet.getGameId());
                player.setId(packet.getPlayerId());
                player.setX(50 + (int) (Math.random() * 200));
                player.setY(50 + (int) (Math.random() * 200));
                players.put(packet.getPlayerId(), player);

                client.sendTCP(new PacketRequestNPC(1, false));
            } else {
                Player newPlayer = new Player(packet.getName());
                newPlayer.setId(packet.getPlayerId());
                newPlayer.setX(packet.getX());
                newPlayer.setY(packet.getY());
                players.put(packet.getPlayerId(), newPlayer);
            }
        }

        if (object instanceof PacketOnSpawnNpc packet) {
            addNpc(packet.id, packet.x, packet.y, packet.skinName);
        }

        if (object instanceof PacketOnNpcMove packet) {
            NPC npc = aiNpcs.get(packet.npcId);
            if (npc != null) npc.setTarget(packet.x, packet.y);
        }

        if (object instanceof PacketPlayerDisconnect packet) {
            players.remove(packet.getPlayerId());
        }

        if (object instanceof PacketRemoveNpcMessage msg) {
            if (playScreen != null) {
                playScreen.safelyRemoveNPC(msg.npcId);
            }
        }

        if (object instanceof PacketNpcOrderTaken packet) {
            NPC npc = aiNpcs.get(packet.npcId);
            if (npc != null) {
                npc.setCanTakeOrder(false);
                npc.setWaitingTexture(null);
            }
        }

        if (object instanceof PacketSendCoordinates packet) {
            Player target = players.get(packet.getPlayerId());
            if (target != null) {
                target.setX(packet.getX());
                target.setY(packet.getY());
            } else {
                Player newPlayer = new Player("Unknown");
                newPlayer.setId(packet.getPlayerId());
                newPlayer.setGameId(packet.getGameId());
                newPlayer.setX(packet.getX());
                newPlayer.setY(packet.getY());
                players.put(packet.getPlayerId(), newPlayer);
            }
        }
    }

    private void handleDisconnection(Connection connection) {
        if (player != null) {
            PacketPlayerDisconnect disconnectPacket = new PacketPlayerDisconnect(player.getId(), player.getGameId());
            client.sendTCP(disconnectPacket);
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        if (playerTexture != null) playerTexture.dispose();
        if (batch != null) batch.dispose();

        if (player != null) {
            PacketPlayerDisconnect packet = new PacketPlayerDisconnect(player.getId(), player.getGameId());
            client.sendTCP(packet);
        }

        if (client != null) {
            if (client.isConnected()) {
                client.stop();
            }
            client.close();
        }

        Timer.instance().clear();
        System.exit(0);
    }

    public Map<Integer, NPC> getNPCs() {
        return aiNpcs;
    }

    public void createNpcBodiesIfNeeded(World world) {
        for (NPC npc : aiNpcs.values()) {
            if (npc.getBody() == null) {
                npc.createBody(world);
            }
        }
    }
}
