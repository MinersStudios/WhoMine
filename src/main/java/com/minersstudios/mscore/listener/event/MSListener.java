package com.minersstudios.mscore.listener.event;

import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.NotNull;

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
     * @throws IllegalArgumentException If the plugin is not assignable from
     *                                  this class
     */
    void register(final @NotNull P plugin) throws IllegalStateException, IllegalArgumentException;

    /**
     * @return A string representation of this listener
     */
    @Override
    @NotNull String toString();
}
