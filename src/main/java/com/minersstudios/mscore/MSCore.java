package com.minersstudios.mscore;

import com.minersstudios.mscore.packet.ChannelHandler;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.util.CoreProtectUtils;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Logger;

/**
 * Main class of the MSCore plugin
 *
 * @see MSPlugin
 */
public final class MSCore extends MSPlugin<MSCore> {
    private static MSCore singleton;

    public MSCore() {
        singleton = this;
    }

    @Override
    public void enable() {
        this.setupCoreProtect();

        if (!LanguageFile.isLoaded()) {
            LanguageFile.loadLanguage(
                    GLOBAL_CONFIG.getLanguageFolderLink(),
                    GLOBAL_CONFIG.getLanguageCode()
            );
        }

        for (final var connection : MinecraftServer.getServer().getConnection().getConnections()) {
            ChannelHandler.injectConnection(connection, this);
        }
    }

    @Override
    public void disable() {
        LanguageFile.unloadLanguage();

        for (final var connection : MinecraftServer.getServer().getConnection().getConnections()) {
            ChannelHandler.uninjectConnection(connection);
        }
    }

    /**
     * Setups the CoreProtectAPI if the plugin is installed
     *
     * @see CoreProtectUtils
     * @see CoreProtectUtils#set(CoreProtectAPI)
     */
    public void setupCoreProtect() {
        try {
            final CoreProtect coreProtect = CoreProtect.getInstance();

            if (coreProtect.isEnabled()) {
                CoreProtectUtils.set(coreProtect.getAPI());
                MSLogger.fine("CoreProtect connected");
                return;
            }

            MSLogger.severe("CoreProtect is not Enabled, actions logging will not be available");
        } catch (final IllegalStateException e) {
            MSLogger.severe("CoreProtect is already connected");
        } catch (final NoClassDefFoundError e) {
            MSLogger.severe("CoreProtect is not installed, actions logging will not be available");
        } catch (final NullPointerException e) {
            MSLogger.severe("CoreProtectAPI is not running yet");
        }
    }

    /**
     * @return The singleton of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability MSCore singleton() {
        return singleton;
    }

    /**
     * @return The logger of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability Logger logger() {
        return singleton == null ? null : singleton.getLogger();
    }

    /**
     * @return The component logger of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability ComponentLogger componentLogger() {
        return singleton == null ? null : singleton.getComponentLogger();
    }
}
