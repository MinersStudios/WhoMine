package com.minersstudios.msessentials.commands.minecraft.admin.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.util.DateUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class AdminFirstJoinCommand {
    private static final TranslatableComponent FIRST_JOIN_FORMAT = translatable("ms.command.player.first_join");

    public static boolean runCommand(
            final @NotNull CommandSender sender,
            final @NotNull PlayerInfo playerInfo
    ) {
        MSLogger.fine(
                sender,
                FIRST_JOIN_FORMAT.args(
                        playerInfo.getGrayIDGreenName(),
                        text(playerInfo.getNickname()),
                        text(DateUtils.getSenderDate(playerInfo.getPlayerFile().getFirstJoin(), sender))
                )
        );
        return true;
    }
}
