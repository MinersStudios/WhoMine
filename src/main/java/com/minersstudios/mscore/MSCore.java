package com.minersstudios.mscore;

import com.minersstudios.mscore.language.LanguageFile;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.utility.CoreProtectUtils;
import com.minersstudios.mscore.utility.PaperUtils;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Logger;

import static com.minersstudios.mscore.language.LanguageRegistry.Keys.ERROR_NO_PERMISSION;
import static com.minersstudios.mscore.utility.Font.Chars.RED_EXCLAMATION_MARK;

/**
 * Main class of the MSCore plugin
 *
 * @see MSPlugin
 */
public final class MSCore extends MSPlugin<MSCore> {
    private static MSCore singleton;

    public static final String NAMESPACE = "mscore";

    //<editor-fold desc="Config keys" defaultstate="collapsed">
    private static final String CONNECTION_THROTTLE = "messages.kick.connection-throttle";
    private static final String FLYING_PLAYER = "messages.kick.flying-player";
    private static final String FLYING_VEHICLE = "messages.kick.flying-vehicle";
    private static final String NO_PERMISSION = "messages.no-permission";
    private static final String TOO_MANY_PACKETS = "packet-limiter.kick-message";
    //</editor-fold>

    //<editor-fold desc="Config messages" defaultstate="collapsed">
    private static final String MESSAGE_CONNECTION_THROTTLE = "<red><lang:" + LanguageRegistry.Keys.ERROR_CONNECTION_THROTTLE + '>';
    private static final String MESSAGE_FLYING_PLAYER = "<red><lang:" + LanguageRegistry.Keys.ERROR_FLYING_PLAYER + '>';
    private static final String MESSAGE_FLYING_VEHICLE = "<red><lang:" + LanguageRegistry.Keys.ERROR_FLYING_VEHICLE + '>';
    private static final String MESSAGE_NO_PERMISSION = ' ' + RED_EXCLAMATION_MARK + " <red><lang:" + ERROR_NO_PERMISSION + '>';
    private static final String MESSAGE_TOO_MANY_PACKETS = "<red><lang:" + LanguageRegistry.Keys.ERROR_TOO_MANY_PACKETS + '>';
    //</editor-fold>

    @Override
    public void load() {
        PaperUtils
        .editConfig(PaperUtils.ConfigType.GLOBAL, this.getServer())
        .set(CONNECTION_THROTTLE, MESSAGE_CONNECTION_THROTTLE)
        .set(FLYING_PLAYER, MESSAGE_FLYING_PLAYER)
        .set(FLYING_VEHICLE, MESSAGE_FLYING_VEHICLE)
        .set(NO_PERMISSION, MESSAGE_NO_PERMISSION)
        .set(TOO_MANY_PACKETS, MESSAGE_TOO_MANY_PACKETS)
        .save();
    }

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

            MSLogger.warning("CoreProtect is not Enabled, actions logging will not be available");
        } catch (final IllegalStateException e) {
            MSLogger.warning("CoreProtect is already connected");
        } catch (final NoClassDefFoundError e) {
            MSLogger.warning("CoreProtect is not installed, actions logging will not be available");
        } catch (final NullPointerException e) {
            MSLogger.warning("CoreProtectAPI is not running yet");
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
