package com.github.minersstudios.msessentials.listeners.player;

import com.github.minersstudios.mscore.listener.AbstractMSListener;
import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.ResourcePack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerResourcePackStatusListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerResourcePackStatus(@NotNull PlayerResourcePackStatusEvent event) {
        PlayerResourcePackStatusEvent.Status status = event.getStatus();
        Player player = event.getPlayer();
        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
        ResourcePack.Type currentType = playerInfo.getPlayerFile().getPlayerSettings().getResourcePackType();

        if (
                currentType == ResourcePack.Type.NULL
                || status == PlayerResourcePackStatusEvent.Status.ACCEPTED
        ) return;

        playerInfo.completeResourcePackFuture(status);
    }
}
