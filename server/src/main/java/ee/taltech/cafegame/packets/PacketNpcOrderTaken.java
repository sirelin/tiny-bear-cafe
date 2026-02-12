package ee.taltech.cafegame.packets;

public class PacketNpcOrderTaken {
    public int npcId;

    public PacketNpcOrderTaken() {}  // Required for Kryo serialization

    public PacketNpcOrderTaken(int npcId) {
        this.npcId = npcId;
    }
}
