package com.minersstudios.msblock.collection;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StepMap {
    private final Map<Player, Double> stepMap = new ConcurrentHashMap<>();

    private static final double FINAL_STEP = 1.25d;

    public double getDistance(@NotNull Player player) {
        return this.stepMap.getOrDefault(player, 0.0d);
    }

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

    public void put(
            @NotNull Player player,
            double distance
    ) {
        this.stepMap.put(player, distance);
    }

    public void remove(@NotNull Player player) {
        this.stepMap.remove(player);
    }

    public boolean contains(@NotNull Player player) {
        return this.stepMap.containsKey(player);
    }

    public boolean isEmpty() {
        return this.stepMap.isEmpty();
    }

    public void clear() {
        this.stepMap.clear();
    }

    public int size() {
        return this.stepMap.size();
    }

    public static boolean isFinalStep(double distance) {
        return distance >= FINAL_STEP;
    }
}
