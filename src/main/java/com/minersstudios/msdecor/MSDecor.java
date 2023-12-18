package com.minersstudios.msdecor;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msdecor.api.CustomDecorType;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.bukkit.Material;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class of the MSDecor plugin
 *
 * @see MSPlugin
 */
public final class MSDecor extends MSPlugin<MSDecor> {
    private static MSDecor singleton;

    public static final String NAMESPACE = "msdecor";

    public MSDecor() {
        singleton = this;
    }

    @Override
    @SuppressWarnings("JavaReflectionMemberAccess")
    public void load() {
        initClass(CustomDecorType.class);

        try {
            final Field item = Item.class.getDeclaredField("d"); // "maxStackSize" field : https://nms.screamingsandals.org/1.20.2/net/minecraft/world/item/Item.html

            item.setAccessible(true);
            item.setInt(Items.LEATHER_HORSE_ARMOR, 8);

            final Field material = Material.class.getDeclaredField("maxStack");

            material.setAccessible(true);
            material.setInt(Material.LEATHER_HORSE_ARMOR, 8);
        } catch (final Throwable e) {
            this.getLogger().log(
                    Level.SEVERE,
                    "Failed to set max stack size for leather horse armor",
                    e
            );
        }
    }

    /**
     * @return The instance of the plugin
     *         or null if the plugin is not enabled
     */
    public static @UnknownNullability MSDecor singleton() {
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
