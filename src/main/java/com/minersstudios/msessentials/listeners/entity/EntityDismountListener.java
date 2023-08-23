package com.minersstudios.msessentials.listeners.entity;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityDismountEvent;

@MSListener
public class EntityDismountListener extends AbstractMSListener {

    @EventHandler
    public void onEntityDismount(final @NotNull EntityDismountEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerInfo.fromOnlinePlayer(player).unsetSitting();
        }
    }
}
