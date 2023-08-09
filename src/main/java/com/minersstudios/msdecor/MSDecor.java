package com.minersstudios.msdecor;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.util.MSPluginUtils;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msdecor.utils.ConfigCache;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public final class MSDecor extends MSPlugin {
    private static MSDecor instance;
    private CoreProtectAPI coreProtectAPI;
    private ConfigCache configCache;

    @Override
    public void enable() {
        instance = this;
        coreProtectAPI = CoreProtect.getInstance().getAPI();

        reloadConfigs();
    }

    public static void reloadConfigs() {
        instance.saveDefaultConfig();
        instance.reloadConfig();
        instance.configCache = new ConfigCache();

        instance.configCache.registerCustomDecors();
        instance.setLoadedCustoms(true);

        instance.runTaskTimer(task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                task.cancel();
                instance.configCache.recipeDecors.forEach(CustomDecorData::registerRecipes);
                instance.configCache.recipeDecors.clear();
            }
        }, 0L, 10L);
    }

    public static MSDecor getInstance() {
        return instance;
    }

    public static ConfigCache getConfigCache() {
        return instance.configCache;
    }

    public static CoreProtectAPI getCoreProtectAPI() {
        return instance.coreProtectAPI;
    }
}
