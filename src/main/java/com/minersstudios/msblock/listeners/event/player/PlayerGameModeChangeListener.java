package com.minersstudios.msblock.listeners.event.player;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class PlayerGameModeChangeListener extends AbstractMSListener<MSBlock> {

    @EventHandler
    public void onPlayerGameModeChange(final @NotNull PlayerGameModeChangeEvent event) {
        this.getPlugin().getCache().getDiggingMap().removeAll(event.getPlayer());
    }
}
