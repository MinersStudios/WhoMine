package com.minersstudios.mscore.plugin.config;

import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class PluginConfig<P extends MSPlugin<P>> extends MSConfig {
    private final P plugin;

    /**
     * Configuration constructor. All variables must be initialized in
     * {@link #reloadVariables()}.
     *
     * @param plugin The plugin that owns this config
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public PluginConfig(
            final @NotNull P plugin,
            final @NotNull File file
    ) throws IllegalArgumentException {
        super(file);

        this.plugin = plugin;
    }

    /**
     * @return The plugin that owns this config
     */
    public final @NotNull P getPlugin() {
        return this.plugin;
    }
}
