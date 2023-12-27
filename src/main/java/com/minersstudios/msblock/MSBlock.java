package com.minersstudios.msblock;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.status.FailureStatus;
import com.minersstudios.mscore.plugin.status.SuccessStatus;
import com.minersstudios.mscore.utility.PaperUtils;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Logger;

import static com.minersstudios.mscore.plugin.status.SuccessStatus.low;

/**
 * The main class of the MSBlock plugin
 *
 * @see MSPlugin
 */
public final class MSBlock extends MSPlugin<MSBlock> {
    private static MSBlock singleton;

    private Cache cache;
    private Config config;

    public static final String NAMESPACE = "msblock";

    //<editor-fold desc="Plugin Statuses" defaultstate="collapsed">
    public static final FailureStatus FAILED_LOAD_BLOCKS = FailureStatus.low("FAILED_LOAD_BLOCKS");

    public static final SuccessStatus LOADING_BLOCKS = low("LOADING_BLOCKS");
    public static final SuccessStatus LOADED_BLOCKS =  low("LOADED_BLOCKS", FAILED_LOAD_BLOCKS);
    //</editor-fold>

    private static final String NOTE_BLOCK_UPDATES = "block-updates.disable-noteblock-updates";

    @Override
    public void load() {
        PaperUtils
        .editConfig(PaperUtils.ConfigType.GLOBAL, this.getServer())
        .set(NOTE_BLOCK_UPDATES, true)
        .save();
    }

    @Override
    public void enable() {
        singleton = this;
        this.cache = new Cache(this);
        this.config = new Config(this);

        this.cache.load();
        this.config.reload();
    }

    @Override
    public void disable() {
        singleton = null;
    }

    /**
     * @return The cache of the plugin or null if the plugin is disabled
     */
    public @UnknownNullability Cache getCache() {
        return this.cache;
    }

    /**
     * @return The configuration of the plugin or null if the plugin is disabled
     */
    public @UnknownNullability Config getConfiguration() {
        return this.config;
    }

    /**
     * @return The instance of the plugin or null if the plugin is disabled
     */
    public static @UnknownNullability MSBlock singleton() {
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

    /**
     * @return The cache of the plugin or null if the plugin is disabled
     */
    public static @UnknownNullability Cache cache()  {
        return singleton == null ? null : singleton.cache;
    }

    /**
     * @return The configuration of the plugin or null if the plugin is disabled
     */
    public static @UnknownNullability Config config() {
        return singleton == null ? null : singleton.config;
    }
}
