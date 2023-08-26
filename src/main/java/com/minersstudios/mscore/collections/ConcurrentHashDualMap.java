package com.minersstudios.mscore.collections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashDualMap<P, S, V> implements DualMap<P, S, V> {
    private final @NotNull Map<P, Map.Entry<S, V>> map;
    private final @NotNull Map<S, P> keyMap;

    public ConcurrentHashDualMap() {
        this.map = new ConcurrentHashMap<>();
        this.keyMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashDualMap(final @Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        this.map = new ConcurrentHashMap<>(initialCapacity);
        this.keyMap = new ConcurrentHashMap<>(initialCapacity);
    }

    @Override
    public @Nullable V put(
            final @NotNull P primary,
            final @NotNull S secondary,
            final @NotNull V value
    ) {
        this.keyMap.put(secondary, primary);
        return this.map.put(primary, Map.entry(secondary, value)) == null ? null : value;
    }

    @Override
    public P getPrimaryKey(final @NotNull S secondary) {
        return this.keyMap.get(secondary);
    }

    @Override
    public S getSecondaryKey(final @NotNull P primary) {
        final var entry = this.map.get(primary);
        return entry == null ? null : entry.getKey();
    }

    @Override
    public V getByPrimaryKey(final @NotNull P primary) {
        final var entry = this.map.get(primary);
        return entry == null ? null : entry.getValue();
    }

    @Override
    public V getBySecondaryKey(final @NotNull S secondary) {
        return this.getByPrimaryKey(this.keyMap.get(secondary));
    }

    @Override
    public @Nullable V removeByPrimaryKey(final @NotNull P primary) {
        final var entry = this.map.remove(primary);
        if (entry == null) return null;
        this.keyMap.remove(entry.getKey());
        return entry.getValue();
    }

    @Override
    public @Nullable V removeBySecondaryKey(final @NotNull S secondary) {
        final P primary = this.keyMap.remove(secondary);
        return primary == null ? null : this.map.remove(primary).getValue();
    }

    @Override
    @Contract(value = "null -> false")
    public boolean containsPrimaryKey(final @Nullable P primary) {
        return primary != null && this.map.containsKey(primary);
    }

    @Override
    @Contract(value = "null -> false")
    public boolean containsSecondaryKey(final @Nullable S secondary) {
        return secondary != null && this.secondaryKeySet().contains(secondary);
    }

    @Override
    @Contract(value = "null -> false")
    public boolean containsValue(final @Nullable V value) {
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
        final var entries = this.map.values();
        final var values = new ArrayList<V>(entries.size());

        for (final var entry : entries) {
            values.add(entry.getValue());
        }

        return values;
    }
}
