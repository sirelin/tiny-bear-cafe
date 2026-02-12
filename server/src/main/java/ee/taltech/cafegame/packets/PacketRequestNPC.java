package ee.taltech.cafegame.packets;

public class PacketRequestNPC {
    private boolean npcFromFinishedOrder;

    public int getGAME_ID() {
        return GAME_ID;
    }

    public int GAME_ID;

    public PacketRequestNPC() {}


    public PacketRequestNPC(int gameId, boolean npcFromFinishedOrder) {
        GAME_ID = gameId;
        this.npcFromFinishedOrder = npcFromFinishedOrder;
    }

    public boolean isNpcFromFinishedOrder() {
        return npcFromFinishedOrder;
    }
}
