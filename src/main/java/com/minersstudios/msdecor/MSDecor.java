package com.minersstudios.msdecor;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.utility.ItemUtils;
import com.minersstudios.msdecor.api.CustomDecorType;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Logger;

/**
 * The main class of the MSDecor plugin
 *
 * @see MSPlugin
 */
public final class MSDecor extends MSPlugin<MSDecor> {
    private static MSDecor singleton;

    public static final String NAMESPACE = "msdecor";

    @Override
    public void load() {
        ItemUtils.setMaxStackSize(Material.LEATHER_HORSE_ARMOR, 8);
        CustomDecorType.load(this);
    }

    @Override
    public void enable() {
        singleton = this;
    }

    @Override
    public void disable() {
        singleton = null;
    }

    /**
     * @return The instance of the plugin or null if the plugin is disabled
     */
    public static @UnknownNullability MSDecor singleton() {
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
