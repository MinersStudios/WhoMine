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
 * Represents a registry of configuration packet types used in the Minecraft
 * server networking.
 *
 * @see PacketType
 * @see PacketRegistry
 * @see <a href="https://wiki.vg/Protocol#Configuration">Protocol Wiki - Configuration</a>
 *
 * @version Minecraft {@value SharedConstants#MINECRAFT_VERSION},
 *          Protocol {@value SharedConstants#PROTOCOL_VERSION}
 */
@SuppressWarnings("unused")
public final class ConfigurationPackets {
    //<editor-fold desc="Configuration clientbound packets" defaultstate="collapsed">

    public static final PacketType CLIENT_COOKIE_REQUEST                   = ofMC(CLIENTBOUND, 0x00, "cookie_request");          // "Cookie Request (configuration)
    public static final PacketType CLIENT_PLUGIN_MESSAGE                   = ofMC(CLIENTBOUND, 0x01, "custom_payload");          // Clientbound Plugin Message (configuration)
    public static final PacketType CLIENT_DISCONNECT                       = ofMC(CLIENTBOUND, 0x02, "disconnect");              // Disconnect (configuration)
    public static final PacketType CLIENT_FINISH_CONFIGURATION             = ofMC(CLIENTBOUND, 0x03, "finish_configuration");    // Finish Configuration
    public static final PacketType CLIENT_KEEP_ALIVE                       = ofMC(CLIENTBOUND, 0x04, "keep_alive");              // Clientbound Keep Alive (configuration)
    public static final PacketType CLIENT_PING                             = ofMC(CLIENTBOUND, 0x05, "ping");                    // Ping (configuration)
    public static final PacketType CLIENT_RESET_CHAT                       = ofMC(CLIENTBOUND, 0x06, "reset_chat");              // Reset Chat
    public static final PacketType CLIENT_REGISTRY_DATA                    = ofMC(CLIENTBOUND, 0x07, "registry_data");           // Registry Data
    public static final PacketType CLIENT_REMOVE_RESOURCE_PACK             = ofMC(CLIENTBOUND, 0x08, "resource_pack_pop");       // Remove Resource Pack (configuration)
    public static final PacketType CLIENT_ADD_RESOURCE_PACK                = ofMC(CLIENTBOUND, 0x09, "resource_pack_push");      // Add Resource Pack (configuration)
    public static final PacketType CLIENT_STORE_COOKIE                     = ofMC(CLIENTBOUND, 0x0A, "store_cookie");            // Store Cookie
    public static final PacketType CLIENT_TRANSFER                         = ofMC(CLIENTBOUND, 0x0B, "transfer");                // Transfer (configuration)
    public static final PacketType CLIENT_FEATURE_FLAGS                    = ofMC(CLIENTBOUND, 0x0C, "update_enabled_features"); // Feature Flags
    public static final PacketType CLIENT_UPDATE_TAGS                      = ofMC(CLIENTBOUND, 0x0D, "update_tags");             // Update Tags (configuration)
    public static final PacketType CLIENT_KNOWN_PACKS                      = ofMC(CLIENTBOUND, 0x0E, "select_known_packs");      // Clientbound Known Packs
    public static final PacketType CLIENT_CUSTOM_REPORT_DETAILS            = ofMC(CLIENTBOUND, 0x0F, "custom_report_details");   // Custom Report Details (configuration)
    public static final PacketType CLIENT_SERVER_LINKS                     = ofMC(CLIENTBOUND, 0x10, "server_links");            // Server Links (configuration)

    //</editor-fold>
    //<editor-fold desc="Configuration serverbound packets" defaultstate="collapsed">

    public static final PacketType SERVER_CLIENT_INFORMATION               = ofMC(SERVERBOUND, 0x00, "client_information");      // Client Information (configuration)
    public static final PacketType SERVER_COOKIE_RESPONSE                  = ofMC(SERVERBOUND, 0x01, "cookie_response");         // Cookie Response (configuration)
    public static final PacketType SERVER_PLUGIN_MESSAGE                   = ofMC(SERVERBOUND, 0x02, "custom_payload");          // Serverbound Plugin Message (configuration)
    public static final PacketType SERVER_ACKNOWLEDGE_FINISH_CONFIGURATION = ofMC(SERVERBOUND, 0x03, "finish_configuration");    // Acknowledge Finish Configuration
    public static final PacketType SERVER_KEEP_ALIVE                       = ofMC(SERVERBOUND, 0x04, "keep_alive");              // Serverbound Keep Alive (configuration)
    public static final PacketType SERVER_PONG                             = ofMC(SERVERBOUND, 0x05, "pong");                    // Pong (configuration)
    public static final PacketType SERVER_RESOURCE_PACK_RESPONSE           = ofMC(SERVERBOUND, 0x06, "resource_pack");           // Resource Pack Response (configuration)
    public static final PacketType SERVER_KNOWN_PACKS                      = ofMC(SERVERBOUND, 0x07, "select_known_packs");      // Serverbound Known Packs

    //</editor-fold>

    private static final PacketRegistry REGISTRY =
            PacketRegistry.create(
                    PacketMap.key2PacketBuilder()
                             .add(
                                     //<editor-fold desc="Clientbound packets" defaultstate="collapsed">

                                     CLIENT_COOKIE_REQUEST,
                                     CLIENT_PLUGIN_MESSAGE,
                                     CLIENT_DISCONNECT,
                                     CLIENT_FINISH_CONFIGURATION,
                                     CLIENT_KEEP_ALIVE,
                                     CLIENT_PING,
                                     CLIENT_RESET_CHAT,
                                     CLIENT_REGISTRY_DATA,
                                     CLIENT_REMOVE_RESOURCE_PACK,
                                     CLIENT_ADD_RESOURCE_PACK,
                                     CLIENT_STORE_COOKIE,
                                     CLIENT_TRANSFER,
                                     CLIENT_FEATURE_FLAGS,
                                     CLIENT_UPDATE_TAGS,
                                     CLIENT_KNOWN_PACKS,
                                     CLIENT_CUSTOM_REPORT_DETAILS,
                                     CLIENT_SERVER_LINKS,

                                     //</editor-fold>
                                     //<editor-fold desc="Serverbound packets" defaultstate="collapsed">

                                     SERVER_CLIENT_INFORMATION,
                                     SERVER_COOKIE_RESPONSE,
                                     SERVER_PLUGIN_MESSAGE,
                                     SERVER_ACKNOWLEDGE_FINISH_CONFIGURATION,
                                     SERVER_KEEP_ALIVE,
                                     SERVER_PONG,
                                     SERVER_RESOURCE_PACK_RESPONSE,
                                     SERVER_KNOWN_PACKS

                                     //</editor-fold>
                             ).build()
            );

    @Contract(" -> fail")
    private ConfigurationPackets() throws AssertionError {
        throw new AssertionError("Registry class");
    }

    /**
     * Returns the registry of configuration packet types
     *
     * @return The registry of configuration packet types
     */
    public static @Unmodifiable @NotNull PacketRegistry registry() {
        return REGISTRY;
    }
}
