package com.github.minersstudios.msdecor;

import com.github.minersstudios.mscore.MSPlugin;
import com.github.minersstudios.mscore.utils.MSPluginUtils;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.utils.ConfigCache;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
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
        instance.setLoadedCustoms(true);

        instance.runTaskTimer(task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                configCache.recipeDecors.forEach(CustomDecorData::registerRecipes);
                configCache.recipeDecors.clear();
                task.cancel();
            }
        }, 0L, 10L);
    }

    public static @NotNull MSDecor getInstance() {
        return instance;
    }

    public static @NotNull CoreProtectAPI getCoreProtectAPI() {
        return coreProtectAPI;
    }

    public static @NotNull ConfigCache getConfigCache() {
        return configCache;
    }
}
