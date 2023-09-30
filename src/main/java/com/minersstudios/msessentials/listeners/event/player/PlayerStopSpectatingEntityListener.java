package com.minersstudios.msessentials.listeners.event.player;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.world.WorldDark;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerStopSpectatingEntityListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerStopSpectatingEntity(final @NotNull PlayerStopSpectatingEntityEvent event) {
        if (WorldDark.isInWorldDark(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
