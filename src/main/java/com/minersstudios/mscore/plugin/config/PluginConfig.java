package com.minersstudios.mscore.plugin.config;

import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Abstract plugin configuration class
 *
 * @param <P> The plugin type that owns this config
 * @see Config
 */
public abstract class PluginConfig<P extends MSPlugin<P>> extends Config {
    private final P plugin;

    /**
     * Configuration constructor
     *
     * @param plugin The plugin that owns this config
     * @see #PluginConfig(P, File)
     */
    protected PluginConfig(final @NotNull P plugin) {
        this(plugin, plugin.getConfigFile());
    }

    /**
     * Configuration constructor
     *
     * @param plugin The plugin that owns this config
     * @param file   The config file, where the configuration is stored
     */
    protected PluginConfig(
            final @NotNull P plugin,
            final @NotNull File file
    ) {
        super(file);

        this.plugin = plugin;
    }

    /**
     * Returns the plugin that owns this config
     *
     * @return The plugin that owns this config
     */
    public final @NotNull P getPlugin() {
        return this.plugin;
    }
}
