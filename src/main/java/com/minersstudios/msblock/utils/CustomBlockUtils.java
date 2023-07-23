package com.minersstudios.msblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.msblock.MSBlock.getCache;

public final class CustomBlockUtils {

    @Contract(value = " -> fail")
    private CustomBlockUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @param player player
     * @return False if no tasks with player
     */
    public static boolean hasPlayer(@NotNull Player player) {
        return getCache().blocks.containsSecondaryKey(player);
    }

    /**
     * @param block block
     * @return False if no tasks with block
     */
    public static boolean hasBlock(@NotNull Block block) {
        return getCache().blocks.containsPrimaryKey(block);
    }

    /**
     * Cancels all block break tasks with block
     *
     * @param block block
     */
    public static void cancelAllTasksWithThisBlock(@NotNull Block block) {
        getCache().farAway.remove(getCache().blocks.getSecondaryKey(block));
        Integer taskId = getCache().blocks.removeByPrimaryKey(block);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    /**
     * Cancels all block break tasks with player
     *
     * @param player player
     */
    public static void cancelAllTasksWithThisPlayer(@NotNull Player player) {
        getCache().farAway.remove(player);
        Integer taskId = getCache().blocks.removeBySecondaryKey(player);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}
