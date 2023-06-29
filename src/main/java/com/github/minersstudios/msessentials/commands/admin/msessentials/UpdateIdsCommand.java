package com.github.minersstudios.msessentials.commands.admin.msessentials;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.config.ConfigCache;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class UpdateIdsCommand {

    public static void runCommand(@NotNull CommandSender sender) {
        long time = System.currentTimeMillis();
        ConfigCache configCache = MSEssentials.getConfigCache();

        configCache.idMap.reloadIds();
        configCache.playerInfoMap.getMap().values().forEach(PlayerInfo::initNames);
        ChatUtils.sendFine(
                sender,
                Component.translatable(
                        "ms.command.msessentials.update_ids.success",
                        text(System.currentTimeMillis() - time)
                )
        );
    }
}
