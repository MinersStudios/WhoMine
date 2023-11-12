package com.minersstudios.mscore.collection;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface DualMap<P, S, V> {

    V put(
            final P primary,
            final S secondary,
            final V value
    );

    P getPrimaryKey(final S secondary);

    S getSecondaryKey(final P primary);

    V getByPrimaryKey(final P primary);

    V getBySecondaryKey(final S secondary);

    V removeByPrimaryKey(final P primary);

    V removeBySecondaryKey(final S secondary);

    @Contract("null -> false")
    boolean containsPrimaryKey(final P primary);

    @Contract("null -> false")
    boolean containsSecondaryKey(final S secondary);

    @Contract("null -> false")
    boolean containsValue(final V value);

    void clear();

    int size();

    boolean isEmpty();

    @NotNull Set<P> primaryKeySet();

    @NotNull Set<S> secondaryKeySet();

    @NotNull Collection<V> values();
}
