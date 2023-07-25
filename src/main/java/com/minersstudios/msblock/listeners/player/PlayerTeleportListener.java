package com.minersstudios.msblock.listeners.player;

import com.minersstudios.msblock.Cache;
import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerTeleportListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerTeleport(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Cache cache = MSBlock.getCache();

        cache.diggingMap.removeAll(player);
        cache.stepMap.remove(player);
    }
}
