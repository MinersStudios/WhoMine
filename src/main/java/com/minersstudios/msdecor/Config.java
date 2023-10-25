package com.minersstudios.msdecor;

import com.minersstudios.mscore.plugin.config.MSConfig;
import com.minersstudios.mscore.util.MSPluginUtils;
import com.minersstudios.msdecor.api.CustomDecorData;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class Config extends MSConfig {
    private final MSDecor plugin;

    public boolean isChristmas;
    public boolean isHalloween;

    /**
     * Configuration constructor
     *
     * @param file The config file, where the configuration is stored
     * @throws IllegalArgumentException If the given file does not exist
     */
    public Config(
            final @NotNull MSDecor plugin,
            final @NotNull File file
    ) throws IllegalArgumentException {
        super(file);
        this.plugin = plugin;
    }

    @Override
    public void reloadVariables() {
        this.isChristmas = this.yaml.getBoolean("is-christmas");
        this.isHalloween = this.yaml.getBoolean("is-halloween");

        this.plugin.setLoadedCustoms(true);
        this.plugin.runTaskTimer(task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                task.cancel();

                final Cache cache = MSDecor.getCache();

                cache.recipeDecors.forEach(CustomDecorData::registerRecipes);
                cache.recipeDecors.clear();
            }
        }, 0L, 10L);
    }

    @Override
    public void reloadDefaultVariables() {
        this.setIfNotExists("is-christmas", false);
        this.setIfNotExists("is-halloween", false);
    }
}
