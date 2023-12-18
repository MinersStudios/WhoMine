package com.minersstudios.msessentials.listeners.event.player;

import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class PlayerQuitListener extends AbstractMSListener<MSEssentials> {

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
