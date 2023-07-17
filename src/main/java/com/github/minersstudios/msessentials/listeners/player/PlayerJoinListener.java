package com.github.minersstudios.msessentials.listeners.player;

import com.github.minersstudios.mscore.listener.AbstractMSListener;
import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msessentials.menu.PronounsMenu;
import com.github.minersstudios.msessentials.player.PlayerFile;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.RegistrationProcess;
import com.github.minersstudios.msessentials.player.ResourcePack;
import com.github.minersstudios.msessentials.world.WorldDark;
import org.bukkit.GameMode;
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
        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
        PlayerFile playerFile = playerInfo.getPlayerFile();

        event.joinMessage(null);
        playerInfo.hideNameTag();
        player.displayName(playerInfo.getDefaultName());

        if (player.isDead()) {
            this.getPlugin().runTaskLater(() -> {
                player.spigot().respawn();
                WorldDark.teleportToDarkWorld(player);
                player.setGameMode(GameMode.SPECTATOR);
            }, 8L);
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            WorldDark.teleportToDarkWorld(player);
        }

        this.getPlugin().runTaskTimer(task -> {
            if (!player.isOnline()) task.cancel();
            if (playerInfo.isAuthenticated() && !player.isDead()) {
                if (
                        !playerFile.exists()
                        || (playerFile.exists()
                        && playerFile.isNoName())
                ) {
                    task.cancel();
                    new RegistrationProcess().registerPlayer(playerInfo);
                } else {
                    task.cancel();
                    if (playerFile.getConfig().getString("pronouns") == null) {
                        PronounsMenu.open(player);
                    } else {
                        if (playerFile.getPlayerSettings().getResourcePackType() == ResourcePack.Type.NONE) {
                            playerInfo.handleJoin();
                        } else {
                            ResourcePack.setResourcePack(playerInfo);
                        }
                    }
                }
            }
        }, 1L, 1L);
    }
}
