package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerQuitListener extends AbstractEventListener<MSEssentials> {

    @EventHandler
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        event.quitMessage(null);

        if (event.getReason() != PlayerQuitEvent.QuitReason.KICKED) {
            PlayerInfo
            .fromOnlinePlayer(this.getPlugin(), event.getPlayer())
            .handleQuit();
        }
    }
}
