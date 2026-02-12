package ee.taltech.cafegame.packets;

public class PacketOnSpawnNpc {
    public int id;
    public float x, y;
    public String skinName; // ← Lisa see

    public PacketOnSpawnNpc() {}

    public PacketOnSpawnNpc(int id, float x, float y, String skinName) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.skinName = skinName;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSkinName() {
        return skinName;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }
}
