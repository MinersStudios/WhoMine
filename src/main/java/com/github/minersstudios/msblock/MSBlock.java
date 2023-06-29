package com.github.minersstudios.msblock;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.msblock.listeners.block.PacketBlockDigListener;
import com.github.minersstudios.msblock.utils.ConfigCache;
import com.github.minersstudios.mscore.MSPlugin;
import com.github.minersstudios.mscore.utils.MSPluginUtils;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class MSBlock extends MSPlugin {
	private static MSBlock instance;
	private static ConfigCache configCache;
	private static CoreProtectAPI coreProtectAPI;

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
		instance.loadedCustoms = true;

		Bukkit.getScheduler().runTaskTimer(instance, task -> {
			if (MSPluginUtils.isLoadedCustoms()) {
				configCache.recipeBlocks.forEach(CustomBlockData::registerRecipes);
				configCache.recipeBlocks.clear();
				task.cancel();
			}
		}, 0L, 10L);
	}

	@Contract(pure = true)
	public static @NotNull MSBlock getInstance() {
		return instance;
	}

	@Contract(pure = true)
	public static @NotNull ConfigCache getConfigCache() {
		return configCache;
	}

	@Contract(pure = true)
	public static @NotNull CoreProtectAPI getCoreProtectAPI() {
		return coreProtectAPI;
	}
}
