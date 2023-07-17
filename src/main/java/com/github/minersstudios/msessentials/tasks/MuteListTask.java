package com.github.minersstudios.msessentials.tasks;

import com.github.minersstudios.msessentials.Cache;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.Objects;

public class MuteListTask implements Runnable {

    @Override
    public void run() {
        Cache cache = MSEssentials.getCache();
        if (cache.muteMap.isEmpty()) return;
        Instant currentInstant = Instant.now();

        cache.muteMap.entrySet().stream()
        .filter(entry -> entry.getValue().getExpiration().isBefore(currentInstant))
        .forEach(entry -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            PlayerInfo.fromProfile(player.getUniqueId(), Objects.requireNonNull(player.getName())).unmute(null);
        });
    }
}
