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
 * Represents a registry of play packet types used in the Minecraft
 * server networking.
 *
 * @see PacketType
 * @see PacketRegistry
 * @see <a href="https://wiki.vg/Protocol#Play">Protocol Wiki - Play</a>
 *
 * @version Minecraft {@value SharedConstants#MINECRAFT_VERSION},
 *          Protocol {@value SharedConstants#PROTOCOL_VERSION}
 */
public final class PlayPackets {
    //<editor-fold desc="Play clientbound packets" defaultstate="collapsed">

    public static final PacketType CLIENT_BUNDLE_DELIMITER                    = ofMC(CLIENTBOUND, 0x00, "bundle");                       // Bundle Delimiter
    public static final PacketType CLIENT_SPAWN_ENTITY                        = ofMC(CLIENTBOUND, 0x01, "add_entity");                   // Spawn Entity
    public static final PacketType CLIENT_SPAWN_EXPERIENCE_ORB                = ofMC(CLIENTBOUND, 0x02, "add_experience_orb");           // Spawn Experience Orb
    public static final PacketType CLIENT_ENTITY_ANIMATION                    = ofMC(CLIENTBOUND, 0x03, "animate");                      // Entity Animation
    public static final PacketType CLIENT_AWARD_STATISTICS                    = ofMC(CLIENTBOUND, 0x04, "award_stats");                  // Award Statistics
    public static final PacketType CLIENT_ACKNOWLEDGE_BLOCK_CHANGE            = ofMC(CLIENTBOUND, 0x05, "block_changed_ack");            // Acknowledge Block Change
    public static final PacketType CLIENT_SET_BLOCK_DESTROY_STAGE             = ofMC(CLIENTBOUND, 0x06, "block_destruction");            // Set Block Destroy Stage
    public static final PacketType CLIENT_BLOCK_ENTITY_DATA                   = ofMC(CLIENTBOUND, 0x07, "block_entity_data");            // Block Entity Data
    public static final PacketType CLIENT_BLOCK_ACTION                        = ofMC(CLIENTBOUND, 0x08, "block_event");                  // Block Action
    public static final PacketType CLIENT_BLOCK_UPDATE                        = ofMC(CLIENTBOUND, 0x09, "block_update");                 // Block Update
    public static final PacketType CLIENT_BOSS_BAR                            = ofMC(CLIENTBOUND, 0x0A, "boss_event");                   // Boss Bar
    public static final PacketType CLIENT_CHANGE_DIFFICULTY                   = ofMC(CLIENTBOUND, 0x0B, "change_difficulty");            // Change Difficulty
    public static final PacketType CLIENT_CHUNK_BATCH_FINISHED                = ofMC(CLIENTBOUND, 0x0C, "chunk_batch_finished");         // Chunk Batch Finished
    public static final PacketType CLIENT_CHUNK_BATCH_START                   = ofMC(CLIENTBOUND, 0x0D, "chunk_batch_start");            // Chunk Batch Start
    public static final PacketType CLIENT_CHUNK_BIOMES                        = ofMC(CLIENTBOUND, 0x0E, "chunks_biomes");                // Chunk Biomes
    public static final PacketType CLIENT_CLEAR_TITLES                        = ofMC(CLIENTBOUND, 0x0F, "clear_titles");                 // Clear Titles
    public static final PacketType CLIENT_COMMAND_SUGGESTIONS_RESPONSE        = ofMC(CLIENTBOUND, 0x10, "command_suggestions");          // Command Suggestions Response
    public static final PacketType CLIENT_COMMANDS                            = ofMC(CLIENTBOUND, 0x11, "commands");                     // Commands
    public static final PacketType CLIENT_CLOSE_CONTAINER                     = ofMC(CLIENTBOUND, 0x12, "container_close");              // Close Container
    public static final PacketType CLIENT_SET_CONTAINER_CONTENT               = ofMC(CLIENTBOUND, 0x13, "container_set_content");        // Set Container Content
    public static final PacketType CLIENT_SET_CONTAINER_PROPERTY              = ofMC(CLIENTBOUND, 0x14, "container_set_data");           // Set Container Property
    public static final PacketType CLIENT_SET_CONTAINER_SLOT                  = ofMC(CLIENTBOUND, 0x15, "container_set_slot");           // Set Container Slot
    public static final PacketType CLIENT_COOKIE_REQUEST                      = ofMC(CLIENTBOUND, 0x16, "cookie_request");               // Cookie Request (play)
    public static final PacketType CLIENT_SET_COOLDOWN                        = ofMC(CLIENTBOUND, 0x17, "cooldown");                     // Set Cooldown
    public static final PacketType CLIENT_CHAT_SUGGESTIONS                    = ofMC(CLIENTBOUND, 0x18, "custom_chat_completions");      // Chat Suggestions
    public static final PacketType CLIENT_PLUGIN_MESSAGE                      = ofMC(CLIENTBOUND, 0x19, "custom_payload");               // Clientbound Plugin Message (play)
    public static final PacketType CLIENT_DAMAGE_EVENT                        = ofMC(CLIENTBOUND, 0x1A, "damage_event");                 // Damage Event
    public static final PacketType CLIENT_DEBUG_SAMPLE                        = ofMC(CLIENTBOUND, 0x1B, "debug_sample");                 // Debug Sample
    public static final PacketType CLIENT_DELETE_MESSAGE                      = ofMC(CLIENTBOUND, 0x1C, "delete_chat");                  // Delete Message
    public static final PacketType CLIENT_DISCONNECT                          = ofMC(CLIENTBOUND, 0x1D, "disconnect");                   // Disconnect (play)
    public static final PacketType CLIENT_DISGUISED_CHAT_MESSAGE              = ofMC(CLIENTBOUND, 0x1E, "disguised_chat");               // Disguised Chat Message
    public static final PacketType CLIENT_ENTITY_EVENT                        = ofMC(CLIENTBOUND, 0x1F, "entity_event");                 // Entity Event
    public static final PacketType CLIENT_EXPLOSION                           = ofMC(CLIENTBOUND, 0x20, "explode");                      // Explosion
    public static final PacketType CLIENT_UNLOAD_CHUNK                        = ofMC(CLIENTBOUND, 0x21, "forget_level_chunk");           // Unload Chunk
    public static final PacketType CLIENT_GAME_EVENT                          = ofMC(CLIENTBOUND, 0x22, "game_event");                   // Game Event
    public static final PacketType CLIENT_OPEN_HORSE_SCREEN                   = ofMC(CLIENTBOUND, 0x23, "horse_screen_open");            // Open Horse Screen
    public static final PacketType CLIENT_HURT_ANIMATION                      = ofMC(CLIENTBOUND, 0x24, "hurt_animation");               // Hurt Animation
    public static final PacketType CLIENT_INITIALIZE_WORLD_BORDER             = ofMC(CLIENTBOUND, 0x25, "initialize_border");            // Initialize World Border
    public static final PacketType CLIENT_KEEP_ALIVE                          = ofMC(CLIENTBOUND, 0x26, "keep_alive");                   // Clientbound Keep Alive (play)
    public static final PacketType CLIENT_CHUNK_DATA_AND_UPDATE_LIGHT         = ofMC(CLIENTBOUND, 0x27, "level_chunk_with_light");       // Chunk Data and Update Light
    public static final PacketType CLIENT_WORLD_EVENT                         = ofMC(CLIENTBOUND, 0x28, "level_event");                  // World Event
    public static final PacketType CLIENT_PARTICLE                            = ofMC(CLIENTBOUND, 0x29, "level_particles");              // Particle
    public static final PacketType CLIENT_UPDATE_LIGHT                        = ofMC(CLIENTBOUND, 0x2A, "light_update");                 // Update Light
    public static final PacketType CLIENT_LOGIN                               = ofMC(CLIENTBOUND, 0x2B, "login");                        // Login (play)
    public static final PacketType CLIENT_MAP_DATA                            = ofMC(CLIENTBOUND, 0x2C, "map_item_data");                // Map Data
    public static final PacketType CLIENT_MERCHANT_OFFERS                     = ofMC(CLIENTBOUND, 0x2D, "merchant_offers");              // Merchant Offers
    public static final PacketType CLIENT_UPDATE_ENTITY_POSITION              = ofMC(CLIENTBOUND, 0x2E, "move_entity_pos");              // Update Entity Position
    public static final PacketType CLIENT_UPDATE_ENTITY_POSITION_AND_ROTATION = ofMC(CLIENTBOUND, 0x2F, "move_entity_pos_rot");          // Update Entity Position and Rotation
    public static final PacketType CLIENT_UPDATE_ENTITY_ROTATION              = ofMC(CLIENTBOUND, 0x30, "move_entity_rot");              // Update Entity Rotation
    public static final PacketType CLIENT_MOVE_VEHICLE                        = ofMC(CLIENTBOUND, 0x31, "move_vehicle");                 // Move Vehicle
    public static final PacketType CLIENT_OPEN_BOOK                           = ofMC(CLIENTBOUND, 0x32, "open_book");                    // Open Book
    public static final PacketType CLIENT_OPEN_SCREEN                         = ofMC(CLIENTBOUND, 0x33, "open_screen");                  // Open Screen
    public static final PacketType CLIENT_OPEN_SIGN_EDITOR                    = ofMC(CLIENTBOUND, 0x34, "open_sign_editor");             // Open Sign Editor
    public static final PacketType CLIENT_PING                                = ofMC(CLIENTBOUND, 0x35, "ping");                         // Ping (play)
    public static final PacketType CLIENT_PING_RESPONSE                       = ofMC(CLIENTBOUND, 0x36, "pong_response");                // Ping Response (play)
    public static final PacketType CLIENT_PLACE_GHOST_RECIPE                  = ofMC(CLIENTBOUND, 0x37, "place_ghost_recipe");           // Place Ghost Recipe
    public static final PacketType CLIENT_PLAYER_ABILITIES                    = ofMC(CLIENTBOUND, 0x38, "player_abilities");             // Player Abilities (clientbound)
    public static final PacketType CLIENT_PLAYER_CHAT_MESSAGE                 = ofMC(CLIENTBOUND, 0x39, "player_chat");                  // Player Chat Message
    public static final PacketType CLIENT_END_COMBAT                          = ofMC(CLIENTBOUND, 0x3A, "player_combat_end");            // End Combat
    public static final PacketType CLIENT_ENTER_COMBAT                        = ofMC(CLIENTBOUND, 0x3B, "player_combat_enter");          // Enter Combat
    public static final PacketType CLIENT_COMBAT_DEATH                        = ofMC(CLIENTBOUND, 0x3C, "player_combat_kill");           // Combat Death
    public static final PacketType CLIENT_PLAYER_INFO_REMOVE                  = ofMC(CLIENTBOUND, 0x3D, "player_info_remove");           // Player Info Remove
    public static final PacketType CLIENT_PLAYER_INFO_UPDATE                  = ofMC(CLIENTBOUND, 0x3E, "player_info_update");           // Player Info Update
    public static final PacketType CLIENT_LOOK_AT                             = ofMC(CLIENTBOUND, 0x3F, "player_look_at");               // Look At
    public static final PacketType CLIENT_SYNCHRONIZE_PLAYER_POSITION         = ofMC(CLIENTBOUND, 0x40, "player_position");              // Synchronize Player Position
    public static final PacketType CLIENT_UPDATE_RECIPE_BOOK                  = ofMC(CLIENTBOUND, 0x41, "recipe");                       // Update Recipe Book
    public static final PacketType CLIENT_REMOVE_ENTITIES                     = ofMC(CLIENTBOUND, 0x42, "remove_entities");              // Remove Entities
    public static final PacketType CLIENT_REMOVE_ENTITY_EFFECT                = ofMC(CLIENTBOUND, 0x43, "remove_mob_effect");            // Remove Entity Effect
    public static final PacketType CLIENT_RESET_SCORE                         = ofMC(CLIENTBOUND, 0x44, "reset_score");                  // Reset Score
    public static final PacketType CLIENT_REMOVE_RESOURCE_PACK                = ofMC(CLIENTBOUND, 0x45, "resource_pack_pop");            // Remove Resource Pack (play)
    public static final PacketType CLIENT_ADD_RESOURCE_PACK                   = ofMC(CLIENTBOUND, 0x46, "resource_pack_push");           // Add Resource Pack (play)
    public static final PacketType CLIENT_RESPAWN                             = ofMC(CLIENTBOUND, 0x47, "respawn");                      // Respawn
    public static final PacketType CLIENT_SET_HEAD_ROTATION                   = ofMC(CLIENTBOUND, 0x48, "rotate_head");                  // Set Head Rotation
    public static final PacketType CLIENT_UPDATE_SECTION_BLOCKS               = ofMC(CLIENTBOUND, 0x49, "section_blocks_update");        // Update Section Blocks
    public static final PacketType CLIENT_SELECT_ADVANCEMENT_TAB              = ofMC(CLIENTBOUND, 0x4A, "select_advancements_tab");      // Select Advancement Tab
    public static final PacketType CLIENT_SERVER_DATA                         = ofMC(CLIENTBOUND, 0x4B, "server_data");                  // Server Data
    public static final PacketType CLIENT_SET_ACTION_BAR_TEXT                 = ofMC(CLIENTBOUND, 0x4C, "set_action_bar_text");          // Set Action Bar Text
    public static final PacketType CLIENT_SET_BORDER_CENTER                   = ofMC(CLIENTBOUND, 0x4D, "set_border_center");            // Set Border Center
    public static final PacketType CLIENT_SET_BORDER_LERP_SIZE                = ofMC(CLIENTBOUND, 0x4E, "set_border_lerp_size");         // Set Border Lerp Size
    public static final PacketType CLIENT_SET_BORDER_SIZE                     = ofMC(CLIENTBOUND, 0x4F, "set_border_size");              // Set Border Size
    public static final PacketType CLIENT_SET_BORDER_WARNING_DELAY            = ofMC(CLIENTBOUND, 0x50, "set_border_warning_delay");     // Set Border Warning Delay
    public static final PacketType CLIENT_SET_BORDER_WARNING_DISTANCE         = ofMC(CLIENTBOUND, 0x51, "set_border_warning_distance");  // Set Border Warning Distance
    public static final PacketType CLIENT_SET_CAMERA                          = ofMC(CLIENTBOUND, 0x52, "set_camera");                   // Set Camera
    public static final PacketType CLIENT_SET_HELD_ITEM                       = ofMC(CLIENTBOUND, 0x53, "set_carried_item");             // Set Held Item (clientbound)
    public static final PacketType CLIENT_SET_CENTER_CHUNK                    = ofMC(CLIENTBOUND, 0x54, "set_chunk_cache_center");       // Set Center Chunk
    public static final PacketType CLIENT_SET_RENDER_DISTANCE                 = ofMC(CLIENTBOUND, 0x55, "set_chunk_cache_radius");       // Set Render Distance
    public static final PacketType CLIENT_SET_DEFAULT_SPAWN_POSITION          = ofMC(CLIENTBOUND, 0x56, "set_default_spawn_position");   // Set Default Spawn Position
    public static final PacketType CLIENT_DISPLAY_OBJECTIVE                   = ofMC(CLIENTBOUND, 0x57, "set_display_objective");        // Display Objective
    public static final PacketType CLIENT_SET_ENTITY_METADATA                 = ofMC(CLIENTBOUND, 0x58, "set_entity_data");              // Set Entity Metadata
    public static final PacketType CLIENT_LINK_ENTITIES                       = ofMC(CLIENTBOUND, 0x59, "set_entity_link");              // Link Entities
    public static final PacketType CLIENT_SET_ENTITY_VELOCITY                 = ofMC(CLIENTBOUND, 0x5A, "set_entity_motion");            // Set Entity Velocity
    public static final PacketType CLIENT_SET_EQUIPMENT                       = ofMC(CLIENTBOUND, 0x5B, "set_equipment");                // Set Equipment
    public static final PacketType CLIENT_SET_EXPERIENCE                      = ofMC(CLIENTBOUND, 0x5C, "set_experience");               // Set Experience
    public static final PacketType CLIENT_SET_HEALTH                          = ofMC(CLIENTBOUND, 0x5D, "set_health");                   // Set Health
    public static final PacketType CLIENT_UPDATE_OBJECTIVES                   = ofMC(CLIENTBOUND, 0x5E, "set_objective");                // Update Objectives
    public static final PacketType CLIENT_SET_PASSENGERS                      = ofMC(CLIENTBOUND, 0x5F, "set_passengers");               // Set Passengers
    public static final PacketType CLIENT_UPDATE_TEAMS                        = ofMC(CLIENTBOUND, 0x60, "set_player_team");              // Update Teams
    public static final PacketType CLIENT_UPDATE_SCORE                        = ofMC(CLIENTBOUND, 0x61, "set_score");                    // Update Score
    public static final PacketType CLIENT_SET_SIMULATION_DISTANCE             = ofMC(CLIENTBOUND, 0x62, "set_simulation_distance");      // Set Simulation Distance
    public static final PacketType CLIENT_SET_SUBTITLE_TEXT                   = ofMC(CLIENTBOUND, 0x63, "set_subtitle_text");            // Set Subtitle Text
    public static final PacketType CLIENT_UPDATE_TIME                         = ofMC(CLIENTBOUND, 0x64, "set_time");                     // Update Time
    public static final PacketType CLIENT_SET_TITLE_TEXT                      = ofMC(CLIENTBOUND, 0x65, "set_title_text");               // Set Title Text
    public static final PacketType CLIENT_SET_TITLE_ANIMATION_TIMES           = ofMC(CLIENTBOUND, 0x66, "set_titles_animation");         // Set Title Animation Times
    public static final PacketType CLIENT_ENTITY_SOUND_EFFECT                 = ofMC(CLIENTBOUND, 0x67, "sound_entity");                 // Entity Sound Effect
    public static final PacketType CLIENT_SOUND_EFFECT                        = ofMC(CLIENTBOUND, 0x68, "sound");                        // Sound Effect
    public static final PacketType CLIENT_START_CONFIGURATION                 = ofMC(CLIENTBOUND, 0x69, "start_configuration");          // Start Configuration
    public static final PacketType CLIENT_STOP_SOUND                          = ofMC(CLIENTBOUND, 0x6A, "stop_sound");                   // Stop Sound
    public static final PacketType CLIENT_STORE_COOKIE                        = ofMC(CLIENTBOUND, 0x6B, "store_cookie");                 // Store Cookie (play)
    public static final PacketType CLIENT_SYSTEM_CHAT_MESSAGE                 = ofMC(CLIENTBOUND, 0x6C, "system_chat");                  // System Chat Message
    public static final PacketType CLIENT_SET_TAB_LIST_HEADER_AND_FOOTER      = ofMC(CLIENTBOUND, 0x6D, "tab_list");                     // Set Tab List Header And Footer
    public static final PacketType CLIENT_TAG_QUERY_RESPONSE                  = ofMC(CLIENTBOUND, 0x6E, "tag_query");                    // Tag Query Response
    public static final PacketType CLIENT_PICKUP_ITEM                         = ofMC(CLIENTBOUND, 0x6F, "take_item_entity");             // Pickup Item
    public static final PacketType CLIENT_TELEPORT_ENTITY                     = ofMC(CLIENTBOUND, 0x70, "teleport_entity");              // Teleport Entity
    public static final PacketType CLIENT_SET_TICKING_STATE                   = ofMC(CLIENTBOUND, 0x71, "ticking_state");                // Set Ticking State
    public static final PacketType CLIENT_STEP_TICK                           = ofMC(CLIENTBOUND, 0x72, "ticking_step");                 // Step Tick
    public static final PacketType CLIENT_TRANSFER                            = ofMC(CLIENTBOUND, 0x73, "transfer");                     // Transfer (play)
    public static final PacketType CLIENT_UPDATE_ADVANCEMENTS                 = ofMC(CLIENTBOUND, 0x74, "update_advancements");          // Update Advancements
    public static final PacketType CLIENT_UPDATE_ATTRIBUTES                   = ofMC(CLIENTBOUND, 0x75, "update_attributes");            // Update Attributes
    public static final PacketType CLIENT_ENTITY_EFFECT                       = ofMC(CLIENTBOUND, 0x76, "update_mob_effect");            // Entity Effect
    public static final PacketType CLIENT_UPDATE_RECIPES                      = ofMC(CLIENTBOUND, 0x77, "update_recipes");               // Update Recipes
    public static final PacketType CLIENT_UPDATE_TAGS                         = ofMC(CLIENTBOUND, 0x78, "update_tags");                  // Update Tags (play)
    public static final PacketType CLIENT_PROJECTILE_POWER                    = ofMC(CLIENTBOUND, 0x79, "projectile_power");             // Projectile Power
    public static final PacketType CLIENT_CUSTOM_REPORT_DETAILS               = ofMC(CLIENTBOUND, 0x7A, "custom_report_details");        // Custom Report Details
    public static final PacketType CLIENT_SERVER_LINKS                        = ofMC(CLIENTBOUND, 0x7B, "server_links");                 // Server Links (play)

    //</editor-fold>
    //<editor-fold desc="Play serverbound packets" defaultstate="collapsed">

    public static final PacketType SERVER_CONFIRM_TELEPORTATION               = ofMC(SERVERBOUND, 0x00, "accept_teleportation");         // Confirm Teleportation
    public static final PacketType SERVER_QUERY_BLOCK_ENTITY_TAG              = ofMC(SERVERBOUND, 0x01, "block_entity_tag_query");       // Query Block Entity Tag
    public static final PacketType SERVER_CHANGE_DIFFICULTY                   = ofMC(SERVERBOUND, 0x02, "change_difficulty");            // Change Difficulty
    public static final PacketType SERVER_ACKNOWLEDGE_MESSAGE                 = ofMC(SERVERBOUND, 0x03, "chat_ack");                     // Acknowledge Message
    public static final PacketType SERVER_CHAT_COMMAND                        = ofMC(SERVERBOUND, 0x04, "chat_command");                 // Chat Command
    public static final PacketType SERVER_SIGNED_CHAT_COMMAND                 = ofMC(SERVERBOUND, 0x05, "chat_command_signed");          // Signed Chat Command
    public static final PacketType SERVER_CHAT_MESSAGE                        = ofMC(SERVERBOUND, 0x06, "chat");                         // Chat Message
    public static final PacketType SERVER_PLAYER_SESSION                      = ofMC(SERVERBOUND, 0x07, "chat_session_update");          // Player Session
    public static final PacketType SERVER_CHUNK_BATCH_RECEIVED                = ofMC(SERVERBOUND, 0x08, "chunk_batch_received");         // Chunk Batch Received
    public static final PacketType SERVER_CLIENT_STATUS                       = ofMC(SERVERBOUND, 0x09, "client_command");               // Client Status
    public static final PacketType SERVER_CLIENT_INFORMATION                  = ofMC(SERVERBOUND, 0x0A, "client_information");           // Client Information (play)
    public static final PacketType SERVER_COMMAND_SUGGESTIONS_REQUEST         = ofMC(SERVERBOUND, 0x0B, "command_suggestion");           // Command Suggestions Request
    public static final PacketType SERVER_ACKNOWLEDGE_CONFIGURATION           = ofMC(SERVERBOUND, 0x0C, "configuration_acknowledged");   // Acknowledge Configuration
    public static final PacketType SERVER_CLICK_CONTAINER_BUTTON              = ofMC(SERVERBOUND, 0x0D, "container_button_click");       // Click Container Button
    public static final PacketType SERVER_CLICK_CONTAINER                     = ofMC(SERVERBOUND, 0x0E, "container_click");              // Click Container
    public static final PacketType SERVER_CLOSE_CONTAINER                     = ofMC(SERVERBOUND, 0x0F, "container_close");              // Close Container
    public static final PacketType SERVER_CHANGE_CONTAINER_SLOT_STATE         = ofMC(SERVERBOUND, 0x10, "container_slot_state_changed"); // Change Container Slot State
    public static final PacketType SERVER_COOKIE_RESPONSE                     = ofMC(SERVERBOUND, 0x11, "cookie_response");              // Cookie Response (play)
    public static final PacketType SERVER_PLUGIN_MESSAGE                      = ofMC(SERVERBOUND, 0x12, "custom_payload");               // Serverbound Plugin Message (play)
    public static final PacketType SERVER_DEBUG_SAMPLE_SUBSCRIPTION           = ofMC(SERVERBOUND, 0x13, "debug_sample_subscription");    // Debug Sample Subscription
    public static final PacketType SERVER_EDIT_BOOK                           = ofMC(SERVERBOUND, 0x14, "edit_book");                    // Edit Book
    public static final PacketType SERVER_QUERY_ENTITY_TAG                    = ofMC(SERVERBOUND, 0x15, "entity_tag_query");             // Query Entity Tag
    public static final PacketType SERVER_INTERACT                            = ofMC(SERVERBOUND, 0x16, "interact");                     // Interact
    public static final PacketType SERVER_JIGSAW_GENERATE                     = ofMC(SERVERBOUND, 0x17, "jigsaw_generate");              // Jigsaw Generate
    public static final PacketType SERVER_KEEP_ALIVE                          = ofMC(SERVERBOUND, 0x18, "keep_alive");                   // Serverbound Keep Alive (play)
    public static final PacketType SERVER_LOCK_DIFFICULTY                     = ofMC(SERVERBOUND, 0x19, "lock_difficulty");              // Lock Difficulty
    public static final PacketType SERVER_SET_PLAYER_POSITION                 = ofMC(SERVERBOUND, 0x1A, "move_player_pos");              // Set Player Position
    public static final PacketType SERVER_SET_PLAYER_POSITION_AND_ROTATION    = ofMC(SERVERBOUND, 0x1B, "move_player_pos_rot");          // Set Player Position and Rotation
    public static final PacketType SERVER_SET_PLAYER_ROTATION                 = ofMC(SERVERBOUND, 0x1C, "move_player_rot");              // Set Player Rotation
    public static final PacketType SERVER_SET_PLAYER_ON_GROUND                = ofMC(SERVERBOUND, 0x1D, "move_player_status_only");      // Set Player On Ground
    public static final PacketType SERVER_MOVE_VEHICLE                        = ofMC(SERVERBOUND, 0x1E, "move_vehicle");                 // Move Vehicle
    public static final PacketType SERVER_PADDLE_BOAT                         = ofMC(SERVERBOUND, 0x1F, "paddle_boat");                  // Paddle Boat
    public static final PacketType SERVER_PICK_ITEM                           = ofMC(SERVERBOUND, 0x20, "pick_item");                    // Pick Item
    public static final PacketType SERVER_PING_REQUEST                        = ofMC(SERVERBOUND, 0x21, "ping_request");                 // Ping Request (play)
    public static final PacketType SERVER_PLACE_RECIPE                        = ofMC(SERVERBOUND, 0x22, "place_recipe");                 // Place Recipe
    public static final PacketType SERVER_PLAYER_ABILITIES                    = ofMC(SERVERBOUND, 0x23, "player_abilities");             // Player Abilities (serverbound)
    public static final PacketType SERVER_PLAYER_ACTION                       = ofMC(SERVERBOUND, 0x24, "player_action");                // Player Action
    public static final PacketType SERVER_PLAYER_COMMAND                      = ofMC(SERVERBOUND, 0x25, "player_command");               // Player Command
    public static final PacketType SERVER_PLAYER_INPUT                        = ofMC(SERVERBOUND, 0x26, "player_input");                 // Player Input
    public static final PacketType SERVER_PONG                                = ofMC(SERVERBOUND, 0x27, "pong");                         // Pong (play)
    public static final PacketType SERVER_CHANGE_RECIPE_BOOK_SETTINGS         = ofMC(SERVERBOUND, 0x28, "recipe_book_change_settings");  // Change Recipe Book Settings
    public static final PacketType SERVER_SET_SEEN_RECIPE                     = ofMC(SERVERBOUND, 0x29, "recipe_book_seen_recipe");      // Set Seen Recipe
    public static final PacketType SERVER_RENAME_ITEM                         = ofMC(SERVERBOUND, 0x2A, "rename_item");                  // Rename Item
    public static final PacketType SERVER_RESOURCE_PACK_RESPONSE              = ofMC(SERVERBOUND, 0x2B, "resource_pack");                // Resource Pack Response (play)
    public static final PacketType SERVER_SEEN_ADVANCEMENTS                   = ofMC(SERVERBOUND, 0x2C, "seen_advancements");            // Seen Advancements
    public static final PacketType SERVER_SELECT_TRADE                        = ofMC(SERVERBOUND, 0x2D, "select_trade");                 // Select Trade
    public static final PacketType SERVER_SET_BEACON_EFFECT                   = ofMC(SERVERBOUND, 0x2E, "set_beacon");                   // Set Beacon Effect
    public static final PacketType SERVER_SET_HELD_ITEM                       = ofMC(SERVERBOUND, 0x2F, "set_carried_item");             // Set Held Item (serverbound)
    public static final PacketType SERVER_PROGRAM_COMMAND_BLOCK               = ofMC(SERVERBOUND, 0x30, "set_command_block");            // Program Command Block
    public static final PacketType SERVER_PROGRAM_COMMAND_BLOCK_MINECART      = ofMC(SERVERBOUND, 0x31, "set_command_minecart");         // Program Command Block Minecart
    public static final PacketType SERVER_SET_CREATIVE_MODE_SLOT              = ofMC(SERVERBOUND, 0x32, "set_creative_mode_slot");       // Set Creative Mode Slot
    public static final PacketType SERVER_PROGRAM_JIGSAW_BLOCK                = ofMC(SERVERBOUND, 0x33, "set_jigsaw_block");             // Program Jigsaw Block
    public static final PacketType SERVER_PROGRAM_STRUCTURE_BLOCK             = ofMC(SERVERBOUND, 0x34, "set_structure_block");          // Program Structure Block
    public static final PacketType SERVER_UPDATE_SIGN                         = ofMC(SERVERBOUND, 0x35, "sign_update");                  // Update Sign
    public static final PacketType SERVER_SWING_ARM                           = ofMC(SERVERBOUND, 0x36, "swing");                        // Swing Arm
    public static final PacketType SERVER_TELEPORT_TO_ENTITY                  = ofMC(SERVERBOUND, 0x37, "teleport_to_entity");           // Teleport To Entity
    public static final PacketType SERVER_USE_ITEM_ON                         = ofMC(SERVERBOUND, 0x38, "use_item_on");                  // Use Item On
    public static final PacketType SERVER_USE_ITEM                            = ofMC(SERVERBOUND, 0x39, "use_item");                     // Use Item

    //</editor-fold>

    private static final PacketRegistry REGISTRY =
            PacketRegistry.create(
                    PacketMap.key2PacketBuilder()
                             .add(
                                     //<editor-fold desc="Clientbound packets" defaultstate="collapsed">

                                     CLIENT_BUNDLE_DELIMITER,
                                     CLIENT_SPAWN_ENTITY,
                                     CLIENT_SPAWN_EXPERIENCE_ORB,
                                     CLIENT_ENTITY_ANIMATION,
                                     CLIENT_AWARD_STATISTICS,
                                     CLIENT_ACKNOWLEDGE_BLOCK_CHANGE,
                                     CLIENT_SET_BLOCK_DESTROY_STAGE,
                                     CLIENT_BLOCK_ENTITY_DATA,
                                     CLIENT_BLOCK_ACTION,
                                     CLIENT_BLOCK_UPDATE,
                                     CLIENT_BOSS_BAR,
                                     CLIENT_CHANGE_DIFFICULTY,
                                     CLIENT_CHUNK_BATCH_FINISHED,
                                     CLIENT_CHUNK_BATCH_START,
                                     CLIENT_CHUNK_BIOMES,
                                     CLIENT_CLEAR_TITLES,
                                     CLIENT_COMMAND_SUGGESTIONS_RESPONSE,
                                     CLIENT_COMMANDS,
                                     CLIENT_CLOSE_CONTAINER,
                                     CLIENT_SET_CONTAINER_CONTENT,
                                     CLIENT_SET_CONTAINER_PROPERTY,
                                     CLIENT_SET_CONTAINER_SLOT,
                                     CLIENT_COOKIE_REQUEST,
                                     CLIENT_SET_COOLDOWN,
                                     CLIENT_CHAT_SUGGESTIONS,
                                     CLIENT_PLUGIN_MESSAGE,
                                     CLIENT_DAMAGE_EVENT,
                                     CLIENT_DEBUG_SAMPLE,
                                     CLIENT_DELETE_MESSAGE,
                                     CLIENT_DISCONNECT,
                                     CLIENT_DISGUISED_CHAT_MESSAGE,
                                     CLIENT_ENTITY_EVENT,
                                     CLIENT_EXPLOSION,
                                     CLIENT_UNLOAD_CHUNK,
                                     CLIENT_GAME_EVENT,
                                     CLIENT_OPEN_HORSE_SCREEN,
                                     CLIENT_HURT_ANIMATION,
                                     CLIENT_INITIALIZE_WORLD_BORDER,
                                     CLIENT_KEEP_ALIVE,
                                     CLIENT_CHUNK_DATA_AND_UPDATE_LIGHT,
                                     CLIENT_WORLD_EVENT,
                                     CLIENT_PARTICLE,
                                     CLIENT_UPDATE_LIGHT,
                                     CLIENT_LOGIN,
                                     CLIENT_MAP_DATA,
                                     CLIENT_MERCHANT_OFFERS,
                                     CLIENT_UPDATE_ENTITY_POSITION,
                                     CLIENT_UPDATE_ENTITY_POSITION_AND_ROTATION,
                                     CLIENT_UPDATE_ENTITY_ROTATION,
                                     CLIENT_MOVE_VEHICLE,
                                     CLIENT_OPEN_BOOK,
                                     CLIENT_OPEN_SCREEN,
                                     CLIENT_OPEN_SIGN_EDITOR,
                                     CLIENT_PING,
                                     CLIENT_PING_RESPONSE,
                                     CLIENT_PLACE_GHOST_RECIPE,
                                     CLIENT_PLAYER_ABILITIES,
                                     CLIENT_PLAYER_CHAT_MESSAGE,
                                     CLIENT_END_COMBAT,
                                     CLIENT_ENTER_COMBAT,
                                     CLIENT_COMBAT_DEATH,
                                     CLIENT_PLAYER_INFO_REMOVE,
                                     CLIENT_PLAYER_INFO_UPDATE,
                                     CLIENT_LOOK_AT,
                                     CLIENT_SYNCHRONIZE_PLAYER_POSITION,
                                     CLIENT_UPDATE_RECIPE_BOOK,
                                     CLIENT_REMOVE_ENTITIES,
                                     CLIENT_REMOVE_ENTITY_EFFECT,
                                     CLIENT_RESET_SCORE,
                                     CLIENT_REMOVE_RESOURCE_PACK,
                                     CLIENT_ADD_RESOURCE_PACK,
                                     CLIENT_RESPAWN,
                                     CLIENT_SET_HEAD_ROTATION,
                                     CLIENT_UPDATE_SECTION_BLOCKS,
                                     CLIENT_SELECT_ADVANCEMENT_TAB,
                                     CLIENT_SERVER_DATA,
                                     CLIENT_SET_ACTION_BAR_TEXT,
                                     CLIENT_SET_BORDER_CENTER,
                                     CLIENT_SET_BORDER_LERP_SIZE,
                                     CLIENT_SET_BORDER_SIZE,
                                     CLIENT_SET_BORDER_WARNING_DELAY,
                                     CLIENT_SET_BORDER_WARNING_DISTANCE,
                                     CLIENT_SET_CAMERA,
                                     CLIENT_SET_HELD_ITEM,
                                     CLIENT_SET_CENTER_CHUNK,
                                     CLIENT_SET_RENDER_DISTANCE,
                                     CLIENT_SET_DEFAULT_SPAWN_POSITION,
                                     CLIENT_DISPLAY_OBJECTIVE,
                                     CLIENT_SET_ENTITY_METADATA,
                                     CLIENT_LINK_ENTITIES,
                                     CLIENT_SET_ENTITY_VELOCITY,
                                     CLIENT_SET_EQUIPMENT,
                                     CLIENT_SET_EXPERIENCE,
                                     CLIENT_SET_HEALTH,
                                     CLIENT_UPDATE_OBJECTIVES,
                                     CLIENT_SET_PASSENGERS,
                                     CLIENT_UPDATE_TEAMS,
                                     CLIENT_UPDATE_SCORE,
                                     CLIENT_SET_SIMULATION_DISTANCE,
                                     CLIENT_SET_SUBTITLE_TEXT,
                                     CLIENT_UPDATE_TIME,
                                     CLIENT_SET_TITLE_TEXT,
                                     CLIENT_SET_TITLE_ANIMATION_TIMES,
                                     CLIENT_ENTITY_SOUND_EFFECT,
                                     CLIENT_SOUND_EFFECT,
                                     CLIENT_START_CONFIGURATION,
                                     CLIENT_STOP_SOUND,
                                     CLIENT_STORE_COOKIE,
                                     CLIENT_SYSTEM_CHAT_MESSAGE,
                                     CLIENT_SET_TAB_LIST_HEADER_AND_FOOTER,
                                     CLIENT_TAG_QUERY_RESPONSE,
                                     CLIENT_PICKUP_ITEM,
                                     CLIENT_TELEPORT_ENTITY,
                                     CLIENT_SET_TICKING_STATE,
                                     CLIENT_STEP_TICK,
                                     CLIENT_TRANSFER,
                                     CLIENT_UPDATE_ADVANCEMENTS,
                                     CLIENT_UPDATE_ATTRIBUTES,
                                     CLIENT_ENTITY_EFFECT,
                                     CLIENT_UPDATE_RECIPES,
                                     CLIENT_UPDATE_TAGS,
                                     CLIENT_PROJECTILE_POWER,
                                     CLIENT_CUSTOM_REPORT_DETAILS,
                                     CLIENT_SERVER_LINKS,

                                     //</editor-fold>
                                     //<editor-fold desc="Serverbound packets" defaultstate="collapsed">

                                     SERVER_CONFIRM_TELEPORTATION,
                                     SERVER_QUERY_BLOCK_ENTITY_TAG,
                                     SERVER_CHANGE_DIFFICULTY,
                                     SERVER_ACKNOWLEDGE_MESSAGE,
                                     SERVER_CHAT_COMMAND,
                                     SERVER_SIGNED_CHAT_COMMAND,
                                     SERVER_CHAT_MESSAGE,
                                     SERVER_PLAYER_SESSION,
                                     SERVER_CHUNK_BATCH_RECEIVED,
                                     SERVER_CLIENT_STATUS,
                                     SERVER_CLIENT_INFORMATION,
                                     SERVER_COMMAND_SUGGESTIONS_REQUEST,
                                     SERVER_ACKNOWLEDGE_CONFIGURATION,
                                     SERVER_CLICK_CONTAINER_BUTTON,
                                     SERVER_CLICK_CONTAINER,
                                     SERVER_CLOSE_CONTAINER,
                                     SERVER_CHANGE_CONTAINER_SLOT_STATE,
                                     SERVER_COOKIE_RESPONSE,
                                     SERVER_PLUGIN_MESSAGE,
                                     SERVER_DEBUG_SAMPLE_SUBSCRIPTION,
                                     SERVER_EDIT_BOOK,
                                     SERVER_QUERY_ENTITY_TAG,
                                     SERVER_INTERACT,
                                     SERVER_JIGSAW_GENERATE,
                                     SERVER_KEEP_ALIVE,
                                     SERVER_LOCK_DIFFICULTY,
                                     SERVER_SET_PLAYER_POSITION,
                                     SERVER_SET_PLAYER_POSITION_AND_ROTATION,
                                     SERVER_SET_PLAYER_ROTATION,
                                     SERVER_SET_PLAYER_ON_GROUND,
                                     SERVER_MOVE_VEHICLE,
                                     SERVER_PADDLE_BOAT,
                                     SERVER_PICK_ITEM,
                                     SERVER_PING_REQUEST,
                                     SERVER_PLACE_RECIPE,
                                     SERVER_PLAYER_ABILITIES,
                                     SERVER_PLAYER_ACTION,
                                     SERVER_PLAYER_COMMAND,
                                     SERVER_PLAYER_INPUT,
                                     SERVER_PONG,
                                     SERVER_CHANGE_RECIPE_BOOK_SETTINGS,
                                     SERVER_SET_SEEN_RECIPE,
                                     SERVER_RENAME_ITEM,
                                     SERVER_RESOURCE_PACK_RESPONSE,
                                     SERVER_SEEN_ADVANCEMENTS,
                                     SERVER_SELECT_TRADE,
                                     SERVER_SET_BEACON_EFFECT,
                                     SERVER_SET_HELD_ITEM,
                                     SERVER_PROGRAM_COMMAND_BLOCK,
                                     SERVER_PROGRAM_COMMAND_BLOCK_MINECART,
                                     SERVER_SET_CREATIVE_MODE_SLOT,
                                     SERVER_PROGRAM_JIGSAW_BLOCK,
                                     SERVER_PROGRAM_STRUCTURE_BLOCK,
                                     SERVER_UPDATE_SIGN,
                                     SERVER_SWING_ARM,
                                     SERVER_TELEPORT_TO_ENTITY,
                                     SERVER_USE_ITEM_ON,
                                     SERVER_USE_ITEM

                                     //</editor-fold>
                             ).build()
            );

    @Contract(" -> fail")
    private PlayPackets() throws AssertionError {
        throw new AssertionError("Registry class");
    }

    /**
     * Returns the registry of play packet types
     *
     * @return The registry of play packet types
     */
    public static @Unmodifiable @NotNull PacketRegistry registry() {
        return REGISTRY;
    }
}
