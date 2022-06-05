package org.dhwpcs.webchat.network.protocol.packet;

import java.util.Objects;
import java.util.function.Supplier;

public record PacketType<T extends Packet>(Class<T> type, Supplier<T> factory) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PacketType)) return false;
        PacketType<?> that = (PacketType<?>) o;
        return type.equals(that.type) && factory.equals(that.factory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, factory);
    }
}
