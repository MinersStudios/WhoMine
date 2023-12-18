package com.minersstudios.mscore.utility;

import com.minersstudios.mscore.MSCore;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for interacting with the CoreProtect API. This class provides
 * static methods for logging and retrieving block interactions. It uses a
 * singleton pattern to hold a reference to the CoreProtectAPI instance.
 * <p>
 * Singleton initialization is done in the {@link MSCore#setupCoreProtect()}
 * method in the plugin enable logic.
 *
 * @see CoreProtectAPI
 * @see #set(CoreProtectAPI)
 * @see <a href="https://docs.coreprotect.net">CoreProtect Documentation</a>
 * @see <a href="https://github.com/PlayPro/CoreProtect/">CoreProtect GitHub</a>
 */
public final class CoreProtectUtils {
    private static CoreProtectUtils singleton;

    private final CoreProtectAPI api;

    private CoreProtectUtils(final @NotNull CoreProtectAPI api) {
        this.api = api;
    }

    /**
     * Sets the CoreProtectAPI instance. This method should only be called once.
     *
     * @param api The CoreProtectAPI instance
     * @throws IllegalStateException If the CoreProtectAPI instance has already
     *                               been set
     */
    public static void set(final @NotNull CoreProtectAPI api) throws IllegalStateException {
        if (singleton != null) {
            throw new IllegalStateException("CoreProtectUtils is already initialized");
        }

        singleton = new CoreProtectUtils(api);
    }

    /**
     * @return An Optional containing the CoreProtectAPI instance, or an empty
     *         Optional if it has not been set
     * @see #set(CoreProtectAPI)
     */
    public static @NotNull Optional<CoreProtectAPI> api() {
        return singleton == null
                ? Optional.empty()
                : Optional.of(singleton.api);
    }

    /**
     * @return The CoreProtectAPI version, or -1 if it has not been set
     */
    public static int getAPIVersion() {
        return singleton == null
                ? -1
                : singleton.api.APIVersion();
    }

    /**
     * @return True if the CoreProtectAPI instance is enabled, or false if it
     *         has not been set or is disabled
     * @see CoreProtectAPI#isEnabled()
     */
    public static boolean isEnabled() {
        return singleton != null && singleton.api.isEnabled();
    }

    /**
     * @param placer The username you're checking to see if they've placed a
     *               block already.
     * @param block  The block you're checking.
     * @param time   How far back to check. "5" would only check through the
     *               last 5 seconds of logged blocks.
     * @param offset A time offset. "2" would ignore the last 2 seconds of most
     *               recently ignored data. (0 = no offset)
     * @return True if a user has already placed a block at the location within
     *         the specified time limit.
     */
    public static boolean hasPlaced(
            final @NotNull String placer,
            final @Nullable Block block,
            final int time,
            final int offset
    ) {
        return singleton != null
                && singleton.api.hasPlaced(
                        placer,
                        block,
                        time,
                        offset
                );
    }

    /**
     * @param remover The username you're checking to see if they've removed a
     *                block already.
     * @param block   The block you're checking.
     * @param time    How far back to check. "5" would only check through the
     *                last 5 seconds of logged blocks.
     * @param offset  A time offset. "2" would ignore the last 2 seconds of most
     *                recently ignored data. (0 = no offset)
     * @return True if a user has already removed a block at the location within
     *         the specified time limit.
     */
    public static boolean hasRemoved(
            final @NotNull String remover,
            final @Nullable Block block,
            final int time,
            final int offset
    ) {
        return singleton != null
                && singleton.api.hasRemoved(
                        remover,
                        block,
                        time,
                        offset
                );
    }

    /**
     * This will log a chat message as having been sent.
     *
     * @param player  The player who sent the message.
     * @param message The message that was sent.
     * @return True if the message was logged successfully.
     */
    public static boolean logChat(
            final @NotNull Player player,
            final @NotNull String message
    ) {
        return singleton != null
                && singleton.api.logChat(
                        player,
                        message
                );
    }

    /**
     * This will log a command as having been executed.
     *
     * @param player  The player who executed the command.
     * @param command The command that was executed.
     * @return True if the command was logged successfully.
     */
    public static boolean logCommand(
            final @NotNull Player player,
            final @NotNull String command
    ) {
        return singleton != null
                && singleton.api.logCommand(
                        player,
                        command
                );
    }

    /**
     * This will log a block as having been interacted with.
     *
     * @param who      Specify the username to log as having caused the
     *                 interaction.
     * @param location Specify the location of the interaction you're logging.
     * @return True if the interaction was logged successfully.
     */
    public static boolean logInteraction(
            final @NotNull String who,
            final @NotNull Location location
    ) {
        return singleton != null
                && singleton.api.logInteraction(
                        who,
                        location
                );
    }

    /**
     * This will log any transactions made to a block's inventory immediately
     * after calling the method.
     *
     * @param who      Specify the username to log as having added/removed the
     *                 items.
     * @param location Specify the location of the block inventory you're
     *                 logging.
     * @return True if the transaction was logged successfully.
     */
    public static boolean logContainerTransaction(
            final @NotNull String who,
            final @NotNull Location location
    ) {
        return singleton != null
                && singleton.api.logContainerTransaction(
                        who,
                        location
                );
    }

    /**
     * This will log a block as being placed.
     *
     * @param placer   Specify the username to log as having placed the block.
     * @param location Specify the location of the block you're logging.
     * @return True if the block was logged successfully.
     * @see #logPlacement(String, Location, Material, BlockData)
     */
    public static boolean logPlacement(
            final @NotNull String placer,
            final @NotNull Location location
    ) {
        return logPlacement(placer, location, null, null);
    }

    /**
     * This will log a block as being placed.
     *
     * @param placer    Specify the username to log as having placed the block.
     * @param location  Specify the location of the block you're logging.
     * @param blockData Specify the BlockData of the block you're logging.
     * @return True if the block was logged successfully.
     * @see #logPlacement(String, Location, Material, BlockData)
     */
    public static boolean logPlacement(
            final @NotNull String placer,
            final @NotNull Location location,
            final @Nullable BlockData blockData
    ) {
        return logPlacement(placer, location, null, blockData);
    }

    /**
     * This will log a block as being placed.
     *
     * @param placer   Specify the username to log as having placed the block.
     * @param location Specify the location of the block you're logging.
     * @param material Specify the Material of the block you're logging.
     * @return True if the block was logged successfully.
     * @see #logPlacement(String, Location, Material, BlockData)
     */
    public static boolean logPlacement(
            final @NotNull String placer,
            final @NotNull Location location,
            final @Nullable Material material
    ) {
        return logPlacement(placer, location, material, null);
    }

    /**
     * This will log a block as being placed.
     *
     * @param placer    Specify the username to log as having placed the block.
     * @param location  Specify the location of the block you're logging.
     * @param material  Specify the Material of the block you're logging.
     * @param blockData Specify the BlockData of the block you're logging.
     * @return True if the block was logged successfully.
     */
    public static boolean logPlacement(
            final @NotNull String placer,
            final @NotNull Location location,
            final @Nullable Material material,
            final @Nullable BlockData blockData
    ) {
        return singleton != null
                && singleton.api.logPlacement(
                        placer,
                        location,
                        material,
                        blockData
                );
    }

    /**
     * This will log a block as being removed/broken, and will log the block's
     * inventory (if applicable).
     *
     * @param remover  Specify the username to log as having removed the block.
     * @param location Specify the location of the block you're logging.
     * @return True if the block was logged successfully.
     * @see #logRemoval(String, Location, Material, BlockData)
     */
    public static boolean logRemoval(
            final @NotNull String remover,
            final @NotNull Location location
    ) {
        return logRemoval(remover, location, null, null);
    }

    /**
     * This will log a block as being removed/broken, and will log the block's
     * inventory (if applicable).
     *
     * @param remover   Specify the username to log as having removed the block.
     * @param location  Specify the location of the block you're logging.
     * @param blockData Specify the BlockData of the block you're logging.
     * @return True if the block was logged successfully.
     * @see #logRemoval(String, Location, Material, BlockData)
     */
    public static boolean logRemoval(
            final @NotNull String remover,
            final @NotNull Location location,
            final @Nullable BlockData blockData
    ) {
        return logRemoval(remover, location, null, blockData);
    }

    /**
     * This will log a block as being removed/broken, and will log the block's
     * inventory (if applicable).
     *
     * @param remover  Specify the username to log as having removed the block.
     * @param location Specify the location of the block you're logging.
     * @param material Specify the Material of the block you're logging.
     * @return True if the block was logged successfully.
     * @see #logRemoval(String, Location, Material, BlockData)
     */
    public static boolean logRemoval(
            final @NotNull String remover,
            final @NotNull Location location,
            final @Nullable Material material
    ) {
        return logRemoval(remover, location, material, null);
    }

    /**
     * This will log a block as being removed/broken, and will log the block's
     * inventory (if applicable).
     *
     * @param remover   Specify the username to log as having removed the block.
     * @param location  Specify the location of the block you're logging.
     * @param material  Specify the Material of the block you're logging.
     * @param blockData Specify the BlockData of the block you're logging.
     * @return True if the block was logged successfully.
     */
    public static boolean logRemoval(
            final @NotNull String remover,
            final @NotNull Location location,
            final @Nullable Material material,
            final @Nullable BlockData blockData
    ) {
        return singleton != null
                && singleton.api.logRemoval(
                        remover,
                        location,
                        material,
                        blockData
                );
    }

    /**
     * This will perform a lookup
     *
     * @param time           Specify the amount of time to search back.
     *                       "5" would return results from the last 5 seconds.
     * @param restrictUsers  Specify any usernames to perform the lookup on. Can
     *                       be set to "null" if both a radius and a location
     *                       are specified.
     * @param excludeUsers   Specify any usernames to exclude from the lookup.
     * @param restrictBlocks Specify a list of EntityType's or Material's to
     *                       restrict the search to.
     * @param excludeBlocks  Specify a list of EntityType's or Material's to
     *                       exclude from the search.
     * @param actionList     Specify a list of action types to restrict the
     *                       search to.
     * @param radius         Specify a radius to restrict the search to. A
     *                       location must be specified if using this. Set to "0"
     *                       to disable.
     * @param radiusLocation Specify a location to search around. Can be set to
     *                       "null" if no radius is specified, and a user is
     *                       specified.
     * @return A list of results or null if the CoreProtectAPI instance is not
     *         set
     */
    public static @Nullable List<String[]> performLookup(
            final int time,
            final @Nullable List<String> restrictUsers,
            final @Nullable List<String> excludeUsers,
            final @Nullable List<Object> restrictBlocks,
            final @Nullable List<Object> excludeBlocks,
            final @Nullable List<Integer> actionList,
            final int radius,
            final @Nullable Location radiusLocation
    ) {
        return singleton == null
                ? null
                : singleton.api.performLookup(
                        time,
                        restrictUsers,
                        excludeUsers,
                        restrictBlocks,
                        excludeBlocks,
                        actionList,
                        radius,
                        radiusLocation
                );
    }

    /**
     * This will perform a partial lookup
     *
     * @param time           Specify the amount of time to search back.
     *                       "5" would return results from the last 5 seconds.
     * @param restrictUsers  Specify any usernames to perform the lookup on. Can
     *                       be set to "null" if both a radius and a location
     *                       are specified.
     * @param excludeUsers   Specify any usernames to exclude from the lookup.
     * @param restrictBlocks Specify a list of EntityType's or Material's to
     *                       restrict the search to.
     * @param excludeBlocks  Specify a list of EntityType's or Material's to
     *                       exclude from the search.
     * @param actionList     Specify a list of action types to restrict the
     *                       search to.
     * @param radius         Specify a radius to restrict the search to. A
     *                       location must be specified if using this. Set to "0"
     *                       to disable.
     * @param radiusLocation Specify a location to search around. Can be set to
     *                       "null" if no radius is specified, and a user is
     *                       specified.
     * @param limitOffset    Specify the limit offset.
     * @param limitCount     Specify the limit count.
     * @return A list of results or null if the CoreProtectAPI instance is not
     *         set
     */
    public static @Nullable List<String[]> performPartialLookup(
            final int time,
            final @Nullable List<String> restrictUsers,
            final @Nullable List<String> excludeUsers,
            final @Nullable List<Object> restrictBlocks,
            final @Nullable List<Object> excludeBlocks,
            final @Nullable List<Integer> actionList,
            final int radius,
            final @Nullable Location radiusLocation,
            final int limitOffset,
            final int limitCount
    ) {
        return singleton == null
                ? null
                : singleton.api.performPartialLookup(
                        time,
                        restrictUsers,
                        excludeUsers,
                        restrictBlocks,
                        excludeBlocks,
                        actionList,
                        radius,
                        radiusLocation,
                        limitOffset,
                        limitCount
                );
    }

    /**
     * This will perform a restore.
     *
     * @param time           Specify the amount of time to restore.
     *                       "5" would return results from the last 5 seconds.
     * @param restrictUsers  Specify any usernames to perform the restore on.
     *                       Can be set to "null" if both a radius and a
     *                       location are specified.
     * @param excludeUsers   Specify any usernames to exclude from the restore.
     * @param restrictBlocks Specify a list of EntityType's or Material's to
     *                       restrict the restore to.
     * @param excludeBlocks  Specify a list of EntityType's or Material's to
     *                       exclude from the restore.
     * @param actionList     Specify a list of action types to restrict the
     *                       restore to.
     * @param radius         Specify a radius to restrict the restore to. A
     *                       location must be specified if using this. Set to "0"
     *                       to disable.
     * @param radiusLocation Specify a location to restore around. Can be set to
     *                       "null" if no radius is specified, and a user is
     *                       specified.
     * @return A list of results or null if the CoreProtectAPI instance is not
     *         set
     */
    public static @Nullable List<String[]> performRestore(
            final int time,
            final @Nullable List<String> restrictUsers,
            final @Nullable List<String> excludeUsers,
            final @Nullable List<Object> restrictBlocks,
            final @Nullable List<Object> excludeBlocks,
            final @Nullable List<Integer> actionList,
            final int radius,
            final @Nullable Location radiusLocation
    ) {
        return singleton == null
                ? null
                : singleton.api.performRestore(
                        time,
                        restrictUsers,
                        excludeUsers,
                        restrictBlocks,
                        excludeBlocks,
                        actionList,
                        radius,
                        radiusLocation
                );
    }

    /**
     * This will perform a rollback. Method must be called async.
     *
     * @param time           Specify the amount of time to rollback. "5" would
     *                       return results from the last 5 seconds.
     * @param restrictUsers  Specify any usernames to perform the rollback on.
     *                       Can be set to "null" if both a radius and a
     *                       location are specified.
     * @param excludeUsers   Specify any usernames to exclude from the rollback.
     * @param restrictBlocks Specify a list of EntityType's or Material's to
     *                       restrict the rollback to.
     * @param excludeBlocks  Specify a list of EntityType's or Material's to
     *                       exclude from the rollback.
     * @param actionList     Specify a list of action types to restrict the
     *                       rollback to.
     * @param radius         Specify a radius to restrict the rollback to. A
     *                       location must be specified if using this. Set to "0"
     *                       to disable.
     * @param radiusLocation Specify a location to rollback around. Can be set
     *                       to "null" if no radius is specified, and a user is
     *                       specified.
     * @return A list of results or null if the CoreProtectAPI instance is not
     *         set
     */
    public @Nullable List<String[]> performRollback(
            final int time,
            final @Nullable List<String> restrictUsers,
            final @Nullable List<String> excludeUsers,
            final @Nullable List<Object> restrictBlocks,
            final @Nullable List<Object> excludeBlocks,
            final @Nullable List<Integer> actionList,
            final int radius,
            final @Nullable Location radiusLocation
    ) {
        return singleton == null
                ? null
                : singleton.api.performRollback(
                        time,
                        restrictUsers,
                        excludeUsers,
                        restrictBlocks,
                        excludeBlocks,
                        actionList,
                        radius,
                        radiusLocation
                );
    }

    /**
     * This will search the consumer queue for changes on a block not yet saved
     * in the database.
     *
     * @param block The block to perform the lookup on.
     * @return A list of results or null if the CoreProtectAPI instance is not
     *         set
     */
    public static @NotNull List<String[]> queueLookup(final @NotNull Block block) {
        return singleton == null
                ? Collections.emptyList()
                : singleton.api.queueLookup(block);
    }

    /**
     * This will perform a full lookup on a single block.
     *
     * @param block The block to perform the lookup on.
     * @param time  Specify the amount of time to search back. "5" would return
     *              results from the last 5 seconds.
     * @return A list of results or null if the CoreProtectAPI instance is not
     *         set
     */
    public static @NotNull List<String[]> blockLookup(
            final @NotNull Block block,
            final int time
    ) {
        if (singleton == null) {
            return Collections.emptyList();
        }

        final var results = singleton.api.blockLookup(block, time);
        return results == null
                ? Collections.emptyList()
                : results;
    }

    /**
     * This will perform a session lookup on a single player.
     *
     * @param user The user to perform the lookup on.
     * @param time Specify the amount of time to search back. "5" would return
     *             results from the last 5 seconds.
     * @return A list of results or null if the CoreProtectAPI instance is not
     *         set
     */
    public @NotNull List<String[]> sessionLookup(
            final @NotNull String user,
            final int time
    ) {
        return singleton == null
                ? Collections.emptyList()
                : singleton.api.sessionLookup(user, time);
    }

    /**
     * This will parse results from a lookup. You'll then be able to view the
     * following:
     * <ul>
     *     <li><b>getX() : </b> Returns the X coordinate of the block.</li>
     *     <li><b>getY() : </b> Returns the Y coordinate of the block.</li>
     *     <li><b>getZ() : </b> Returns the Z coordinate of the block.</li>
     *     <li><b>getType() : </b> Get the Material of the block.</li>
     *     <li><b>getBlockData() : </b> Get the BlockData of the block.</li>
     *     <li><b>getPlayer() : </b> Get the username as a string.</li>
     *     <li><b>getTimestamp() : </b> Get the time of the action.</li>
     *     <li><b>getActionId() : </b> Get the action ID. <br>
     *                                 (0=removed, 1=placed, 2=interaction)
     *     </li>
     *     <li><b>getActionString() : </b> Get the action as a string. <br>
     *                                     (Removal, Placement, Interaction)
     *     </li>
     *     <li><b>isRolledBack() : </b> If the block is currently rolled back or
     *                                  not.
     *     </li>
     *     <li><b>worldName() : </b> The name of the world the block is located
     *                               in.
     *     </li>
     * </ul>
     *
     * @param results The results to parse.
     * @return A list of results or null if the CoreProtectAPI instance is not
     *         set
     */
    public static @Nullable CoreProtectAPI.ParseResult parseResult(final String @NotNull [] results) {
        return singleton == null
                ? null
                : singleton.api.parseResult(results);
    }

    /**
     * This will perform a purge on the CoreProtect database.
     *
     * @param time Purge any data earlier than this. "120" would purge any data
     *             older than 120 seconds (2 minutes).
     */
    public static void performPurge(final int time) {
        if (singleton != null) {
            singleton.api.performPurge(time);
        }
    }

    /**
     * Running this will print out "[CoreProtect] API Test Successful." in the
     * server console.
     */
    public static void testAPI() {
        if (singleton != null) {
            singleton.api.testAPI();
        }
    }
}
