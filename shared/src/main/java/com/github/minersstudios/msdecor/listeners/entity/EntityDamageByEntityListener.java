package com.github.minersstudios.msdecor.listeners.entity;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import com.github.minersstudios.msdecor.customdecor.CustomDecor;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.utils.CustomDecorUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class EntityDamageByEntityListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if (MSDecorUtils.isCustomDecorEntity(entity)) {
			event.setCancelled(true);
		}
		if (
				!(event.getDamager() instanceof Player player)
				|| player.getGameMode() == GameMode.ADVENTURE
				|| ((!player.isSneaking()
				|| player.getGameMode() != GameMode.SURVIVAL)
				&& player.getGameMode() != GameMode.CREATIVE)
		) return;
		CustomDecorData customDecorData = CustomDecorUtils.getCustomDecorDataByEntity(entity);
		if (customDecorData != null) {
			new CustomDecor(entity.getLocation().getBlock(), player, customDecorData).breakCustomDecor();
		}
	}
}
