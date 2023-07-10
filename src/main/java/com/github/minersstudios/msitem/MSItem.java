package com.github.minersstudios.msitem;

import com.github.minersstudios.mscore.MSPlugin;
import com.github.minersstudios.mscore.utils.MSPluginUtils;
import com.github.minersstudios.msitem.items.CustomItem;
import com.github.minersstudios.msitem.items.RenamesMenu;
import com.github.minersstudios.msitem.listeners.mechanic.DosimeterMechanic;
import com.github.minersstudios.msitem.utils.ConfigCache;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public final class MSItem extends MSPlugin {
    private static MSItem instance;
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
        RenamesMenu.update();
        instance.setLoadedCustoms(true);

        configCache.bukkitTasks.add(instance.runTaskTimer(DosimeterMechanic.DosimeterTask::run, 0L, configCache.dosimeterCheckRate));

        instance.runTaskTimer(task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                configCache.recipeItems.forEach(CustomItem::registerRecipes);
                configCache.recipeItems.clear();
                task.cancel();
            }
        }, 0L, 10L);
    }

    public static @NotNull MSItem getInstance() {
        return instance;
    }

    public static @NotNull ConfigCache getConfigCache() {
        return configCache;
    }
}
