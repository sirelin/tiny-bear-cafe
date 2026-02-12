package ee.taltech.cafegame.packets;

/**
 * The type Packet player connect.
 */
public class PacketPlayerConnect {

    private String name;
    private int playerId;
    private int gameId;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    private float x, y;

    public PacketPlayerConnect() {

    }

    public PacketPlayerConnect(String name, int playerId, int gameId, float x, float y) {
        this.name = name;
        this.playerId = playerId;
        this.gameId = gameId;
        this.x = x;
        this.y = y;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
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
