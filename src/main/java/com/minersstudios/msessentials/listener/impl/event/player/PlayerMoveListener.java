package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerMoveListener extends AbstractEventListener<MSEssentials> {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final @NotNull PlayerMoveEvent event) {
        if (this.getPlugin().getCache().getWorldDark().isInWorldDark(event.getFrom())) {
            event.setCancelled(true);
        }
    }
}
