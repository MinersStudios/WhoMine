package com.minersstudios.msessentials.tasks;

import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.Objects;

public class MuteMapTask implements Runnable {

    @Override
    public void run() {
        final Cache cache = MSEssentials.getCache();
        if (cache.muteMap.isEmpty()) return;
        final Instant currentInstant = Instant.now();

        cache.muteMap.entrySet().stream().parallel()
        .filter(entry -> entry.getValue().getExpiration().isBefore(currentInstant))
        .forEach(entry -> {
            final OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            PlayerInfo.fromProfile(player.getUniqueId(), Objects.requireNonNull(player.getName())).unmute(null);
        });
    }
}
