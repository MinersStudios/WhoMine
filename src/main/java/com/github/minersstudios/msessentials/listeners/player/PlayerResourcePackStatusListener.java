package com.github.minersstudios.msessentials.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.PlayerSettings;
import com.github.minersstudios.msessentials.player.ResourcePack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.github.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@MSListener
public class PlayerResourcePackStatusListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerResourcePackStatus(@NotNull PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
        PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();
        Component nickname = text(player.getName());

        if (playerSettings.getResourcePackType() == ResourcePack.Type.NULL) return;

        switch (event.getStatus()) {
            case ACCEPTED -> MSLogger.fine(Component.translatable("ms.resource_pack.accepted", nickname));
            case SUCCESSFULLY_LOADED -> {
                MSLogger.fine(Component.translatable("ms.resource_pack.successfully_loaded", nickname));

                if (playerInfo.isInWorldDark()) {
                    playerInfo.handleJoin();
                }
            }
            case FAILED_DOWNLOAD -> {
                MSLogger.fine(Component.translatable("ms.resource_pack.failed_download.console", nickname));
                playerSettings.setResourcePackType(ResourcePack.Type.NONE);
                playerSettings.save();
                playerInfo.kickPlayer(
                        Component.translatable("ms.resource_pack.failed_download.receiver.title"),
                        Component.translatable("ms.resource_pack.failed_download.receiver.subtitle")
                );
            }
            case DECLINED -> {
                MSLogger.fine(Component.translatable("ms.resource_pack.declined.console", nickname));
                playerInfo.kickPlayer(
                        Component.translatable("ms.resource_pack.declined.receiver.title"),
                        Component.translatable("ms.resource_pack.declined.receiver.subtitle")
                );
            }
        }
    }
}
