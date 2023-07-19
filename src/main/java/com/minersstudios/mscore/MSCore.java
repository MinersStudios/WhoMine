package com.minersstudios.mscore;

import com.comphenix.protocol.ProtocolLibrary;
import com.minersstudios.mscore.config.Config;
import com.minersstudios.mscore.config.LanguageFile;
import com.minersstudios.mscore.listeners.player.PlayerUpdateSignListener;
import com.minersstudios.mscore.plugin.MSPlugin;
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
        config = new Config(this, this.getConfigFile());

        config.reload();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PlayerUpdateSignListener(this));
        LanguageFile.loadLanguage(config.languageUrl, config.languageCode);
    }

    @Override
    public void disable() {
        LanguageFile.unloadLanguage();
    }

    /**
     * @return The instance of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull MSCore getInstance() throws NullPointerException {
        return singleton;
    }

    /**
     * @return The configuration of the plugin
     * @throws NullPointerException If the plugin is not enabled
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
