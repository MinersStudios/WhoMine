package com.github.minersstudios.msessentials.listeners.player;

import com.github.minersstudios.mscore.listener.AbstractMSListener;
import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.world.WorldDark;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerJoinListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

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

    private void handle(@NotNull Player player) {
        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        playerInfo.hideNameTag();
        player.displayName(playerInfo.getDefaultName());
        WorldDark.teleportToDarkWorld(player).thenRun(() ->
                playerInfo.handleResourcePack().thenAccept(bool -> {
                    if (bool) {
                        playerInfo.handleJoin();
                    }
                })
        );
    }
}
