package com.minersstudios.msdecor;

import com.minersstudios.mscore.plugin.config.PluginConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class Config extends PluginConfig<MSDecor> {
    public boolean isChristmas;
    public boolean isHalloween;

    /**
     * Configuration constructor
     *
     * @param plugin The plugin that owns this config
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            final @NotNull MSDecor plugin,
            final @NotNull File file
    ) throws IllegalArgumentException {
        super(plugin, file);
    }

    @Override
    public void reloadVariables() {
        this.isChristmas = this.yaml.getBoolean("is-christmas");
        this.isHalloween = this.yaml.getBoolean("is-halloween");

        this.getPlugin().setLoadedCustoms(true);
    }

    @Override
    public void reloadDefaultVariables() {
        this.setIfNotExists("is-christmas", false);
        this.setIfNotExists("is-halloween", false);
    }
}
