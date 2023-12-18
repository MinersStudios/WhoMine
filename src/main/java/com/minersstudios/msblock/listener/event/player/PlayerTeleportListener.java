package com.minersstudios.msblock.listener.event.player;

import com.minersstudios.msblock.Cache;
import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerTeleportListener extends AbstractEventListener<MSBlock> {

    @EventHandler
    public void onPlayerTeleport(final @NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Cache cache = this.getPlugin().getCache();

        cache.getDiggingMap().removeAll(player);
        cache.getStepMap().put(player, 0.0d);
    }
}
