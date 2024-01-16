package com.minersstudios.msessentials.listener.impl.event.entity;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.entity.EntityDismountEvent;

@EventListener
public final class EntityDismountListener extends AbstractEventListener<MSEssentials> {

    @EventHandler
    public void onEntityDismount(final @NotNull EntityDismountEvent event) {
        if (event.getEntity() instanceof final Player player) {
            PlayerInfo
            .fromOnlinePlayer(this.getPlugin(), player)
            .unsetSitting();
        }
    }
}
