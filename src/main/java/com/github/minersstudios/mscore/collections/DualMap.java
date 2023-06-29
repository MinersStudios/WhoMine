package com.github.minersstudios.mscore.collections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class DualMap<K1, K2, V> {
    private final @NotNull HashMap<K1, Map.Entry<K2, V>> map = new HashMap<>();
    private final @NotNull HashMap<K2, K1> keyMap = new HashMap<>();

    public @Nullable V put(
            @NotNull K1 key1,
            @NotNull K2 key2,
            @NotNull V value
    ) {
        Map.Entry<K2, V> entry = new AbstractMap.SimpleEntry<>(key2, value);
        this.keyMap.put(key2, key1);
        return this.map.put(key1, entry) != null ? value : null;
    }

    @Contract(pure = true)
    public @NotNull Set<K1> primaryKeySet() {
        return this.map.keySet();
    }

    @Contract(pure = true)
    public @NotNull Set<K2> secondaryKeySet() {
        return this.keyMap.keySet();
    }

    @Contract(pure = true)
    public @NotNull Collection<V> values() {
        return this.map.values().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public @Nullable K1 getPrimaryKey(@Nullable K2 key2) {
        return this.keyMap.get(key2);
    }

    public @Nullable K2 getSecondaryKey(@Nullable K1 key1) {
        Map.Entry<K2, V> entry = this.map.get(key1);
        return entry != null ? entry.getKey() : null;
    }

    public @Nullable V getByPrimaryKey(@Nullable K1 key1) {
        Map.Entry<K2, V> entry = this.map.get(key1);
        return entry != null ? entry.getValue() : null;
    }

    public @Nullable V getBySecondaryKey(@Nullable K2 key2) {
        return this.getByPrimaryKey(this.keyMap.get(key2));
    }

    @Contract(value = "null -> false", pure = true)
    public boolean containsPrimaryKey(@Nullable K1 key1) {
        if (key1 == null) return false;
        return this.map.containsKey(key1);
    }

    @Contract(value = "null -> false", pure = true)
    public boolean containsSecondaryKey(@Nullable K2 key2) {
        if (key2 == null) return false;
        return this.secondaryKeySet().contains(key2);
    }

    @Contract(value = "null -> false", pure = true)
    public boolean containsValue(@Nullable V value) {
        if (value == null) return false;
        return this.values().contains(value);
    }

    public @Nullable V removeByPrimaryKey(@NotNull K1 key1) {
        Map.Entry<K2, V> entry = this.map.remove(key1);
        if (entry == null) return null;
        this.keyMap.remove(entry.getKey());
        return entry.getValue();
    }

    public @Nullable V removeBySecondaryKey(@NotNull K2 key2) {
        K1 key1 = this.keyMap.remove(key2);
        if (key1 == null) return null;
        return this.map.remove(key1).getValue();
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
