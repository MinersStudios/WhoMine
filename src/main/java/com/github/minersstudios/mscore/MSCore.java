package com.github.minersstudios.mscore;

import com.github.minersstudios.mscore.config.Config;
import com.github.minersstudios.mscore.config.LanguageFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Main class of the plugin
 *
 * @see MSPlugin
 */
public final class MSCore extends MSPlugin {
    private static MSCore singleton;
    private static Cache cache;
    private static Config config;

    @Override
    public void load() {
        cache = new Cache();
    }

    @Override
    public void enable() {
        singleton = this;
        config = new Config();

        LanguageFile.loadLanguage(config.languageUrl, config.languageCode);
    }

    @Override
    public void disable() {
        LanguageFile.unloadLanguage();
    }

    @Contract(pure = true)
    public static @NotNull MSCore getInstance() {
        return singleton;
    }

    @Contract(pure = true)
    public static @NotNull Cache getCache() {
        return cache;
    }

    @Contract(pure = true)
    public static @NotNull Config getConfiguration() {
        return config;
    }
}
