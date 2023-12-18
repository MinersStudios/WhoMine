package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerDropItemListener extends AbstractEventListener<MSEssentials> {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(final @NotNull PlayerDropItemEvent event) {
        if (WorldDark.isInWorldDark(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
