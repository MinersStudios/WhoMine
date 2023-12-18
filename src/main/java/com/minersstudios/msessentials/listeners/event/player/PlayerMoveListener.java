package com.minersstudios.msessentials.listeners.event.player;

import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class PlayerMoveListener extends AbstractMSListener<MSEssentials> {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final @NotNull PlayerMoveEvent event) {
        if (WorldDark.isInWorldDark(event.getFrom())) {
            event.setCancelled(true);
        }
    }
}
