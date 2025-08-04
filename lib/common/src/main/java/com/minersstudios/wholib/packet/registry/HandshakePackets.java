package com.minersstudios.wholib.packet.registry;

import com.minersstudios.wholib.packet.PacketRegistry;
import com.minersstudios.wholib.packet.collection.PacketMap;
import com.minersstudios.wholib.packet.PacketType;
import com.minersstudios.wholib.utility.SharedConstants;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static com.minersstudios.wholib.packet.PacketBound.*;
import static com.minersstudios.wholib.packet.PacketType.ofMC;

/**
 * Represents a registry of handshake packet types used in the Minecraft
 * server networking.
 *
 * @see PacketType
 * @see PacketRegistry
 * @see <a href="https://wiki.vg/Protocol#Handshaking">Protocol Wiki - Handshake</a>
 *
 * @version Minecraft {@value SharedConstants#MINECRAFT_VERSION},
 *          Protocol {@value SharedConstants#PROTOCOL_VERSION}
 */
@SuppressWarnings("unused")
public final class HandshakePackets {
    //<editor-fold desc="Handshaking clientbound packets" defaultstate="collapsed">

    // There are no clientbound packets in the Handshaking state, since the
    // protocol immediately switches to a different state after the client sends
    // the first packet.

    //</editor-fold>
    //<editor-fold desc="Handshaking serverbound packets" defaultstate="collapsed">

    public static final PacketType SERVER_HANDSHAKE               = ofMC(SERVERBOUND, 0x00, "intention"); // Handshake
    public static final PacketType SERVER_LEGACY_SERVER_LIST_PING = ofMC(SERVERBOUND, 0xFE, "");          // Legacy Server List Ping

    //</editor-fold>

    private static final PacketRegistry REGISTRY =
            PacketRegistry.create(
                    PacketMap.key2PacketBuilder()
                             .add(
                                     //<editor-fold desc="Serverbound packets" defaultstate="collapsed">

                                     SERVER_HANDSHAKE,
                                     SERVER_LEGACY_SERVER_LIST_PING

                                     //</editor-fold>
                             ).build()
            );

    @Contract(" -> fail")
    private HandshakePackets() throws AssertionError {
        throw new AssertionError("Registry class");
    }

    /**
     * Returns the registry of the handshake packet types
     *
     * @return The registry of the handshake packet types
     */
    public static @Unmodifiable @NotNull PacketRegistry registry() {
        return REGISTRY;
    }
}
