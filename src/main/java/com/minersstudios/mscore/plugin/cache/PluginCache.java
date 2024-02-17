package com.minersstudios.mscore.plugin.cache;

import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * This abstract class represents a cache for a plugin. It provides methods to
 * load and unload the cache, and to check if the cache is loaded. Subclasses
 * must implement the {@link #onLoad()} and {@link #onUnload()} methods to
 * define what happens when the cache is loaded and unloaded.
 */
public abstract class PluginCache<P extends MSPlugin<P>> extends MSCache {
    private final P plugin;

    /**
     * Cache constructor
     *
     * @param plugin The plugin that owns this cache
     */
    protected PluginCache(final @NotNull P plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the plugin that owns this cache
     *
     * @return The plugin that owns this cache
     */
    public final @NotNull P getPlugin() {
        return this.plugin;
    }

    /**
     * Returns a string representation of this cache
     *
     * @return A string representation of this cache
     */
    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() +
                "{plugin=" + this.plugin.getName() +
                ", isLoaded=" + this.isLoaded() +
                '}';
    }
}
