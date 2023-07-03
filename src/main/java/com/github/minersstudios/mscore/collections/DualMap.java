package com.github.minersstudios.mscore.collections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class DualMap<P, S, V> {
    private final @NotNull Map<P, Map.Entry<S, V>> map = new HashMap<>();
    private final @NotNull Map<S, P> keyMap = new HashMap<>();

    public @Nullable V put(
            @NotNull P primary,
            @NotNull S secondary,
            @NotNull V value
    ) {
        var entry = Map.entry(secondary, value);
        this.keyMap.put(secondary, primary);
        return this.map.put(primary, entry) != null ? value : null;
    }

    @Contract(pure = true)
    public @NotNull Set<P> primaryKeySet() {
        return this.map.keySet();
    }

    @Contract(pure = true)
    public @NotNull Set<S> secondaryKeySet() {
        return this.keyMap.keySet();
    }

    @Contract(pure = true)
    public @NotNull Collection<V> values() {
        return this.map.values().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public P getPrimaryKey(@Nullable S secondary) {
        return this.keyMap.get(secondary);
    }

    public S getSecondaryKey(@Nullable P primary) {
        var entry = this.map.get(primary);
        return entry != null ? entry.getKey() : null;
    }

    public V getByPrimaryKey(@Nullable P primary) {
        var entry = this.map.get(primary);
        return entry != null ? entry.getValue() : null;
    }

    public V getBySecondaryKey(@Nullable S secondary) {
        return this.getByPrimaryKey(this.keyMap.get(secondary));
    }

    @Contract(value = "null -> false", pure = true)
    public boolean containsPrimaryKey(@Nullable P primary) {
        if (primary == null) return false;
        return this.map.containsKey(primary);
    }

    @Contract(value = "null -> false", pure = true)
    public boolean containsSecondaryKey(@Nullable S secondary) {
        if (secondary == null) return false;
        return this.secondaryKeySet().contains(secondary);
    }

    @Contract(value = "null -> false", pure = true)
    public boolean containsValue(@Nullable V value) {
        if (value == null) return false;
        return this.values().contains(value);
    }

    public @Nullable V removeByPrimaryKey(@NotNull P primary) {
        var entry = this.map.remove(primary);
        if (entry == null) return null;
        this.keyMap.remove(entry.getKey());
        return entry.getValue();
    }

    public @Nullable V removeBySecondaryKey(@NotNull S secondary) {
        P primary = this.keyMap.remove(secondary);
        if (primary == null) return null;
        return this.map.remove(primary).getValue();
    }

    public void clear() {
        this.map.clear();
        this.keyMap.clear();
    }

    @Contract(pure = true)
    public int size() {
        return this.map.size();
    }

    @Contract(pure = true)
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
}
