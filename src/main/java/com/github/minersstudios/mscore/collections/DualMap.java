package com.github.minersstudios.mscore.collections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface DualMap<P, S, V> {

    V put(
            P primary,
            S secondary,
            V value
    );

    P getPrimaryKey(S secondary);

    S getSecondaryKey(P primary);

    V getByPrimaryKey(P primary);

    V getBySecondaryKey(S secondary);

    V removeByPrimaryKey(P primary);

    V removeBySecondaryKey(S secondary);

    @Contract(value = "null -> false", pure = true)
    boolean containsPrimaryKey(P primary);

    @Contract(value = "null -> false", pure = true)
    boolean containsSecondaryKey(S secondary);

    @Contract(value = "null -> false", pure = true)
    boolean containsValue(V value);

    void clear();

    int size();

    boolean isEmpty();

    @NotNull Set<P> primaryKeySet();

    @NotNull Set<S> secondaryKeySet();

    @NotNull Collection<V> values();
}
