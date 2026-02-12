package ee.taltech.cafegame.packets;

public class PacketPlayerDisconnect {
    private int playerId;
    private int gameId;

    // Constructor
    public PacketPlayerDisconnect(int playerId, int gameId) {
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public PacketPlayerDisconnect() {
    }

    // Getters and Setters
    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
