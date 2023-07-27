package com.minersstudios.msessentials.commands.admin.player;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.DateUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.MuteMap;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class AdminMuteInfoCommand {
    private static final TranslatableComponent WRONG_FORMAT = translatable("ms.error.wrong_format");
    private static final TranslatableComponent MUTE_INFO_FORMAT = translatable("ms.command.player.mute_info.info");
    private static final TranslatableComponent MUTE_INFO_NOT_MUTED_FORMAT = translatable("ms.command.player.mute_info.not_muted");
    private static final TranslatableComponent MUTE_INFO_INFO_NOT_MUTED_FORMAT = translatable("ms.command.player.mute_info.info_not_muted");
    private static final TranslatableComponent MUTE_INFO_GET_REASON_FORMAT = translatable("ms.command.player.mute_info.get.reason");
    private static final TranslatableComponent MUTE_INFO_SET_REASON_FORMAT = translatable("ms.command.player.mute_info.set.reason");
    private static final TranslatableComponent MUTE_INFO_GET_TIME_TO_FORMAT = translatable("ms.command.player.mute_info.get.time_to");
    private static final TranslatableComponent MUTE_INFO_SET_TIME_TO_FORMAT = translatable("ms.command.player.mute_info.set.time_to");

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
            MSLogger.fine(
                    sender,
                    muted
                    ? MUTE_INFO_FORMAT.args(
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname()),
                            text(playerInfo.getMutedBy()),
                            text(playerInfo.getMuteReason()),
                            playerInfo.getMutedFrom(sender),
                            playerInfo.getMutedTo(sender)
                    )
                    : MUTE_INFO_INFO_NOT_MUTED_FORMAT.args(
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname())
                    )
            );
            return true;
        }

        if (!muted) {
            MSLogger.severe(
                    sender,
                    MUTE_INFO_NOT_MUTED_FORMAT.args(
                            playerInfo.getDefaultName(),
                            text(playerInfo.getNickname())
                    )
            );
            return true;
        }

        MuteMap muteMap = MSEssentials.getCache().muteMap;

        switch (paramString) {
            case "reason" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            MUTE_INFO_GET_REASON_FORMAT.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(playerInfo.getMuteReason())
                            )
                    );
                    return true;
                }

                String reason = ChatUtils.extractMessage(args, 3);

                muteMap.put(playerInfo.getOfflinePlayer(), playerInfo.getMutedTo(), reason, sender.getName());
                MSLogger.fine(
                        sender,
                        MUTE_INFO_SET_REASON_FORMAT.args(
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
                            MUTE_INFO_GET_TIME_TO_FORMAT.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    playerInfo.getMutedTo(sender)
                            )
                    );
                    return true;
                }

                Instant instant = DateUtils.getDateFromString(paramArgString, false);

                if (instant == null) {
                    MSLogger.severe(sender, WRONG_FORMAT);
                    return true;
                }

                muteMap.put(playerInfo.getOfflinePlayer(), instant, playerInfo.getMuteReason(), sender.getName());
                MSLogger.fine(
                        sender,
                        MUTE_INFO_SET_TIME_TO_FORMAT.args(
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
