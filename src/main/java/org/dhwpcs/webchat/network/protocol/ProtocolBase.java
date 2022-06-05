package org.dhwpcs.webchat.network.protocol;

import org.dhwpcs.webchat.network.protocol.packet.Packet;
import org.dhwpcs.webchat.network.protocol.packet.PacketType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ProtocolBase implements Protocol {
    public ProtocolBase(){}

    private Map<String, PacketType<?>> packetRegistry = new HashMap<>();

    protected<T extends Packet> void register(String id, Class<T> packet, Supplier<T> factory) {
        packetRegistry.putIfAbsent(id, new PacketType<>(packet, factory));
    }

    protected<T extends Packet> void replace(String id, Class<T> packet, Supplier<T> factory) {
        packetRegistry.replace(id, new PacketType<>(packet, factory));
    }

    protected<T extends Packet> void unregister(String id) {
        packetRegistry.remove(id);
    }

    protected Class<? extends Packet> queryPacket(String id) {
        return packetRegistry.get(id).type();
    }

    protected Supplier<? extends Packet> queryFactory(String id) {
        return packetRegistry.get(id).factory();
    }

    protected String queryId(Class<? extends Packet> pkt) {
        for(String key : packetRegistry.keySet()) {
            if(packetRegistry.get(key).type() == pkt) {
                return key;
            }
        }
        return null;
    }

    protected Packet createPacket(String id) {
        return queryFactory(id).get();
    }
}
