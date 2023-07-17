package com.github.minersstudios.msessentials.commands.admin.player;

import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.mscore.utils.DateUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class AdminFirstJoinCommand {

    public static boolean runCommand(
            @NotNull CommandSender sender,
            @NotNull PlayerInfo playerInfo
    ) {
        MSLogger.fine(
                sender,
                Component.translatable(
                        "ms.command.player.first_join",
                        playerInfo.getGrayIDGreenName(),
                        text(playerInfo.getNickname()),
                        text(DateUtils.getSenderDate(playerInfo.getPlayerFile().getFirstJoin(), sender))
                )
        );
        return true;
    }
}
