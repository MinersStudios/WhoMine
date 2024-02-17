package com.minersstudios.msessentials.command.impl.minecraft.admin.player;

import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.DateUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class AdminFirstJoinCommand {

    public static boolean runCommand(
            final @NotNull CommandSender sender,
            final @NotNull PlayerInfo playerInfo
    ) {
        MSLogger.fine(
                sender,
                Translations.COMMAND_PLAYER_FIRST_JOIN.asTranslatable()
                .arguments(
                        playerInfo.getGrayIDGreenName(),
                        text(playerInfo.getNickname()),
                        text(DateUtils.getSenderDate(playerInfo.getPlayerFile().getFirstJoin(), sender))
                )
        );
        return true;
    }
}
