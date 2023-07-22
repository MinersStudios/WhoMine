package com.minersstudios.mscore;

import com.minersstudios.mscore.config.Config;
import com.minersstudios.mscore.config.LanguageFile;
import com.minersstudios.mscore.packet.ChannelHandler;
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

    @Override
    public void enable() {
        singleton = this;
        config = new Config(this, this.getConfigFile());

        config.reload();
        LanguageFile.loadLanguage(config.languageUser, config.languageRepo, config.languageCode);
        this.getServer().getOnlinePlayers().forEach(player -> ChannelHandler.injectPlayer(player, this));
    }

    @Override
    public void disable() {
        LanguageFile.unloadLanguage();
        this.getServer().getOnlinePlayers().forEach(ChannelHandler::uninjectPlayer);
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
}
