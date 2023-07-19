package com.minersstudios.msessentials.listeners.player;

import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerTeleportListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerTeleport(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

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
