package com.github.minersstudios.msitems;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.MSPlugin;
import com.github.minersstudios.mscore.utils.MSPluginUtils;
import com.github.minersstudios.msitems.items.CustomItem;
import com.github.minersstudios.msitems.items.RenameableItem;
import com.github.minersstudios.msitems.listeners.mechanic.DosimeterMechanic;
import com.github.minersstudios.msitems.utils.ConfigCache;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class MSItems extends MSPlugin {
    private static MSItems instance;
    private static ConfigCache configCache;

    @Override
    public void enable() {
        instance = this;

        reloadConfigs();
    }

    public static void reloadConfigs() {
        instance.saveResource("items/example.yml", true);
        instance.saveDefaultConfig();
        instance.reloadConfig();

        if (configCache != null) {
            configCache.bukkitTasks.forEach(BukkitTask::cancel);
        }

        configCache = new ConfigCache();

        configCache.registerItems();
        instance.loadedCustoms = true;

        MSCore.getCache().customInventoryMap.put("renames_inventory", RenameableItem.Menu.create());

        configCache.bukkitTasks.add(Bukkit.getScheduler().runTaskTimer(instance, DosimeterMechanic.DosimeterTask::run, 0L, configCache.dosimeterCheckRate));

        Bukkit.getScheduler().runTaskTimer(instance, task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                configCache.recipeItems.forEach(CustomItem::registerRecipes);
                configCache.recipeItems.clear();
                task.cancel();
            }
        }, 0L, 10L);
    }

    @Contract(pure = true)
    public static @NotNull MSItems getInstance() {
        return instance;
    }

    @Contract(pure = true)
    public static @NotNull ConfigCache getConfigCache() {
        return configCache;
    }
}
