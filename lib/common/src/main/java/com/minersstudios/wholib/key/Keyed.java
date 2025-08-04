package com.minersstudios.wholib.key;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that has a {@link Key key}
 */
public interface Keyed {

    /**
     * Returns the key of this object
     *
     * @return The key of this object
     */
    @Key @NotNull String getKey();
}
