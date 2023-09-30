package com.minersstudios.msessentials.listeners.event.player;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.world.WorldDark;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerJoinListener extends AbstractMSListener {

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
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        playerInfo.hideNameTag();
        player.displayName(playerInfo.getDefaultName());
        WorldDark.teleportToDarkWorld(player).thenRun(() -> this.getPlugin().runTaskTimer(task -> {
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
