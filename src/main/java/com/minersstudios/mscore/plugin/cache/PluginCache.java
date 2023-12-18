package com.minersstudios.mscore.plugin.cache;

import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

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
    public PluginCache(final @NotNull P plugin) {
        this.plugin = plugin;
    }

    /**
     * @return The plugin that owns this cache or null if the cache is not loaded
     */
    public final @UnknownNullability P getPlugin() {
        return this.plugin;
    }
}
