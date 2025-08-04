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
 * Represents a registry of status packet types used in the Minecraft
 * server networking.
 *
 * @see PacketType
 * @see PacketRegistry
 * @see <a href="https://wiki.vg/Protocol#Status">Protocol Wiki - Status</a>
 *
 * @version Minecraft {@value SharedConstants#MINECRAFT_VERSION},
 *          Protocol {@value SharedConstants#PROTOCOL_VERSION}
 */
public final class StatusPackets {
    //<editor-fold desc="Status clientbound packets" defaultstate="collapsed">

    public static final PacketType CLIENT_STATUS_RESPONSE = ofMC(CLIENTBOUND, 0x00, "status_response"); // Status Response
    public static final PacketType CLIENT_PONG_RESPONSE   = ofMC(CLIENTBOUND, 0x01, "pong_response");   // Pong Response (status)

    //</editor-fold>
    //<editor-fold desc="Status serverbound packets" defaultstate="collapsed">

    public static final PacketType SERVER_STATUS_REQUEST  = ofMC(SERVERBOUND, 0x00, "status_request");  // Status Request
    public static final PacketType SERVER_PING_REQUEST    = ofMC(SERVERBOUND, 0x01, "ping_request");    // Ping Request (status)

    //</editor-fold>

    private static final PacketRegistry REGISTRY =
            PacketRegistry.create(
                    PacketMap.key2PacketBuilder()
                             .add(
                                     //<editor-fold desc="Clientbound packets" defaultstate="collapsed">

                                     CLIENT_STATUS_RESPONSE,
                                     CLIENT_PONG_RESPONSE,

                                     //</editor-fold>
                                     //<editor-fold desc="Serverbound packets" defaultstate="collapsed">

                                     SERVER_STATUS_REQUEST,
                                     SERVER_PING_REQUEST

                                     //</editor-fold>
                             ).build()
            );

    @Contract(" -> fail")
    private StatusPackets() throws AssertionError {
        throw new AssertionError("Registry class");
    }

    /**
     * Returns the registry of status packet types
     *
     * @return The registry of status packet types
     */
    public static @Unmodifiable @NotNull PacketRegistry registry() {
        return REGISTRY;
    }
}
