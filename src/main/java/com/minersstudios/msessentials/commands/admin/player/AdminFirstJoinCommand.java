package com.minersstudios.msessentials.commands.admin.player;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.utils.DateUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
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
                        Component.text(DateUtils.getSenderDate(playerInfo.getPlayerFile().getFirstJoin(), sender))
                )
        );
        return true;
    }
}
