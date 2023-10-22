package com.minersstudios.mscore;

import com.minersstudios.mscore.packet.ChannelHandler;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Main class of the MSCore plugin
 *
 * @see MSPlugin
 */
public final class MSCore extends MSPlugin<MSCore> {
    private static MSCore instance;

    public MSCore() {
        instance = this;
    }

    @Override
    public void enable() {
        if (!LanguageFile.isLoaded()) {
            LanguageFile.loadLanguage(GLOBAL_CONFIG.languageFolderLink, GLOBAL_CONFIG.languageCode);
        }

        MinecraftServer.getServer().getConnection().getConnections()
            .forEach(connection -> ChannelHandler.injectConnection(connection, this));
    }

    @Override
    public void disable() {
        LanguageFile.unloadLanguage();
        MinecraftServer.getServer().getConnection().getConnections()
            .forEach(ChannelHandler::uninjectConnection);
    }

    /**
     * @return The instance of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull MSCore getInstance() throws NullPointerException {
        return instance;
    }

    /**
     * @return The logger of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Logger logger() throws NullPointerException {
        return instance.getLogger();
    }

    /**
     * @return The component logger of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull ComponentLogger componentLogger() throws NullPointerException {
        return instance.getComponentLogger();
    }
}
