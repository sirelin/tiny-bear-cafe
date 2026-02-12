package ee.taltech.cafegame.packets;

public class PacketSendCollisionMap {

    // A 2D array representing the collision map: 0 for walkable, 1 for blocked
    private int[][] collisionMap;

    // Constructor that takes a 2D collision map array
    public PacketSendCollisionMap(int[][] collisionMap) {
        this.collisionMap = collisionMap;
    }

    // Getter for the collision map
    public int[][] getCollisionMap() {
        return collisionMap;
    }

    // Setter for the collision map
    public void setCollisionMap(int[][] collisionMap) {
        this.collisionMap = collisionMap;
    }
}
