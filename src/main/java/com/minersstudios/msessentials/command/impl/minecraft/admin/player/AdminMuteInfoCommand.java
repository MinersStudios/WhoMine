package com.minersstudios.msessentials.command.impl.minecraft.admin.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.DateUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.MuteMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Locale;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static net.kyori.adventure.text.Component.text;

public final class AdminMuteInfoCommand {

    public static boolean runCommand(
            final @NotNull MSEssentials plugin,
            final @NotNull CommandSender sender,
            final String @NotNull [] args,
            final @NotNull PlayerInfo playerInfo
    ) {
        final boolean muted = playerInfo.isMuted();
        final boolean haveArg = args.length >= 4;
        final String paramString = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        final String paramArgString = haveArg ? args[3].toLowerCase(Locale.ROOT) : "";

        if (args.length == 2) {
            MSLogger.fine(
                    sender,
                    muted
                    ? COMMAND_PLAYER_MUTE_INFO_INFO.args(
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname()),
                            text(playerInfo.getMutedBy()),
                            text(playerInfo.getMuteReason()),
                            playerInfo.getMutedFrom(sender),
                            playerInfo.getMutedTo(sender)
                    )
                    : COMMAND_PLAYER_MUTE_INFO_INFO_NOT_MUTED.args(
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname())
                    )
            );
            return true;
        }

        if (!muted) {
            MSLogger.severe(
                    sender,
                    COMMAND_PLAYER_MUTE_INFO_NOT_MUTED
                    .args(
                            playerInfo.getDefaultName(),
                            text(playerInfo.getNickname())
                    )
            );
            return true;
        }

        final MuteMap muteMap = plugin.getCache().getMuteMap();

        switch (paramString) {
            case "reason" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            COMMAND_PLAYER_MUTE_INFO_GET_REASON
                            .args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(playerInfo.getMuteReason())
                            )
                    );
                    return true;
                }

                final String reason = ChatUtils.extractMessage(args, 3);

                muteMap.put(playerInfo.getOfflinePlayer(), playerInfo.getMutedTo(), reason, sender.getName());
                MSLogger.fine(
                        sender,
                        COMMAND_PLAYER_MUTE_INFO_SET_REASON
                        .args(
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname()),
                                text(reason)
                        )
                );
                return true;
            }
            case "time" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            COMMAND_PLAYER_MUTE_INFO_GET_TIME_TO
                            .args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    playerInfo.getMutedTo(sender)
                            )
                    );
                    return true;
                }

                final Instant instant = DateUtils.getDateFromString(paramArgString, false);

                if (instant == null) {
                    MSLogger.severe(
                            sender,
                            ERROR_WRONG_FORMAT
                    );
                    return true;
                }

                muteMap.put(playerInfo.getOfflinePlayer(), instant, playerInfo.getMuteReason(), sender.getName());
                MSLogger.fine(
                        sender,
                        COMMAND_PLAYER_MUTE_INFO_SET_TIME_TO
                        .args(
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
