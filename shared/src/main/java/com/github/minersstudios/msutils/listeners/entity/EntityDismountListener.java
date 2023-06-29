package com.github.minersstudios.msutils.listeners.entity;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msutils.MSUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityDismountEvent;

@MSListener
public class EntityDismountListener implements Listener {

    @EventHandler
    public void onEntityDismount(@NotNull EntityDismountEvent event) {
        if (event.getEntity() instanceof Player player) {
            MSUtils.getConfigCache().playerInfoMap.getPlayerInfo(player).unsetSitting();
        }
    }
}
