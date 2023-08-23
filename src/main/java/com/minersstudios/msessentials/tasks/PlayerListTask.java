package com.minersstudios.msessentials.tasks;

import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.world.WorldDark;
import org.bukkit.Bukkit;

public class PlayerListTask implements Runnable {

    @Override
    public void run() {
        final Cache cache = MSEssentials.getCache();
        final var onlinePlayers = Bukkit.getOnlinePlayers();

        if (
                cache.playerInfoMap.isEmpty()
                || onlinePlayers.isEmpty()
        ) return;

        onlinePlayers.stream().parallel()
        .filter(player -> !WorldDark.isInWorldDark(player))
        .forEach(player -> {
            PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

            playerInfo.savePlayerDataParams();
        });
    }
}
