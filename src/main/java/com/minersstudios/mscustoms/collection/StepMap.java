package com.minersstudios.mscustoms.collection;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The StepMap class represents a data structure
 * that associates step distances with players. It
 * allows adding, updating, and removing players
 * along with their corresponding step distances.
 */
public class StepMap {
    private final Map<Player, Double> stepMap = new ConcurrentHashMap<>();

    private static final double FINAL_STEP = 1.5d;

    /**
     * @return An unmodifiable view of the player set in the StepMap
     */
    public @NotNull @UnmodifiableView Set<Player> playerSet() {
        return Collections.unmodifiableSet(this.stepMap.keySet());
    }

    /**
     * @return An unmodifiable view of the map entries containing
     *         the players and their corresponding step distances
     */
    public @NotNull @UnmodifiableView Set<Map.Entry<Player, Double>> entrySet() {
        return Collections.unmodifiableSet(this.stepMap.entrySet());
    }

    /**
     * @param player The player for which to retrieve the distance
     * @return The distance associated with the player,
     *         or 0.0 if the player is not found in the map
     */
    public double get(final @NotNull Player player) {
        return this.stepMap.getOrDefault(player, 0.0d);
    }

    /**
     * Puts a new player with the corresponding distance into the
     * StepMap. If the player already exists in the map, the
     * distance is updated.
     *
     * @param player   The player to add to the StepMap
     * @param distance The distance associated with the player
     * @return The previous distance associated with the player,
     *         or -1.0 if the player is not found in the map
     */
    public double put(
            final @NotNull Player player,
            final double distance
    ) {
        final Double previousDistance = this.stepMap.put(player, distance);
        return previousDistance == null ? -1.0d : previousDistance;
    }

    /**
     * Removes the player and their corresponding distance from the
     * StepMap, if present
     *
     * @param player The player to remove from the StepMap
     * @return The previous distance associated with the player,
     *         or -1.0 if the player is not found in the map
     */
    public double remove(final @NotNull Player player) {
        final Double previousDistance = this.stepMap.remove(player);
        return previousDistance == null ? -1.0d : previousDistance;
    }

    /**
     * Adds or puts the distance associated with the given player
     * in the StepMap. If the updated distance reaches the final
     * step threshold, the player is removed from the map and true
     * is returned.
     *
     * @param player   The player for which to add/update the distance
     * @param distance The distance to be added to the player's
     *                 current distance
     * @return True if the updated distance reaches
     *         or exceeds the final step threshold
     * @see #isFinalStep(double)
     */
    public boolean addDistance(
            final @NotNull Player player,
            final double distance
    ) {
        final double newDistance = this.get(player) + distance;

        if (isFinalStep(newDistance)) {
            this.stepMap.remove(player);
            return true;
        } else {
            this.stepMap.put(player, newDistance);
            return false;
        }
    }

    /**
     * Checks if the StepMap contains the given player
     *
     * @param player The player to check
     * @return True if the player is present in the StepMap
     */
    public boolean contains(final @NotNull Player player) {
        return this.stepMap.containsKey(player);
    }

    /**
     * @return True if the StepMap contains no players
     */
    public boolean isEmpty() {
        return this.stepMap.isEmpty();
    }

    /**
     * Removes all players and their corresponding distances from
     * the StepMap
     */
    public void clear() {
        this.stepMap.clear();
    }

    /**
     * @return The number of players present in the StepMap
     */
    public int size() {
        return this.stepMap.size();
    }

    /**
     * @param distance The distance to check
     * @return True if the distance reaches or exceeds the final
     *         step threshold
     * @see #FINAL_STEP
     */
    public static boolean isFinalStep(final double distance) {
        return distance >= FINAL_STEP;
    }
}
