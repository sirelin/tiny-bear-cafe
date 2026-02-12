package ee.taltech.cafegame.packets;

public class PacketOnNpcMove {
    public int npcId;
    public float x;
    public float y;

    public PacketOnNpcMove(){

    }


    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int newNpcId) {
        this.npcId = newNpcId;
    }


    public PacketOnNpcMove(int npcId, float newX, float newY) {
    }
}
