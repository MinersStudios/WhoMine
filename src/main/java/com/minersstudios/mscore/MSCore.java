package com.minersstudios.mscore;

import com.minersstudios.mscore.language.LanguageFile;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.utility.CoreProtectUtils;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Logger;

/**
 * Main class of the MSCore plugin
 *
 * @see MSPlugin
 */
public final class MSCore extends MSPlugin<MSCore> {
    private static MSCore singleton;

    public static final String NAMESPACE = "mscore";

    @Override
    public void enable() {
        singleton = this;

        this.setupCoreProtect();

        if (!LanguageFile.isLoaded()) {
            LanguageFile.loadLanguage(
                    globalConfig().getLanguageFolderLink(),
                    globalConfig().getLanguageCode()
            );
        }
    }

    @Override
    public void disable() {
        singleton = null;

        LanguageFile.unloadLanguage();
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
     * @return The singleton of the plugin or null if the plugin is disabled
     */
    public static @UnknownNullability MSCore singleton() {
        return singleton;
    }

    /**
     * @return The logger of the plugin or null if the plugin is disabled
     */
    public static @UnknownNullability Logger logger() {
        return singleton == null ? null : singleton.getLogger();
    }

    /**
     * @return The component logger of the plugin or null if the plugin is disabled
     */
    public static @UnknownNullability ComponentLogger componentLogger() {
        return singleton == null ? null : singleton.getComponentLogger();
    }
}
