package com.minersstudios.msessentials.task;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.collection.MuteMap;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public final class MuteMapTask implements Runnable {
    private final Server server;
    private final PlayerInfoMap playerInfoMap;
    private final MuteMap muteMap;

    public MuteMapTask(final @NotNull MSEssentials plugin) {
        this.server = plugin.getServer();
        this.playerInfoMap = plugin.getCache().getPlayerInfoMap();
        this.muteMap = plugin.getCache().getMuteMap();
    }

    @Override
    public void run() {
        if (this.muteMap.isEmpty()) {
            return;
        }

        final Instant currentInstant = Instant.now();

        this.muteMap.entrySet().stream().parallel()
        .filter(entry -> entry.getValue().getExpiration().isBefore(currentInstant))
        .forEach(entry -> {
            final OfflinePlayer player = this.server.getOfflinePlayer(entry.getKey());
            final String name = player.getName();

            if (name != null) {
                this.playerInfoMap
                .get(player.getUniqueId(), name)
                .unmute(null);
            }
        });
    }
}
