package com.minersstudios.msblock.collection;

import com.minersstudios.msblock.MSBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DiggingMap {
    private final Map<Block, Set<Entry>> diggingBlockMap = new ConcurrentHashMap<>();

    public @Nullable Block getBlock(@NotNull Entry diggingEntry) {
        return this.entries().stream()
                .filter(entry -> entry.getValue().equals(diggingEntry))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public @Nullable Block getBlock(@NotNull Player player) {
        return this.entries().stream()
                .filter(entry -> entry.getValue().player.equals(player))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public @Nullable Entry getBiggestStageEntry(@NotNull Block block) {
        return this.entries(block).stream()
                .max(Comparator.comparingInt(Entry::stage))
                .orElse(null);
    }

    public @NotNull @Unmodifiable List<Entry> getDiggingEntries(@NotNull Block block) {
        return List.copyOf(this.entries(block));
    }

    public @NotNull @Unmodifiable Collection<Entry> diggingEntries() {
        return this.diggingBlockMap.values().stream()
                .flatMap(Collection::stream)
                .toList();
    }

    public @NotNull @Unmodifiable Set<Block> blockSet() {
        return Set.copyOf(this.diggingBlockMap.keySet());
    }

    public @NotNull @Unmodifiable Set<Map.Entry<Block, Entry>> entrySet() {
        return Set.copyOf(this.entries());
    }

    public void put(
            @NotNull Block block,
            @NotNull Entry diggingEntry
    ) {
        var entrySet = this.diggingBlockMap.computeIfAbsent(block, b -> ConcurrentHashMap.newKeySet());

        entrySet.add(diggingEntry);
        this.diggingBlockMap.put(block, entrySet);
    }

    public void remove(
            @NotNull Block block,
            @NotNull Entry diggingEntry
    ) {
        var entrySet = this.entries(block);

        entrySet.remove(diggingEntry);
        diggingEntry.cancelTask();

        if (entrySet.isEmpty()) {
            this.diggingBlockMap.remove(block);
        } else {
            this.diggingBlockMap.put(block, entrySet);
        }
    }

    public void remove(
            @NotNull Block block,
            @NotNull Player player
    ) {
        this.entries().stream()
        .filter(entry -> entry.getValue().player.equals(player))
        .findFirst()
        .ifPresent(entry -> this.remove(block, entry.getValue()));
    }

    public void removeAll(@NotNull Block block) {
        var entries = this.diggingBlockMap.remove(block);

        if (entries != null) {
            entries.forEach(Entry::cancelTask);
        }
    }

    public void removeAll(@NotNull Entry diggingEntry) {
        this.entries().stream()
        .filter(entry -> entry.getValue().equals(diggingEntry))
        .findFirst()
        .ifPresent(entry -> this.remove(entry.getKey(), entry.getValue()));
    }

    public void removeAll(@NotNull Player player) {
        this.entries().stream()
        .filter(entry -> entry.getValue().player.equals(player))
        .findFirst()
        .ifPresent(entry -> this.remove(entry.getKey(), entry.getValue()));
    }

    public boolean containsBlock(@NotNull Block block) {
        return this.diggingBlockMap.containsKey(block);
    }

    public boolean containsEntry(@NotNull Entry diggingEntry) {
        return this.diggingBlockMap.values().stream()
                .anyMatch(entry -> entry.contains(diggingEntry));
    }

    public boolean containsPlayer(@NotNull Player player) {
        return this.entries().stream()
                .anyMatch(entry -> entry.getValue().player.equals(player));
    }

    public boolean isEmpty() {
        return this.diggingBlockMap.isEmpty();
    }

    public void clear() {
        this.diggingBlockMap.clear();
    }

    public int size() {
        return this.diggingBlockMap.size();
    }

    private @NotNull Set<Entry> entries(@NotNull Block block) {
        return this.diggingBlockMap.getOrDefault(block, ConcurrentHashMap.newKeySet());
    }

    private @NotNull Set<Map.Entry<Block, Entry>> entries() {
        return this.diggingBlockMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(value -> Map.entry(entry.getKey(), value)))
                .collect(Collectors.toSet());
    }

    public static class Entry {
        private final Player player;
        private int taskId;
        private int stage;
        private boolean farAway;

        private Entry(
                @NotNull Player player,
                int taskId,
                int stage,
                boolean farAway
        ) {
            this.player = player;
            this.stage = stage;
            this.taskId = taskId;
            this.farAway = farAway;
        }

        @Contract(value = "_ -> new", pure = true)
        public static @NotNull Entry create(@NotNull Player player) {
            return new Entry(player, -1, 0, false);
        }

        @Contract(value = "_, _, _, _ -> new", pure = true)
        public static @NotNull Entry create(
                @NotNull Player player,
                int taskId,
                int stage,
                boolean farAway
        ) {
            return new Entry(player, taskId, stage, farAway);
        }

        public @NotNull Player player() {
            return this.player;
        }

        public int taskId() {
            return this.taskId;
        }

        public @NotNull Entry taskId(int taskId) {
            this.taskId = taskId;
            return this;
        }

        public int stage() {
            return this.stage;
        }

        public @NotNull Entry stage(int stage) {
            this.stage = stage;
            return this;
        }

        public boolean farAway() {
            return this.farAway;
        }

        public @NotNull Entry farAway(boolean farAway) {
            this.farAway = farAway;
            return this;
        }

        public boolean isStageTheBiggest(@NotNull Block block) {
            Entry biggestStageEntry = MSBlock.getCache().diggingMap.getBiggestStageEntry(block);
            return biggestStageEntry != null
                    && (this.equals(biggestStageEntry)
                    || this.stage > biggestStageEntry.stage());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            return obj instanceof Entry entry && entry.taskId == this.taskId;
        }

        @Override
        public @NotNull String toString() {
            return "Entry{" +
                    "player=" + this.player.getName() +
                    ", taskId=" + this.taskId +
                    ", stage=" + this.stage +
                    '}';
        }

        public void cancelTask() {
            if (this.taskId != -1) {
                Bukkit.getScheduler().cancelTask(this.taskId);
                this.taskId = -1;
            }
        }
    }
}
