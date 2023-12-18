package com.minersstudios.msessentials.listeners.event.player;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class PlayerStopSpectatingEntityListener extends AbstractMSListener<MSEssentials> {

    @EventHandler
    public void onPlayerStopSpectatingEntity(final @NotNull PlayerStopSpectatingEntityEvent event) {
        if (WorldDark.isInWorldDark(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
