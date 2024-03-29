package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class PlayerJoinListener extends AbstractEventListener<MSEssentials> {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        event.joinMessage(null);

        if (player.isDead()) {
            this.getPlugin().runTaskLater(() -> {
                player.spigot().respawn();
                this.handle(player);
            }, 8L);
        } else {
            this.handle(player);
        }
    }

    private void handle(final @NotNull Player player) {
        final MSEssentials plugin = this.getPlugin();
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(plugin, player);

        playerInfo.hideNameTag();
        player.displayName(playerInfo.getDefaultName());
        plugin.getCache().getWorldDark()
        .teleportToDarkWorld(player)
        .thenRun(() -> this.getPlugin().runTaskTimer(task -> {
            if (!player.isOnline()) {
                task.cancel();
                return;
            }

            if (playerInfo.isAuthenticated()) {
                task.cancel();
                playerInfo.handleResourcePack().thenAccept(bool -> {
                    if (bool) {
                        playerInfo.handleJoin();
                    }
                });
            }
        }, 0L, 10L));
    }
}
