package com.github.minersstudios.msutils.commands.admin.msutils;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.config.ConfigCache;
import com.github.minersstudios.msutils.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class UpdateIdsCommand {

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();
        ConfigCache configCache = MSUtils.getConfigCache();

        configCache.idMap.reloadIds();
        configCache.playerInfoMap.getMap().values().forEach(PlayerInfo::initNames);
        ChatUtils.sendFine(
                sender,
                Component.translatable(
                        "ms.command.msutils.update_ids.success",
                        text(System.currentTimeMillis() - time)
                )
        );
    }
}
