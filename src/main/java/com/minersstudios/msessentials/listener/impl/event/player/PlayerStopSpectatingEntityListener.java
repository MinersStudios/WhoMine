package com.minersstudios.msessentials.listener.impl.event.player;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerStopSpectatingEntityListener extends AbstractEventListener<MSEssentials> {

    @EventHandler
    public void onPlayerStopSpectatingEntity(final @NotNull PlayerStopSpectatingEntityEvent event) {
        if (this.getPlugin().getCache().getWorldDark().isInWorldDark(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
