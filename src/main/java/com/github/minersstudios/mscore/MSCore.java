package com.github.minersstudios.mscore;

import com.github.minersstudios.mscore.config.Config;
import com.github.minersstudios.mscore.config.LanguageFile;
import org.jetbrains.annotations.NotNull;

/**
 * Main class of the plugin
 *
 * @see MSPlugin
 */
public final class MSCore extends MSPlugin {
    private static MSCore singleton;
    private static Config config;

    private static final Cache CACHE = new Cache();

    @Override
    public void enable() {
        singleton = this;
        config = new Config(this.getConfigFile());

        config.reload();
        LanguageFile.loadLanguage(config.languageUrl, config.languageCode);
    }

    @Override
    public void disable() {
        LanguageFile.unloadLanguage();
    }

    /**
     * @return The instance of the plugin,
     *         or null if the plugin is not enabled
     */
    public static @NotNull MSCore getInstance() throws NullPointerException {
        return singleton;
    }

    /**
     * @return The configuration of the plugin,
     *         or null if the plugin is not enabled
     */
    public static @NotNull Config getConfiguration() throws NullPointerException {
        return config;
    }

    /**
     * @return The cache of the plugin
     */
    public static @NotNull Cache getCache() {
        return CACHE;
    }
}
