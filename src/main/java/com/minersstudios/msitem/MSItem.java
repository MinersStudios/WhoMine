package com.minersstudios.msitem;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.status.FailureStatus;
import com.minersstudios.mscore.plugin.status.SuccessStatus;
import com.minersstudios.msitem.api.CustomItemType;
import com.minersstudios.msitem.listener.event.mechanic.DosimeterMechanic;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Logger;

import static com.minersstudios.mscore.plugin.status.SuccessStatus.low;

/**
 * The main class of the MSItem plugin
 *
 * @see MSPlugin
 */
public final class MSItem extends MSPlugin<MSItem> {
    private static MSItem singleton;

    private Config config;
    private Cache cache;

    public static final String NAMESPACE = "msitems";

    //<editor-fold desc="Plugin Statuses" defaultstate="collapsed">
    public static final FailureStatus FAILED_LOAD_RENAMEABLES = FailureStatus.low("FAILED_LOAD_RENAMEABLES");

    public static final SuccessStatus LOADING_ITEMS =       low("LOADING_ITEMS");
    public static final SuccessStatus LOADED_ITEMS =        low("LOADED_ITEMS");
    public static final SuccessStatus LOADING_RENAMEABLES = low("LOADING_RENAMEABLES");
    public static final SuccessStatus LOADED_RENAMEABLES =  low("LOADED_RENAMEABLES", FAILED_LOAD_RENAMEABLES);
    //</editor-fold>

    @Override
    public void load() {
        CustomItemType.load(this);
    }

    @Override
    public void enable() {
        singleton = this;
        this.cache = new Cache(this);
        this.config = new Config(this);

        this.cache.load();
        this.config.reload();

        this.runTaskTimer(
                () -> new DosimeterMechanic.DosimeterTask(this).run(),
                0L, this.config.getDosimeterCheckRate()
        );
    }

    @Override
    public void disable() {
        singleton = null;
    }

    /**
     * @return The configuration of the plugin or null if the plugin is disabled
     */
    public @UnknownNullability Config getConfiguration() {
        return this.config;
    }

    /**
     * @return The cache of the plugin or null if the plugin is disabled
     */
    public @UnknownNullability Cache getCache() {
        return this.cache;
    }

    /**
     * @return The instance of the plugin or null if the plugin is disabled
     */
    public static @UnknownNullability MSItem singleton() {
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
    public static @UnknownNullability Cache cache() {
        return singleton == null ? null : singleton.cache;
    }

    /**
     * @return The configuration of the plugin or null if the plugin is disabled
     */
    public static @UnknownNullability Config config() {
        return singleton == null ? null : singleton.config;
    }
}
