package com.github.minersstudios.msessentials.commands.admin.player;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.DateUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.MuteMap;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class AdminMuteInfoCommand {

    public static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull [] args,
            @NotNull PlayerInfo playerInfo
    ) {
        boolean muted = playerInfo.isMuted();
        boolean haveArg = args.length >= 4;
        String paramString = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        String paramArgString = haveArg ? args[3].toLowerCase(Locale.ROOT) : "";

        if (args.length == 2) {
            ChatUtils.sendFine(
                    sender,
                    muted
                            ? translatable(
                            "ms.command.player.mute_info.info",
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname()),
                            text(playerInfo.getMutedBy()),
                            text(playerInfo.getMuteReason()),
                            playerInfo.getMutedFrom(sender),
                            playerInfo.getMutedTo(sender)
                    )
                            : translatable(
                            "ms.command.player.mute_info.info_not_muted",
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname())
                    )
            );
            return true;
        }

        if (!muted) {
            ChatUtils.sendError(
                    sender,
                    translatable(
                            "ms.command.player.mute_info.not_muted",
                            playerInfo.getDefaultName(),
                            text(playerInfo.getNickname())
                    )
            );
            return true;
        }

        MuteMap muteMap = MSEssentials.getConfigCache().muteMap;

        switch (paramString) {
            case "reason" -> {
                if (!haveArg) {
                    ChatUtils.sendFine(
                            sender,
                            translatable(
                                    "ms.command.player.mute_info.get.reason",
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(playerInfo.getMuteReason())
                            )
                    );
                    return true;
                }

                String reason = ChatUtils.extractMessage(args, 3);

                muteMap.put(playerInfo.getOfflinePlayer(), playerInfo.getMutedTo(), reason, sender.getName());
                ChatUtils.sendFine(
                        sender,
                        translatable(
                                "ms.command.player.mute_info.set.reason",
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname()),
                                text(reason)
                        )
                );
                return true;
            }
            case "time" -> {
                if (!haveArg) {
                    ChatUtils.sendFine(
                            sender,
                            translatable(
                                    "ms.command.player.mute_info.get.time_to",
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    playerInfo.getMutedTo(sender)
                            )
                    );
                    return true;
                }

                Instant instant = DateUtils.getDateFromString(paramArgString, false);

                if (instant == null) {
                    ChatUtils.sendError(sender, Component.translatable("ms.error.format"));
                    return true;
                }

                muteMap.put(playerInfo.getOfflinePlayer(), instant, playerInfo.getMuteReason(), sender.getName());
                ChatUtils.sendFine(
                        sender,
                        translatable(
                                "ms.command.player.mute_info.set.time_to",
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname()),
                                text(DateUtils.getSenderDate(instant, sender))
                        )
                );
                return true;
            }
        }
        return false;
    }
}
