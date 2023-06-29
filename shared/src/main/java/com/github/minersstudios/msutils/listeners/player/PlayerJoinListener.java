package com.github.minersstudios.msutils.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.menu.PronounsMenu;
import com.github.minersstudios.msutils.player.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerInfoMap playerInfoMap = MSUtils.getConfigCache().playerInfoMap;
        PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(player);
        PlayerFile playerFile = playerInfo.getPlayerFile();

        event.joinMessage(null);
        playerInfo.hideNameTag();
        player.displayName(playerInfo.getDefaultName());

        if (player.isDead()) {
            Bukkit.getScheduler().runTaskLater(MSUtils.getInstance(), () -> {
                player.spigot().respawn();
                player.teleport(new Location(MSUtils.getWorldDark(), 0.0d, 0.0d, 0.0d), PlayerTeleportEvent.TeleportCause.PLUGIN);
                player.setGameMode(GameMode.SPECTATOR);
            }, 8L);
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            Bukkit.getScheduler().runTask(
                    MSUtils.getInstance(),
                    () -> player.setSpectatorTarget(MSUtils.getDarkEntity())
            );
        }

        Bukkit.getScheduler().runTaskTimer(MSUtils.getInstance(), (task) -> {
            if (!player.isOnline()) task.cancel();
            if (MSUtils.getAuthMeApi().isAuthenticated(player) && !player.isDead()) {
                if (
                        !playerFile.exists()
                        || (playerFile.exists()
                        && playerFile.isNoName())
                ) {
                    task.cancel();
                    new RegistrationProcess().registerPlayer(playerInfo);
                } else {
                    task.cancel();
                    if (playerFile.getYamlConfiguration().getString("pronouns") == null) {
                        PronounsMenu.open(player);
                    } else {
                        if (playerFile.getPlayerSettings().getResourcePackType() == ResourcePack.Type.NONE) {
                            playerInfo.initJoin();
                        } else {
                            ResourcePack.setResourcePack(playerInfo);
                        }
                    }
                }
            }
        }, 1L, 1L);
    }
}
