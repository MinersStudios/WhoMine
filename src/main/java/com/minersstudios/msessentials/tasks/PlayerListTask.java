package com.minersstudios.msessentials.tasks;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import com.minersstudios.msessentials.world.WorldDark;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public final class PlayerListTask implements Runnable {
    private final Server server;
    private final PlayerInfoMap playerInfoMap;

    public PlayerListTask(final @NotNull MSEssentials plugin) {
        this.server = plugin.getServer();
        this.playerInfoMap = plugin.getCache().getPlayerInfoMap();
    }

    @Override
    public void run() {
        final var onlinePlayers = this.server.getOnlinePlayers();

        if (onlinePlayers.isEmpty()) {
            return;
        }

        onlinePlayers.stream().parallel()
        .filter(player -> !WorldDark.isInWorldDark(player))
        .forEach(player ->
                this.playerInfoMap
                .get(player)
                .savePlayerDataParams()
        );
    }
}
