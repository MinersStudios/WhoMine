package com.minersstudios.wholib.key;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that has a {@link ResourceKey resource key}
 */
public interface ResourceKeyed {

    /**
     * Returns the resource key of this object
     *
     * @return The resource key of this object
     */
    @NotNull ResourceKey getResourceKey();
}
