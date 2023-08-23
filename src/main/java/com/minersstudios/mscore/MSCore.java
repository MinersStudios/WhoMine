package com.minersstudios.mscore;

import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.packet.ChannelHandler;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Main class of the MSCore plugin
 *
 * @see MSPlugin
 */
public final class MSCore extends MSPlugin {
    private static MSCore instance;

    public MSCore() {
        instance = this;
    }

    @Override
    public void enable() {
        if (!LanguageFile.isLoaded()) {
            LanguageFile.loadLanguage(GLOBAL_CONFIG.languageFolderLink, GLOBAL_CONFIG.languageCode);
        }

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
        return instance;
    }
}
