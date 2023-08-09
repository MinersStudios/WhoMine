package com.minersstudios.msitem;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msitem.item.CustomItemType;

/**
 * The main class of the MSItem plugin
 *
 * @see MSPlugin
 */
public final class MSItem extends MSPlugin {
    private static MSItem instance;
    private Config config;
    private Cache cache;

    @Override
    public void load() {
        initClass(CustomItemType.class);
    }

    @Override
    public void enable() {
        instance = this;
        this.cache = new Cache();
        this.config = new Config(this, this.getConfigFile());

        this.config.reload();
    }

    /**
     * @return The instance of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static MSItem getInstance() throws NullPointerException {
        return instance;
    }

    /**
     * @return The cache of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static Cache getCache() throws NullPointerException {
        return instance.cache;
    }

    /**
     * @return The configuration of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static Config getConfiguration() throws NullPointerException {
        return instance.config;
    }
}
