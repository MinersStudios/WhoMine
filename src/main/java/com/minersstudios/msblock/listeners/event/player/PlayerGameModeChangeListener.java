package com.minersstudios.msblock.listeners.event.player;

import com.minersstudios.msblock.Cache;
import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerGameModeChangeListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerGameModeChange(final @NotNull PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();
        final Cache cache = MSBlock.getCache();

        cache.diggingMap.removeAll(player);
    }
}
