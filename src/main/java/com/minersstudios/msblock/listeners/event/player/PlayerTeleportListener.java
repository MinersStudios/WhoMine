package com.minersstudios.msblock.listeners.event.player;

import com.minersstudios.msblock.Cache;
import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public final class PlayerTeleportListener extends AbstractMSListener<MSBlock> {

    @EventHandler
    public void onPlayerTeleport(final @NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Cache cache = MSBlock.getCache();

        cache.diggingMap.removeAll(player);
        cache.stepMap.put(player, 0.0d);
    }
}
