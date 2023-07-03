package com.github.minersstudios.msessentials.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player killedPlayer = event.getEntity();
        PlayerInfo killedInfo = PlayerInfo.fromMap(killedPlayer);

        event.deathMessage(null);
        killedInfo.unsetSitting();
        MessageUtils.sendDeathMessage(killedPlayer, killedPlayer.getKiller());
    }
}
