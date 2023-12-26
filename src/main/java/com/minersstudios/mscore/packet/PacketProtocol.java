package com.minersstudios.mscore.packet;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents the different packet protocols used in Minecraft. Each protocol
 * corresponds to a specific phase in the network connection process.
 *
 * @see PacketType
 * @see PacketFlow
 * @see PacketRegistry
 * @see <a href="https://wiki.vg/Protocol">Protocol Wiki</a>
 */
public enum PacketProtocol {
    HANDSHAKING("handshake", PacketType.Handshaking.packetMap()),
    PLAY("play", PacketType.Play.packetMap()),
    STATUS("status", PacketType.Status.packetMap()),
    LOGIN("login", PacketType.Login.packetMap()),
    CONFIGURATION("configuration", PacketType.Configuration.packetMap());

    private final String stateId;
    private final Map<PacketFlow, Map<Integer, PacketType>> packets;

    private static final PacketProtocol[] VALUES = values();

    /**
     * Constructor for the PacketProtocol enum
     *
     * @param stateId The state id of this protocol
     * @param packets The map of packet types associated with this protocol,
     *                organized by packet flow and packet ID
     */
    PacketProtocol(
            final @NotNull String stateId,
            final @NotNull Map<PacketFlow, Map<Integer, PacketType>> packets
    ) {
        this.stateId = stateId;
        this.packets = packets;
    }

    /**
     * Get the PacketProtocol associated with a specific
     * {@link ConnectionProtocol}
     *
     * @param protocol The ConnectionProtocol for which to retrieve the
     *                 corresponding PacketProtocol
     * @return The PacketProtocol associated with the given ConnectionProtocol
     */
    public static @NotNull PacketProtocol fromMinecraft(final @NotNull ConnectionProtocol protocol) {
        return VALUES[protocol.ordinal()];
    }

    /**
     * Get the state id of this protocol. The id is the same as the id of the
     * {@link ConnectionProtocol} associated with this protocol.
     *
     * @return The state id of this protocol
     */
    public @NotNull String getStateId() {
        return this.stateId;
    }

    /**
     * Get the packet map associated with this protocol. The packet map contains
     * packet types organized by packet flow and packet ID.
     *
     * @return The packet map of this protocol.
     */
    public @NotNull Map<PacketFlow, Map<Integer, PacketType>> getPackets() {
        return this.packets;
    }
}
