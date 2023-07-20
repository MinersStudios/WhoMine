package com.minersstudios.mscore.packet;

import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a packet container. It contains the packet
 * and the packet type. The packet type contains the
 * id, flow, name and class of the packet. The packet can
 * be modified, but the packet type cannot be changed.
 *
 * @see PacketType
 */
public class PacketContainer {
    private Packet<?> packet;
    private final PacketType type;

    /**
     * Packet container constructor
     *
     * @param packet The packet to contain
     * @param type   The packet type of the packet
     */
    public PacketContainer(
            @NotNull Packet<?> packet,
            @NotNull PacketType type
    ) {
        this.packet = packet;
        this.type = type;
    }

    /**
     * @return The packet contained in this container
     */
    public @NotNull Packet<?> getPacket() {
        return this.packet;
    }

    /**
     * @param packet The packet to set
     * @throws IllegalArgumentException If the packet type of the packet
     *                                  is not the same as the packet type
     *                                  of this container
     *                                  (Checks by comparing the classes)
     */
    public void setPacket(@NotNull Packet<?> packet) throws IllegalArgumentException {
        if (this.packet.getClass() != packet.getClass()) {
            throw new IllegalArgumentException("Packet type cannot be changed!");
        }

        this.packet = packet;
    }

    /**
     * @return The packet type of the packet contained in this container
     */
    public @NotNull PacketType getType() {
        return this.type;
    }

    /**
     * @return The string representation of this packet container
     */
    @Override
    public @NotNull String toString() {
        return "PacketContainer{" +
                "packet=" + this.packet.getClass() +
                ", type=" + this.type +
                '}';
    }
}
