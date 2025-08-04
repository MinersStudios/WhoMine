package com.minersstudios.wholib.key;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that has a {@link Resource resource}
 */
public interface Resourced {

    /**
     * Returns the resource of this object
     *
     * @return The resource of this object
     */
    @Resource @NotNull String getResource();
}
