package com.github.minersstudios.msessentials.listeners.entity;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.player.PlayerInfo;
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
            PlayerInfo.fromOnlinePlayer(player).unsetSitting();
        }
    }
}
