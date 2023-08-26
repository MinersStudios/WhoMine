package com.minersstudios.mscore.packet;

import com.minersstudios.mscore.plugin.MSLogger;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static net.minecraft.network.protocol.PacketFlow.CLIENTBOUND;
import static net.minecraft.network.protocol.PacketFlow.SERVERBOUND;

/**
 * This class represents a packet type used in the
 * Minecraft server networking. It contains information
 * about the packet's flow (CLIENTBOUND or SERVERBOUND),
 * its ID, and its name.
 *
 * @see PacketProtocol
 * @see PacketFlow
 * @see PacketRegistry
 * @see <a href="https://wiki.vg/Protocol">Protocol Wiki</a>
 */
@SuppressWarnings("unused")
public class PacketType {
    private final PacketFlow flow;
    private final int id;
    private final String name;

    /**
     * PacketType constructor
     *
     * @param flow The flow of the packet
     *             (CLIENTBOUND or SERVERBOUND)
     * @param id   The ID of the packet
     * @param name The name of the packet
     */
    private PacketType(
            final @NotNull PacketFlow flow,
            final int id,
            final @NotNull String name
    ) {
        this.flow = flow;
        this.id = id;
        this.name = name;
    }

    /**
     * Retrieves the PacketType corresponding to a given
     * packet class
     *
     * @param clazz The packet class for which to retrieve
     *              the PacketType
     * @return The PacketType for the given packet class,
     *         or null if not found
     */
    public static @Nullable PacketType fromClass(final @NotNull Class<?> clazz) {
        return PacketRegistry.getTypeFromClass(clazz);
    }

    /**
     * @return The flow of the packet
     *         (CLIENTBOUND or SERVERBOUND)
     */
    public @NotNull PacketFlow getFlow() {
        return this.flow;
    }

    /**
     * @return The ID of the packet
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return The name of the packet
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Retrieves the packet class associated with this PacketType
     *
     * @return The packet class associated with this PacketType
     */
    public @NotNull Class<?> getPacketClass() {
        return PacketRegistry.getClassFromType(this);
    }

    /**
     * @return True if the packet is SERVERBOUND,
     *         false otherwise
     */
    public boolean isReceive() {
        return this.flow == SERVERBOUND;
    }

    /**
     * @return True if the packet is CLIENTBOUND,
     *         false otherwise
     */
    public boolean isSend() {
        return this.flow == CLIENTBOUND;
    }

    /**
     * @return The string representation of this packet type
     */
    @Override
    public @NotNull String toString() {
        return "PacketType{" +
                "bound=" + this.flow +
                ", id=" + this.id +
                ", name='" + this.name + '\'' +
                '}';
    }

    /**
     * Internal method to get a map of packet IDs to PacketType
     * instances from a given class
     *
     * @param clazz The class from which to retrieve the packet map
     * @return A map of packet IDs to PacketType instances
     */
    private static @NotNull Map<Integer, PacketType> getPacketsMap(final @NotNull Class<?> clazz) {
        final var map = new HashMap<Integer, PacketType>();

        for (final var field : clazz.getDeclaredFields()) {
            if (field.getType() != PacketType.class) continue;
            try {
                final PacketType packetType = (PacketType) field.get(null);
                map.put(packetType.id, packetType);
            } catch (IllegalAccessException e) {
                MSLogger.log(Level.SEVERE, "Could not get packet type from field " + field.getName(), e);
            }
        }

        return map;
    }

    /**
     * Nested class for the Handshaking packet state
     */
    public static class Handshaking {
        public static final Map<PacketFlow, Map<Integer, PacketType>> PACKET_MAP = new HashMap<>();

        static {
            PACKET_MAP.put(CLIENTBOUND, Client.PACKET_MAP);
            PACKET_MAP.put(SERVERBOUND, Server.PACKET_MAP);
        }

        public static class Client {
            public static final Map<Integer, PacketType> PACKET_MAP = getPacketsMap(Client.class);
        }

        public static class Server {
            public static final PacketType HANDSHAKE = new PacketType(SERVERBOUND, 0x00, "Handshake");

            public static final Map<Integer, PacketType> PACKET_MAP = getPacketsMap(Server.class);
        }
    }

    /**
     * Nested class for the Status packet state
     */
    public static class Status {
        public static final Map<PacketFlow, Map<Integer, PacketType>> PACKET_MAP = new HashMap<>();

        static {
            PACKET_MAP.put(CLIENTBOUND, Client.PACKET_MAP);
            PACKET_MAP.put(SERVERBOUND, Server.PACKET_MAP);
        }

        public static class Client {
            public static final PacketType STATUS_RESPONSE = new PacketType(CLIENTBOUND, 0x00, "Status Response");
            public static final PacketType PING_RESPONSE = new PacketType(CLIENTBOUND, 0x01, "Ping Response");

            public static final Map<Integer, PacketType> PACKET_MAP = getPacketsMap(Client.class);
        }

        public static class Server {
            public static final PacketType STATUS_REQUEST = new PacketType(SERVERBOUND, 0x00, "Status Request");
            public static final PacketType PING_REQUEST = new PacketType(SERVERBOUND, 0x01, "Ping Request");

            public static final Map<Integer, PacketType> PACKET_MAP = getPacketsMap(Server.class);
        }
    }

    /**
     * Nested class for the Login packet state
     */
    public static class Login {
        public static final Map<PacketFlow, Map<Integer, PacketType>> PACKET_MAP = new HashMap<>();

        static {
            PACKET_MAP.put(CLIENTBOUND, Client.PACKET_MAP);
            PACKET_MAP.put(SERVERBOUND, Server.PACKET_MAP);
        }

        public static class Client {
            public static final PacketType DISCONNECT = new PacketType(CLIENTBOUND, 0x00, "Disconnect (login)");
            public static final PacketType ENCRYPTION_REQUEST = new PacketType(CLIENTBOUND, 0x01, "Encryption Request");
            public static final PacketType LOGIN_SUCCESS = new PacketType(CLIENTBOUND, 0x02, "Login Success");
            public static final PacketType SET_COMPRESSION = new PacketType(CLIENTBOUND, 0x03, "Set Compression");
            public static final PacketType LOGIN_PLUGIN_REQUEST = new PacketType(CLIENTBOUND, 0x04, "Login Plugin Request");

            public static final Map<Integer, PacketType> PACKET_MAP = getPacketsMap(Client.class);
        }

        public static class Server {
            public static final PacketType LOGIN_START = new PacketType(SERVERBOUND, 0x00, "Login Start");
            public static final PacketType ENCRYPTION_RESPONSE = new PacketType(SERVERBOUND, 0x01, "Encryption Response");
            public static final PacketType LOGIN_PLUGIN_RESPONSE = new PacketType(SERVERBOUND, 0x02, "Login Plugin Response");

            public static final Map<Integer, PacketType> PACKET_MAP = getPacketsMap(Server.class);
        }
    }

    /**
     * Nested class for the Play packet state
     */
    public static class Play {
        public static final Map<PacketFlow, Map<Integer, PacketType>> PACKET_MAP = new HashMap<>();

        static {
            PACKET_MAP.put(CLIENTBOUND, Client.PACKET_MAP);
            PACKET_MAP.put(SERVERBOUND, Server.PACKET_MAP);
        }

        public static class Client {
            public static final PacketType BUNDLE_DELIMITER = new PacketType(CLIENTBOUND, 0x00, "Bundle Delimiter");
            public static final PacketType SPAWN_ENTITY = new PacketType(CLIENTBOUND, 0x01, "Spawn Entity");
            public static final PacketType SPAWN_EXPERIENCE_ORB = new PacketType(CLIENTBOUND, 0x02, "Spawn Experience Orb");
            public static final PacketType SPAWN_PLAYER = new PacketType(CLIENTBOUND, 0x03, "Spawn Player");
            public static final PacketType ENTITY_ANIMATION = new PacketType(CLIENTBOUND, 0x04, "Entity Animation");
            public static final PacketType AWARD_STATISTICS = new PacketType(CLIENTBOUND, 0x05, "Award Statistics");
            public static final PacketType ACKNOWLEDGE_BLOCK_CHANGE = new PacketType(CLIENTBOUND, 0x06, "Acknowledge Block Change");
            public static final PacketType SET_BLOCK_DESTROY_STAGE = new PacketType(CLIENTBOUND, 0x07, "Set Block Destroy Stage");
            public static final PacketType BLOCK_ENTITY_DATA = new PacketType(CLIENTBOUND, 0x08, "Block Entity Data");
            public static final PacketType BLOCK_ACTION = new PacketType(CLIENTBOUND, 0x09, "Block Action");
            public static final PacketType BLOCK_UPDATE = new PacketType(CLIENTBOUND, 0x0A, "Block Update");
            public static final PacketType BOSS_BAR = new PacketType(CLIENTBOUND, 0x0B, "Boss Bar");
            public static final PacketType CHANGE_DIFFICULTY = new PacketType(CLIENTBOUND, 0x0C, "Change Difficulty");
            public static final PacketType CHUNK_BIOMES = new PacketType(CLIENTBOUND, 0x0D, "Chunk Biomes");
            public static final PacketType CLEAR_TITLES = new PacketType(CLIENTBOUND, 0x0E, "Clear Titles");
            public static final PacketType COMMAND_SUGGESTIONS_RESPONSE = new PacketType(CLIENTBOUND, 0x0F, "Command Suggestions Response");
            public static final PacketType COMMANDS = new PacketType(CLIENTBOUND, 0x10, "Commands");
            public static final PacketType CLOSE_CONTAINER = new PacketType(CLIENTBOUND, 0x11, "Close Container");
            public static final PacketType SET_CONTAINER_CONTENT = new PacketType(CLIENTBOUND, 0x12, "Set Container Content");
            public static final PacketType SET_CONTAINER_PROPERTY = new PacketType(CLIENTBOUND, 0x13, "Set Container Property");
            public static final PacketType SET_CONTAINER_SLOT = new PacketType(CLIENTBOUND, 0x14, "Set Container Slot");
            public static final PacketType SET_COOLDOWN = new PacketType(CLIENTBOUND, 0x15, "Set Cooldown");
            public static final PacketType CHAT_SUGGESTIONS = new PacketType(CLIENTBOUND, 0x16, "Chat Suggestions");
            public static final PacketType PLUGIN_MESSAGE = new PacketType(CLIENTBOUND, 0x17, "Plugin Message");
            public static final PacketType DAMAGE_EVENT = new PacketType(CLIENTBOUND, 0x18, "Damage Event");
            public static final PacketType DELETE_MESSAGE = new PacketType(CLIENTBOUND, 0x19, "Delete Message");
            public static final PacketType DISCONNECT = new PacketType(CLIENTBOUND, 0x1A, "Disconnect (play)");
            public static final PacketType DISGUISED_CHAT_MESSAGE = new PacketType(CLIENTBOUND, 0x1B, "Disguised Chat Message");
            public static final PacketType ENTITY_EVENT = new PacketType(CLIENTBOUND, 0x1C, "Entity Event");
            public static final PacketType EXPLOSION = new PacketType(CLIENTBOUND, 0x1D, "Explosion");
            public static final PacketType UNLOAD_CHUNK = new PacketType(CLIENTBOUND, 0x1E, "Unload Chunk");
            public static final PacketType GAME_EVENT = new PacketType(CLIENTBOUND, 0x1F, "Game Event");
            public static final PacketType OPEN_HORSE_SCREEN = new PacketType(CLIENTBOUND, 0x20, "Open Horse Screen");
            public static final PacketType HURT_ANIMATION = new PacketType(CLIENTBOUND, 0x21, "Hurt Animation");
            public static final PacketType INITIALIZE_WORLD_BORDER = new PacketType(CLIENTBOUND, 0x22, "Initialize World Border");
            public static final PacketType KEEP_ALIVE = new PacketType(CLIENTBOUND, 0x23, "Keep Alive");
            public static final PacketType CHUNK_DATA_AND_UPDATE_LIGHT = new PacketType(CLIENTBOUND, 0x24, "Chunk Data and Update Light");
            public static final PacketType WORLD_EVENT = new PacketType(CLIENTBOUND, 0x25, "World Event");
            public static final PacketType PARTICLE = new PacketType(CLIENTBOUND, 0x26, "Particle");
            public static final PacketType UPDATE_LIGHT = new PacketType(CLIENTBOUND, 0x27, "Update Light");
            public static final PacketType LOGIN = new PacketType(CLIENTBOUND, 0x28, "Login (play)");
            public static final PacketType MAP_DATA = new PacketType(CLIENTBOUND, 0x29, "Map Data");
            public static final PacketType MERCHANT_OFFERS = new PacketType(CLIENTBOUND, 0x2A, "Merchant Offers");
            public static final PacketType UPDATE_ENTITY_POSITION = new PacketType(CLIENTBOUND, 0x2B, "Update Entity Position");
            public static final PacketType UPDATE_ENTITY_POSITION_AND_ROTATION = new PacketType(CLIENTBOUND, 0x2C, "Update Entity Position and Rotation");
            public static final PacketType UPDATE_ENTITY_ROTATION = new PacketType(CLIENTBOUND, 0x2D, "Update Entity Rotation");
            public static final PacketType MOVE_VEHICLE = new PacketType(CLIENTBOUND, 0x2E, "Move Vehicle");
            public static final PacketType OPEN_BOOK = new PacketType(CLIENTBOUND, 0x2F, "Open Book");
            public static final PacketType OPEN_SCREEN = new PacketType(CLIENTBOUND, 0x30, "Open Screen");
            public static final PacketType OPEN_SIGN_EDITOR = new PacketType(CLIENTBOUND, 0x31, "Open Sign Editor");
            public static final PacketType PING = new PacketType(CLIENTBOUND, 0x32, "Ping (play)");
            public static final PacketType PLACE_GHOST_RECIPE = new PacketType(CLIENTBOUND, 0x33, "Place Ghost Recipe");
            public static final PacketType PLAYER_ABILITIES = new PacketType(CLIENTBOUND, 0x34, "Player Abilities");
            public static final PacketType PLAYER_CHAT_MESSAGE = new PacketType(CLIENTBOUND, 0x35, "Player Chat Message");
            public static final PacketType END_COMBAT = new PacketType(CLIENTBOUND, 0x36, "End Combat");
            public static final PacketType ENTER_COMBAT = new PacketType(CLIENTBOUND, 0x37, "Enter Combat");
            public static final PacketType COMBAT_DEATH = new PacketType(CLIENTBOUND, 0x38, "Combat Death");
            public static final PacketType PLAYER_INFO_REMOVE = new PacketType(CLIENTBOUND, 0x39, "Player Info Remove");
            public static final PacketType PLAYER_INFO_UPDATE = new PacketType(CLIENTBOUND, 0x3A, "Player Info Update");
            public static final PacketType LOOK_AT = new PacketType(CLIENTBOUND, 0x3B, "Look At");
            public static final PacketType SYNCHRONIZE_PLAYER_POSITION = new PacketType(CLIENTBOUND, 0x3C, "Synchronize Player Position");
            public static final PacketType UPDATE_RECIPE_BOOK = new PacketType(CLIENTBOUND, 0x3D, "Update Recipe Book");
            public static final PacketType REMOVE_ENTITIES = new PacketType(CLIENTBOUND, 0x3E, "Remove Entities");
            public static final PacketType REMOVE_ENTITY_EFFECT = new PacketType(CLIENTBOUND, 0x3F, "Remove Entity Effect");
            public static final PacketType RESOURCE_PACK = new PacketType(CLIENTBOUND, 0x40, "Resource Pack");
            public static final PacketType RESPAWN = new PacketType(CLIENTBOUND, 0x41, "Respawn");
            public static final PacketType SET_HEAD_ROTATION = new PacketType(CLIENTBOUND, 0x42, "Set Head Rotation");
            public static final PacketType UPDATE_SECTION_BLOCKS = new PacketType(CLIENTBOUND, 0x43, "Update Section Blocks");
            public static final PacketType SELECT_ADVANCEMENT_TAB = new PacketType(CLIENTBOUND, 0x44, "Select Advancement Tab");
            public static final PacketType SERVER_DATA = new PacketType(CLIENTBOUND, 0x45, "Server Data");
            public static final PacketType SET_ACTION_BAR_TEXT = new PacketType(CLIENTBOUND, 0x46, "Set Action Bar Text");
            public static final PacketType SET_BORDER_CENTER = new PacketType(CLIENTBOUND, 0x47, "Set Border Center");
            public static final PacketType SET_BORDER_LERP_SIZE = new PacketType(CLIENTBOUND, 0x48, "Set Border Lerp Size");
            public static final PacketType SET_BORDER_SIZE = new PacketType(CLIENTBOUND, 0x49, "Set Border Size");
            public static final PacketType SET_BORDER_WARNING_DELAY = new PacketType(CLIENTBOUND, 0x4A, "Set Border Warning Delay");
            public static final PacketType SET_BORDER_WARNING_DISTANCE = new PacketType(CLIENTBOUND, 0x4B, "Set Border Warning Distance");
            public static final PacketType SET_CAMERA = new PacketType(CLIENTBOUND, 0x4C, "Set Camera");
            public static final PacketType SET_HELD_ITEM = new PacketType(CLIENTBOUND, 0x4D, "Set Held Item");
            public static final PacketType SET_CENTER_CHUNK = new PacketType(CLIENTBOUND, 0x4E, "Set Center Chunk");
            public static final PacketType SET_RENDER_DISTANCE = new PacketType(CLIENTBOUND, 0x4F, "Set Render Distance");
            public static final PacketType SET_DEFAULT_SPAWN_POSITION = new PacketType(CLIENTBOUND, 0x50, "Set Default Spawn Position");
            public static final PacketType DISPLAY_OBJECTIVE = new PacketType(CLIENTBOUND, 0x51, "Display Objective");
            public static final PacketType SET_ENTITY_METADATA = new PacketType(CLIENTBOUND, 0x52, "Set Entity Metadata");
            public static final PacketType LINK_ENTITIES = new PacketType(CLIENTBOUND, 0x53, "Link Entities");
            public static final PacketType SET_ENTITY_VELOCITY = new PacketType(CLIENTBOUND, 0x54, "Set Entity Velocity");
            public static final PacketType SET_EQUIPMENT = new PacketType(CLIENTBOUND, 0x55, "Set Equipment");
            public static final PacketType SET_EXPERIENCE = new PacketType(CLIENTBOUND, 0x56, "Set Experience");
            public static final PacketType SET_HEALTH = new PacketType(CLIENTBOUND, 0x57, "Set Health");
            public static final PacketType UPDATE_OBJECTIVES = new PacketType(CLIENTBOUND, 0x58, "Update Objectives");
            public static final PacketType SET_PASSENGERS = new PacketType(CLIENTBOUND, 0x59, "Set Passengers");
            public static final PacketType UPDATE_TEAMS = new PacketType(CLIENTBOUND, 0x5A, "Update Teams");
            public static final PacketType UPDATE_SCORE = new PacketType(CLIENTBOUND, 0x5B, "Update Score");
            public static final PacketType SET_SIMULATION_DISTANCE = new PacketType(CLIENTBOUND, 0x5C, "Set Simulation Distance");
            public static final PacketType SET_SUBTITLE_TEXT = new PacketType(CLIENTBOUND, 0x5D, "Set Subtitle Text");
            public static final PacketType UPDATE_TIME = new PacketType(CLIENTBOUND, 0x5E, "Update Time");
            public static final PacketType SET_TITLE_TEXT = new PacketType(CLIENTBOUND, 0x5F, "Set Title Text");
            public static final PacketType SET_TITLE_ANIMATION_TIMES = new PacketType(CLIENTBOUND, 0x60, "Set Title Animation Times");
            public static final PacketType ENTITY_SOUND_EFFECT = new PacketType(CLIENTBOUND, 0x61, "Entity Sound Effect");
            public static final PacketType SOUND_EFFECT = new PacketType(CLIENTBOUND, 0x62, "Sound Effect");
            public static final PacketType STOP_SOUND = new PacketType(CLIENTBOUND, 0x63, "Stop Sound");
            public static final PacketType SYSTEM_CHAT_MESSAGE = new PacketType(CLIENTBOUND, 0x64, "System Chat Message");
            public static final PacketType SET_TAB_LIST_HEADER_AND_FOOTER = new PacketType(CLIENTBOUND, 0x65, "Set Tab List Header And Footer");
            public static final PacketType TAG_QUERY_RESPONSE = new PacketType(CLIENTBOUND, 0x66, "Tag Query Response");
            public static final PacketType PICKUP_ITEM = new PacketType(CLIENTBOUND, 0x67, "Pickup Item");
            public static final PacketType TELEPORT_ENTITY = new PacketType(CLIENTBOUND, 0x68, "Teleport Entity");
            public static final PacketType UPDATE_ADVANCEMENTS = new PacketType(CLIENTBOUND, 0x69, "Update Advancements");
            public static final PacketType UPDATE_ATTRIBUTES = new PacketType(CLIENTBOUND, 0x6A, "Update Attributes");
            public static final PacketType FEATURE_FLAGS = new PacketType(CLIENTBOUND, 0x6B, "Feature Flags");
            public static final PacketType ENTITY_EFFECT = new PacketType(CLIENTBOUND, 0x6C, "Entity Effect");
            public static final PacketType UPDATE_RECIPES = new PacketType(CLIENTBOUND, 0x6D, "Update Recipes");
            public static final PacketType UPDATE_TAGS = new PacketType(CLIENTBOUND, 0x6E, "Update Tags");

            public static final Map<Integer, PacketType> PACKET_MAP = getPacketsMap(Client.class);
        }

        public static class Server {
            public static final PacketType CONFIRM_TELEPORTATION = new PacketType(SERVERBOUND, 0x00, "Confirm Teleportation");
            public static final PacketType QUERY_BLOCK_ENTITY_TAG = new PacketType(SERVERBOUND, 0x01, "Query Block Entity Tag");
            public static final PacketType CHANGE_DIFFICULTY = new PacketType(SERVERBOUND, 0x02, "Change Difficulty");
            public static final PacketType MESSAGE_ACKNOWLEDGMENT = new PacketType(SERVERBOUND, 0x03, "Message Acknowledgment");
            public static final PacketType CHAT_COMMAND = new PacketType(SERVERBOUND, 0x04, "Chat Command");
            public static final PacketType CHAT_MESSAGE = new PacketType(SERVERBOUND, 0x05, "Chat Message");
            public static final PacketType PLAYER_SESSION = new PacketType(SERVERBOUND, 0x06, "Player Session");
            public static final PacketType CLIENT_COMMAND = new PacketType(SERVERBOUND, 0x07, "Client Command");
            public static final PacketType CLIENT_INFORMATION = new PacketType(SERVERBOUND, 0x08, "Client Information");
            public static final PacketType COMMAND_SUGGESTIONS_REQUEST = new PacketType(SERVERBOUND, 0x09, "Command Suggestions Request");
            public static final PacketType CLICK_CONTAINER_BUTTON = new PacketType(SERVERBOUND, 0x0A, "Click Container Button");
            public static final PacketType CLICK_CONTAINER = new PacketType(SERVERBOUND, 0x0B, "Click Container");
            public static final PacketType CLOSE_CONTAINER = new PacketType(SERVERBOUND, 0x0C, "Close Container");
            public static final PacketType PLUGIN_MESSAGE = new PacketType(SERVERBOUND, 0x0D, "Plugin Message");
            public static final PacketType EDIT_BOOK = new PacketType(SERVERBOUND, 0x0E, "Edit Book");
            public static final PacketType QUERY_ENTITY_TAG = new PacketType(SERVERBOUND, 0x0F, "Query Entity Tag");
            public static final PacketType INTERACT = new PacketType(SERVERBOUND, 0x10, "Interact");
            public static final PacketType JIGSAW_GENERATE = new PacketType(SERVERBOUND, 0x11, "Jigsaw Generate");
            public static final PacketType KEEP_ALIVE = new PacketType(SERVERBOUND, 0x12, "Keep Alive");
            public static final PacketType LOCK_DIFFICULTY = new PacketType(SERVERBOUND, 0x13, "Lock Difficulty");
            public static final PacketType SET_PLAYER_POSITION = new PacketType(SERVERBOUND, 0x14, "Set Player Position");
            public static final PacketType SET_PLAYER_POSITION_AND_ROTATION = new PacketType(SERVERBOUND, 0x15, "Set Player Position and Rotation");
            public static final PacketType SET_PLAYER_ROTATION = new PacketType(SERVERBOUND, 0x16, "Set Player Rotation");
            public static final PacketType SET_PLAYER_ON_GROUND = new PacketType(SERVERBOUND, 0x17, "Set Player On Ground");
            public static final PacketType MOVE_VEHICLE = new PacketType(SERVERBOUND, 0x18, "Move Vehicle");
            public static final PacketType PADDLE_BOAT = new PacketType(SERVERBOUND, 0x19, "Paddle Boat");
            public static final PacketType PICK_ITEM = new PacketType(SERVERBOUND, 0x1A, "Pick Item");
            public static final PacketType PLACE_RECIPE = new PacketType(SERVERBOUND, 0x1B, "Place Recipe");
            public static final PacketType PLAYER_ABILITIES = new PacketType(SERVERBOUND, 0x1C, "Player Abilities");
            public static final PacketType PLAYER_ACTION = new PacketType(SERVERBOUND, 0x1D, "Player Action");
            public static final PacketType PLAYER_COMMAND = new PacketType(SERVERBOUND, 0x1E, "Player Command");
            public static final PacketType PLAYER_INPUT = new PacketType(SERVERBOUND, 0x1F, "Player Input");
            public static final PacketType PONG = new PacketType(SERVERBOUND, 0x20, "Pong (play)");
            public static final PacketType CHANGE_RECIPE_BOOK_SETTINGS = new PacketType(SERVERBOUND, 0x21, "Change Recipe Book Settings");
            public static final PacketType SET_SEEN_RECIPE = new PacketType(SERVERBOUND, 0x22, "Set Seen Recipe");
            public static final PacketType RENAME_ITEM = new PacketType(SERVERBOUND, 0x23, "Rename Item");
            public static final PacketType RESOURCE_PACK = new PacketType(SERVERBOUND, 0x24, "Resource Pack");
            public static final PacketType SEEN_ADVANCEMENTS = new PacketType(SERVERBOUND, 0x25, "Seen Advancements");
            public static final PacketType SELECT_TRADE = new PacketType(SERVERBOUND, 0x26, "Select Trade");
            public static final PacketType SET_BEACON_EFFECT = new PacketType(SERVERBOUND, 0x27, "Set Beacon Effect");
            public static final PacketType SET_HELD_ITEM = new PacketType(SERVERBOUND, 0x28, "Set Held Item");
            public static final PacketType PROGRAM_COMMAND_BLOCK = new PacketType(SERVERBOUND, 0x29, "Program Command Block");
            public static final PacketType PROGRAM_COMMAND_BLOCK_MINECART = new PacketType(SERVERBOUND, 0x2A, "Program Command Block Minecart");
            public static final PacketType SET_CREATIVE_MODE_SLOT = new PacketType(SERVERBOUND, 0x2B, "Set Creative Mode Slot");
            public static final PacketType PROGRAM_JIGSAW_BLOCK = new PacketType(SERVERBOUND, 0x2C, "Program Jigsaw Block");
            public static final PacketType PROGRAM_STRUCTURE_BLOCK = new PacketType(SERVERBOUND, 0x2D, "Program Structure Block");
            public static final PacketType UPDATE_SIGN = new PacketType(SERVERBOUND, 0x2E, "Update Sign");
            public static final PacketType SWING_ARM = new PacketType(SERVERBOUND, 0x2F, "Swing Arm");
            public static final PacketType TELEPORT_TO_ENTITY = new PacketType(SERVERBOUND, 0x30, "Teleport To Entity");
            public static final PacketType USE_ITEM_ON = new PacketType(SERVERBOUND, 0x31, "Use Item On");
            public static final PacketType USE_ITEM = new PacketType(SERVERBOUND, 0x32, "Use Item");

            public static final Map<Integer, PacketType> PACKET_MAP = getPacketsMap(Server.class);
        }
    }
}
