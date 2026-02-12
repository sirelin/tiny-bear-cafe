package ee.taltech.cafegame.packets;

/**
 * The type Packet send coordinates.
 */
public class PacketSendCoordinates {
    private float x;
    private float y;
    private int playerId;
    private int gameId;

    public PacketSendCoordinates() {

    }

    public PacketSendCoordinates(float x, float y, int playerId, int gameId) {
        this.x = x;
        this.y = y;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets player id.
     *
     * @return the player id
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Sets player id.
     *
     * @param playerId the player id
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Gets game id.
     *
     * @return the game id
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Sets game id.
     *
     * @param gameId the game id
     */
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
