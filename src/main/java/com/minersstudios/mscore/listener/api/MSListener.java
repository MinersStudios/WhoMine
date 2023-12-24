package com.minersstudios.mscore.listener.api;

import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This interface is used for implementing listeners
 *
 * @param <P> The plugin, that this listener is registered to
 */
public interface MSListener<P extends MSPlugin<P>> {

    /**
     * @return The plugin for this listener or null if not set
     * @throws IllegalStateException If this listener is not registered
     * @see #register(MSPlugin)
     */
    @NotNull P getPlugin() throws IllegalStateException;

    /**
     * @return True if this listener is registered to a plugin
     */
    boolean isRegistered();

    /**
     * Registers this listener to the plugin
     *
     * @param plugin The plugin to register this listener to
     * @throws IllegalStateException If this listener is already registered
     */
    @ApiStatus.Internal
    void register(final @NotNull P plugin) throws IllegalStateException;

    /**
     * @return A string representation of this listener
     */
    @Override
    @NotNull String toString();
}
