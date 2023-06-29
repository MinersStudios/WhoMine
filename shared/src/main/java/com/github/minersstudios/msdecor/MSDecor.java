package com.github.minersstudios.msdecor;

import com.github.minersstudios.mscore.MSPlugin;
import com.github.minersstudios.mscore.utils.MSPluginUtils;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.utils.ConfigCache;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class MSDecor extends MSPlugin {
    private static MSDecor instance;
    private static CoreProtectAPI coreProtectAPI;
    private static ConfigCache configCache;

    @Override
    public void enable() {
        instance = this;
        coreProtectAPI = CoreProtect.getInstance().getAPI();

        reloadConfigs();
    }

    public static void reloadConfigs() {
        instance.saveDefaultConfig();
        instance.reloadConfig();
        configCache = new ConfigCache();

        configCache.registerCustomDecors();
        instance.loadedCustoms = true;

        Bukkit.getScheduler().runTaskTimer(instance, task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                configCache.recipeDecors.forEach(CustomDecorData::registerRecipes);
                configCache.recipeDecors.clear();
                task.cancel();
            }
        }, 0L, 10L);
    }

    @Contract(pure = true)
    public static @NotNull MSDecor getInstance() {
        return instance;
    }

    @Contract(pure = true)
    public static @NotNull CoreProtectAPI getCoreProtectAPI() {
        return coreProtectAPI;
    }

    @Contract(pure = true)
    public static @NotNull ConfigCache getConfigCache() {
        return configCache;
    }
}
