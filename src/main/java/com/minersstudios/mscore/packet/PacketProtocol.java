package com.minersstudios.mscore.packet;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents the different packet protocols used in Minecraft.
 * Each protocol corresponds to a specific phase in the
 * network connection process.
 *
 * @see PacketType
 * @see PacketFlow
 * @see PacketRegistry
 * @see <a href="https://wiki.vg/Protocol">Protocol Wiki</a>
 */
public enum PacketProtocol {
    HANDSHAKING(-1, PacketType.Handshaking.PACKET_MAP),
    PLAY(0, PacketType.Play.PACKET_MAP),
    STATUS(1, PacketType.Status.PACKET_MAP),
    LOGIN(2, PacketType.Login.PACKET_MAP);

    private final int id;
    private final Map<PacketFlow, Map<Integer, PacketType>> packets;

    /**
     * Constructor for the PacketProtocol enum
     *
     * @param id      The ID of this protocol
     * @param packets The map of packet types associated
     *                with this protocol, organized by
     *                packet flow and packet ID
     */
    PacketProtocol(
            int id,
            @NotNull Map<PacketFlow, Map<Integer, PacketType>> packets
    ) {
        this.id = id;
        this.packets = packets;
    }

    /**
     * Get the PacketProtocol associated with a specific
     * {@link ConnectionProtocol}
     *
     * @param protocol The ConnectionProtocol for which to
     *                 retrieve the corresponding PacketProtocol
     * @return The PacketProtocol associated with the given ConnectionProtocol
     */
    public static @NotNull PacketProtocol fromMinecraft(@NotNull ConnectionProtocol protocol) {
        return switch (protocol) {
            case LOGIN -> LOGIN;
            case STATUS -> STATUS;
            case PLAY -> PLAY;
            default -> HANDSHAKING;
        };
    }

    /**
     * Get the id of this protocol.
     * The id is the same as the id of the
     * {@link ConnectionProtocol} associated
     * with this protocol.
     *
     * @return The id of this protocol
     */
    public int getId() {
        return this.id;
    }

    /**
     * Get the packet map associated with this protocol.
     * The packet map contains packet types organized by
     * packet flow and packet ID.
     *
     * @return The packet map of this protocol.
     */
    public @NotNull Map<PacketFlow, Map<Integer, PacketType>> getPackets() {
        return this.packets;
    }
}
