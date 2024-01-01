package com.minersstudios.mscustoms;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.plugin.status.FailureStatus;
import com.minersstudios.mscore.plugin.status.SuccessStatus;
import com.minersstudios.mscore.utility.ItemUtils;
import com.minersstudios.mscore.utility.PaperUtils;
import com.minersstudios.mscustoms.custom.decor.CustomDecorType;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import com.minersstudios.mscustoms.listener.mechanic.DosimeterMechanic;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.jetbrains.annotations.UnknownNullability;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static com.minersstudios.mscore.plugin.status.SuccessStatus.low;

public class MSCustoms extends MSPlugin<MSCustoms> {
    private static MSCustoms singleton;

    private Cache cache;
    private Config config;

    //<editor-fold desc="Plugin Statuses" defaultstate="collapsed">
    public static final FailureStatus FAILED_LOAD_BLOCKS =      FailureStatus.low("FAILED_LOAD_BLOCKS");
    public static final FailureStatus FAILED_LOAD_RENAMEABLES = FailureStatus.low("FAILED_LOAD_RENAMEABLES");

    public static final SuccessStatus LOADING_DECORATIONS = low("LOADING_DECORATIONS");
    public static final SuccessStatus LOADED_DECORATIONS =  low("LOADED_DECORATIONS");
    public static final SuccessStatus LOADING_BLOCKS =      low("LOADING_BLOCKS");
    public static final SuccessStatus LOADED_BLOCKS =       low("LOADED_BLOCKS", FAILED_LOAD_BLOCKS);
    public static final SuccessStatus LOADING_ITEMS =       low("LOADING_ITEMS");
    public static final SuccessStatus LOADED_ITEMS =        low("LOADED_ITEMS");
    public static final SuccessStatus LOADING_RENAMEABLES = low("LOADING_RENAMEABLES");
    public static final SuccessStatus LOADED_RENAMEABLES =  low("LOADED_RENAMEABLES", FAILED_LOAD_RENAMEABLES);
    //</editor-fold>

    private static final String NOTE_BLOCK_UPDATES = "block-updates.disable-noteblock-updates";

    @Override
    public void load() {
        PaperUtils
        .editConfig(PaperUtils.ConfigType.GLOBAL, this.getServer())
        .set(NOTE_BLOCK_UPDATES, true)
        .save();
        ItemUtils.setMaxStackSize(Material.LEATHER_HORSE_ARMOR, 8);

        CompletableFuture.runAsync(() -> CustomDecorType.load(this));
        CompletableFuture.runAsync(() -> CustomItemType.load(this));
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
    public static @UnknownNullability MSCustoms singleton() {
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
