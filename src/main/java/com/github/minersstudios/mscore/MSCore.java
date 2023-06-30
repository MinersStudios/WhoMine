package com.github.minersstudios.mscore;

import com.github.minersstudios.mscore.config.Config;
import com.github.minersstudios.mscore.config.LanguageFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * Main class of the plugin
 *
 * @see MSPlugin
 */
@SuppressWarnings("unused")
public final class MSCore extends MSPlugin {
    private static MSCore instance;
    private static Cache cache;
    private static Config config;

    @Override
    public void enable() {
        instance = this;
        cache = new Cache();
        config = new Config();

        LanguageFile.loadLanguage(config.languageUrl, config.languageCode);
    }

    @Override
    public void disable() {
        LanguageFile.unloadLanguage();
    }

    @Contract(pure = true)
    public static @NotNull Cache getCache() {
        return cache;
    }

    @Contract(pure = true)
    public static @NotNull Config getConfiguration() {
        return config;
    }

    @Contract(pure = true)
    public static @NotNull MSCore getInstance() {
        return instance;
    }
}
