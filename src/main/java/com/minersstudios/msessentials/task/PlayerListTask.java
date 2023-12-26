package com.minersstudios.msessentials.task;

import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import com.minersstudios.msessentials.world.WorldDark;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public final class PlayerListTask implements Runnable {
    private final Server server;
    private final Cache cache;

    public PlayerListTask(final @NotNull MSEssentials plugin) {
        this.server = plugin.getServer();
        this.cache = plugin.getCache();
    }

    @Override
    public void run() {
        final var onlinePlayers = this.server.getOnlinePlayers();

        if (onlinePlayers.isEmpty()) {
            return;
        }

        final WorldDark worldDark = this.cache.getWorldDark();
        final PlayerInfoMap playerInfoMap = this.cache.getPlayerInfoMap();

        onlinePlayers.stream().parallel()
        .filter(player -> !worldDark.isInWorldDark(player))
        .forEach(player ->
                playerInfoMap
                .get(player)
                .savePlayerDataParams()
        );
    }
}
