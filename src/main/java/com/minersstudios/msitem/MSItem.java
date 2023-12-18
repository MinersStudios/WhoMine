package com.minersstudios.msitem;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msitem.api.CustomItemType;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Logger;

/**
 * The main class of the MSItem plugin
 *
 * @see MSPlugin
 */
public final class MSItem extends MSPlugin<MSItem> {
    private static MSItem singleton;

    private Config config;
    private Cache cache;

    public MSItem() {
        singleton = this;
    }

    @Override
    public void load() {
        this.cache = new Cache(this);
        this.config = new Config(this, this.getConfigFile());

        initClass(CustomItemType.class);
    }

    @Override
    public void enable() {
        this.cache.load();
        this.config.reload();
    }

    @Override
    public void disable() {
        this.cache.unload();
    }

    /**
     * @return The configuration of the plugin
     *         or null if the plugin is not enabled
     */
    public @UnknownNullability Config getConfiguration() {
        return this.config;
    }

    /**
     * @return The cache of the plugin
     *         or null if the plugin is not enabled
     */
    public @UnknownNullability Cache getCache() {
        return this.cache;
    }

    /**
     * @return The instance of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability MSItem singleton() {
        return singleton;
    }

    /**
     * @return The logger of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability Logger logger() {
        return singleton == null ? null : singleton.getLogger();
    }

    /**
     * @return The component logger of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability ComponentLogger componentLogger() {
        return singleton == null ? null : singleton.getComponentLogger();
    }

    /**
     * @return The cache of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability Cache cache() {
        return singleton == null ? null : singleton.cache;
    }

    /**
     * @return The configuration of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability Config config() {
        return singleton == null ? null : singleton.config;
    }
}
