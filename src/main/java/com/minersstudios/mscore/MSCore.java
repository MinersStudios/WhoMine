package com.minersstudios.mscore;

import com.minersstudios.mscore.annotation.Namespace;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.utility.CoreProtectUtils;
import com.minersstudios.mscore.utility.PaperUtils;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Logger;

import static com.minersstudios.mscore.locale.Translations.*;
import static com.minersstudios.mscore.utility.Font.Chars.RED_EXCLAMATION_MARK;

/**
 * Main class of the MSCore plugin
 *
 * @see MSPlugin
 */
public final class MSCore extends MSPlugin<MSCore> {
    private static MSCore singleton;

    /** The namespace of the plugin */
    public static final @Namespace String NAMESPACE = "mscore";

    //<editor-fold desc="Config keys" defaultstate="collapsed">
    private static final String KEY_CONNECTION_THROTTLE = "messages.kick.connection-throttle";
    private static final String KEY_FLYING_PLAYER =       "messages.kick.flying-player";
    private static final String KEY_FLYING_VEHICLE =      "messages.kick.flying-vehicle";
    private static final String KEY_NO_PERMISSION =       "messages.no-permission";
    private static final String KEY_TOO_MANY_PACKETS =    "packet-limiter.kick-message";
    //</editor-fold>

    //<editor-fold desc="Config values" defaultstate="collapsed">
    private static final String VALUE_CONNECTION_THROTTLE = "<red><lang:" + ERROR_CONNECTION_THROTTLE.getPath() + '>';
    private static final String VALUE_FLYING_PLAYER =       "<red><lang:" + ERROR_FLYING_PLAYER.getPath() + '>';
    private static final String VALUE_FLYING_VEHICLE =      "<red><lang:" + ERROR_FLYING_VEHICLE.getPath() + '>';
    private static final String VALUE_NO_PERMISSION =       ' ' + RED_EXCLAMATION_MARK + " <red><lang:" + ERROR_NO_PERMISSION.getPath() + '>';
    private static final String VALUE_TOO_MANY_PACKETS =    "<red><lang:" + ERROR_TOO_MANY_PACKETS.getPath() + '>';
    //</editor-fold>

    @Override
    public void load() {
        PaperUtils
        .editConfig(PaperUtils.ConfigType.GLOBAL, this.getServer())
        .set(KEY_CONNECTION_THROTTLE, VALUE_CONNECTION_THROTTLE)
        .set(KEY_FLYING_PLAYER,       VALUE_FLYING_PLAYER)
        .set(KEY_FLYING_VEHICLE,      VALUE_FLYING_VEHICLE)
        .set(KEY_NO_PERMISSION,       VALUE_NO_PERMISSION)
        .set(KEY_TOO_MANY_PACKETS,    VALUE_TOO_MANY_PACKETS)
        .save();
    }

    @Override
    public void enable() {
        singleton = this;

        this.setupCoreProtect();
    }

    @Override
    public void disable() {
        singleton = null;
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

            if (coreProtect == null) {
                MSLogger.warning("CoreProtectAPI is not running yet");
            } else if (coreProtect.isEnabled()) {
                CoreProtectUtils.set(coreProtect.getAPI());
                MSLogger.fine("CoreProtect connected");
            } else {
                MSLogger.warning("CoreProtect is not Enabled, actions logging will not be available");
            }
        } catch (final IllegalStateException e) {
            MSLogger.warning("CoreProtect is already connected");
        } catch (final NoClassDefFoundError e) {
            MSLogger.warning("CoreProtect is not installed, actions logging will not be available");
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
