package com.minersstudios.mscore.resource;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceManager {

    /**
     * Opens and returns the input stream of the resource
     *
     * @return The input stream of the resource
     */
    @NotNull InputStream openStream() throws IOException;

    /**
     * Returns the string representation of this resource manager
     *
     * @return The string representation of this resource manager
     */
    @Override
    @NotNull String toString();
}
