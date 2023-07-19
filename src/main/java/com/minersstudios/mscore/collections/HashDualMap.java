package com.minersstudios.mscore.collections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.stream.Collectors;

public class HashDualMap<P, S, V> implements DualMap<P, S, V> {
    private final @NotNull Map<P, Map.Entry<S, V>> map;
    private final @NotNull Map<S, P> keyMap;

    public HashDualMap() {
        map = new HashMap<>();
        keyMap = new HashMap<>();
    }

    public HashDualMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        map = new HashMap<>(initialCapacity);
        keyMap = new HashMap<>(initialCapacity);
    }

    @Override
    public @Nullable V put(
            @NotNull P primary,
            @NotNull S secondary,
            @NotNull V value
    ) {
        var entry = Map.entry(secondary, value);
        this.keyMap.put(secondary, primary);
        return this.map.put(primary, entry) != null ? value : null;
    }

    @Override
    public P getPrimaryKey(@Nullable S secondary) {
        return this.keyMap.get(secondary);
    }

    @Override
    public S getSecondaryKey(@Nullable P primary) {
        var entry = this.map.get(primary);
        return entry != null ? entry.getKey() : null;
    }

    @Override
    public V getByPrimaryKey(@Nullable P primary) {
        var entry = this.map.get(primary);
        return entry != null ? entry.getValue() : null;
    }

    @Override
    public V getBySecondaryKey(@Nullable S secondary) {
        return this.getByPrimaryKey(this.keyMap.get(secondary));
    }

    @Override
    public @Nullable V removeByPrimaryKey(@NotNull P primary) {
        var entry = this.map.remove(primary);
        if (entry == null) return null;
        this.keyMap.remove(entry.getKey());
        return entry.getValue();
    }

    @Override
    public @Nullable V removeBySecondaryKey(@NotNull S secondary) {
        P primary = this.keyMap.remove(secondary);
        if (primary == null) return null;
        return this.map.remove(primary).getValue();
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean containsPrimaryKey(@Nullable P primary) {
        return primary != null && this.map.containsKey(primary);
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean containsSecondaryKey(@Nullable S secondary) {
        return secondary != null && this.secondaryKeySet().contains(secondary);
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean containsValue(@Nullable V value) {
        return value != null && this.values().contains(value);
    }

    @Override
    public void clear() {
        this.map.clear();
        this.keyMap.clear();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public @NotNull Set<P> primaryKeySet() {
        return this.map.keySet();
    }

    @Override
    public @NotNull Set<S> secondaryKeySet() {
        return this.keyMap.keySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        return this.map.values().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
