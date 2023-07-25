package com.minersstudios.msblock.collection;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

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

    private static final double FINAL_STEP = 1.25d;

    /**
     * @param player The player for which to retrieve the distance
     * @return The distance associated with the player,
     *         or 0.0 if the player is not found in the map
     */
    public double getDistance(@NotNull Player player) {
        return this.stepMap.getOrDefault(player, 0.0d);
    }

    /**
     * @return An unmodifiable set of players present in the StepMap
     */
    public @NotNull @Unmodifiable Set<Player> playerSet() {
        return Set.copyOf(this.stepMap.keySet());
    }

    /**
     * @return An unmodifiable set of Map entries containing players
     *         and step distances
     */
    public @NotNull @Unmodifiable Set<Map.Entry<Player, Double>> entrySet() {
        return Set.copyOf(this.stepMap.entrySet());
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
            @NotNull Player player,
            double distance
    ) {
        double newDistance = this.getDistance(player) + distance;

        if (isFinalStep(newDistance)) {
            this.stepMap.remove(player);
            return true;
        } else {
            this.stepMap.put(player, newDistance);
            return false;
        }
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
            @NotNull Player player,
            double distance
    ) {
        Double previousDistance = this.stepMap.put(player, distance);
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
    public double remove(@NotNull Player player) {
        Double previousDistance = this.stepMap.remove(player);
        return previousDistance == null ? -1.0d : previousDistance;
    }

    /**
     * Checks if the StepMap contains the given player
     *
     * @param player The player to check
     * @return True if the player is present in the StepMap
     */
    public boolean contains(@NotNull Player player) {
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
    public static boolean isFinalStep(double distance) {
        return distance >= FINAL_STEP;
    }
}
