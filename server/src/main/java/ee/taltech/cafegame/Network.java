package ee.taltech.cafegame;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import ee.taltech.cafegame.packets.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Network {

    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(PacketPlayerConnect.class);
        kryo.register(PacketSendCoordinates.class);
        kryo.register(PacketPlayersList.class);
        kryo.register(PacketOnSpawnNpc.class);
        kryo.register(PacketRequestNPC.class);
        kryo.register(PacketOnNpcMove.class);
        kryo.register(PacketRemoveNpcMessage.class);
        kryo.register(PacketSendCollisionMap.class);
        kryo.register(PacketPlayerDisconnect.class);
        kryo.register(PacketNpcOrderTaken.class);
        kryo.register(int[][].class);
        kryo.register(int[].class);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(Integer.class);
        kryo.register(String.class);
    }
}
