package com.minersstudios.msessentials.listeners.event.player;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerTeleportListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerTeleport(final @NotNull PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        if (playerInfo.isSitting()) {
            playerInfo.unsetSitting();
        }

        if (
                event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE
                && playerInfo.isInWorldDark()
        ) {
            event.setCancelled(true);
        }
    }
}
