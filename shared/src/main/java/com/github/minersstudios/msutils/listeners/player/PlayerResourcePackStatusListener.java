package com.github.minersstudios.msutils.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msutils.MSUtils;

import com.github.minersstudios.msutils.player.PlayerInfo;
import com.github.minersstudios.msutils.player.PlayerInfoMap;
import com.github.minersstudios.msutils.player.PlayerSettings;
import com.github.minersstudios.msutils.player.ResourcePack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@MSListener
public class PlayerResourcePackStatusListener implements Listener {

    @EventHandler
    public void onPlayerResourcePackStatus(@NotNull PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        PlayerInfoMap playerInfoMap = MSUtils.getConfigCache().playerInfoMap;
        PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(player);
        PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();
        Component nickname = text(player.getName());

        if (playerSettings.getResourcePackType() == ResourcePack.Type.NULL) return;
        switch (event.getStatus()) {
            case ACCEPTED -> ChatUtils.sendFine(Component.translatable("ms.resource_pack.accepted", nickname));
            case SUCCESSFULLY_LOADED -> {
                ChatUtils.sendFine(Component.translatable("ms.resource_pack.successfully_loaded", nickname));
                if (playerInfo.isInWorldDark()) {
                    playerInfo.initJoin();
                }
            }
            case FAILED_DOWNLOAD -> {
                ChatUtils.sendFine(Component.translatable("ms.resource_pack.failed_download.console", nickname));
                playerSettings.setResourcePackType(ResourcePack.Type.NONE);
                playerSettings.save();
                playerInfo.kickPlayer(
                        Component.translatable("ms.resource_pack.failed_download.receiver.title"),
                        Component.translatable("ms.resource_pack.failed_download.receiver.subtitle")
                );
            }
            case DECLINED -> {
                ChatUtils.sendFine(Component.translatable("ms.resource_pack.declined.console", nickname));
                playerInfo.kickPlayer(
                        Component.translatable("ms.resource_pack.declined.receiver.title"),
                        Component.translatable("ms.resource_pack.declined.receiver.subtitle")
                );
            }
        }
    }
}
