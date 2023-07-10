package com.github.minersstudios.msblock;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.msblock.listeners.block.PacketBlockDigListener;
import com.github.minersstudios.msblock.utils.ConfigCache;
import com.github.minersstudios.mscore.MSPlugin;
import com.github.minersstudios.mscore.utils.MSPluginUtils;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.minecraft.server.MinecraftServer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class MSBlock extends MSPlugin {
    private static MSBlock instance;
    private static ConfigCache configCache;
    private static CoreProtectAPI coreProtectAPI;

    @Override
    public void load() {
        MinecraftServer server = MinecraftServer.getServer();
        File paperGlobalConfig = new File("config/paper-global.yml");
        YamlConfiguration paperConfig = YamlConfiguration.loadConfiguration(paperGlobalConfig);
        String noteBlockUpdates = "block-updates.disable-noteblock-updates";

        if (!paperConfig.getBoolean(noteBlockUpdates, false)) {
            paperConfig.set(noteBlockUpdates, true);
            try {
                paperConfig.save(paperGlobalConfig);
            } catch (IOException e) {
                this.getLogger().log(Level.SEVERE, "Failed to save paper-global.yml with " + noteBlockUpdates + " enabled", e);
            }

            server.paperConfigurations.reloadConfigs(server);
            server.server.reloadCount++;
        }
    }

    @Override
    public void enable() {
        instance = this;
        coreProtectAPI = CoreProtect.getInstance().getAPI();

        reloadConfigs();

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketBlockDigListener());
    }

    public static void reloadConfigs() {
        instance.saveResource("blocks/example.yml", true);
        instance.saveDefaultConfig();
        instance.reloadConfig();
        configCache = new ConfigCache();

        configCache.loadBlocks();
        instance.setLoadedCustoms(true);

        instance.runTaskTimer(task -> {
            if (MSPluginUtils.isLoadedCustoms()) {
                configCache.recipeBlocks.forEach(CustomBlockData::registerRecipes);
                configCache.recipeBlocks.clear();
                task.cancel();
            }
        }, 0L, 10L);
    }

    public static @NotNull MSBlock getInstance() {
        return instance;
    }

    public static @NotNull ConfigCache getConfigCache() {
        return configCache;
    }

    public static @NotNull CoreProtectAPI getCoreProtectAPI() {
        return coreProtectAPI;
    }
}
