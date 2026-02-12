package ee.taltech.cafegame.packets;

public class PacketRemoveNpcMessage {
    public int npcId;

    public PacketRemoveNpcMessage() {}  // Required for Kryo serialization

    public PacketRemoveNpcMessage(int npcId) {
        this.npcId = npcId;
    }
}
