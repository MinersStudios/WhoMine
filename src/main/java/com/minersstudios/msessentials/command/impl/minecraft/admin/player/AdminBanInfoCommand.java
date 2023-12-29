package com.minersstudios.msessentials.command.impl.minecraft.admin.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.DateUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.BanList;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static net.kyori.adventure.text.Component.text;

public final class AdminBanInfoCommand {

    public static boolean runCommand(
            final @NotNull CommandSender sender,
            final String @NotNull [] args,
            final @NotNull PlayerInfo playerInfo
    ) {
        final boolean banned = playerInfo.isBanned();
        final boolean haveArg = args.length >= 4;
        final String paramString = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        final String paramArgString = haveArg ? args[3].toLowerCase(Locale.ROOT) : "";

        if (args.length == 2) {
            MSLogger.fine(
                    sender,
                    banned
                    ? COMMAND_PLAYER_BAN_INFO_INFO.arguments(
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname()),
                            text(playerInfo.getBannedBy()),
                            playerInfo.getBanReason(),
                            playerInfo.getBannedFrom(sender),
                            playerInfo.getBannedTo(sender)
                    )
                    : COMMAND_PLAYER_BAN_INFO_NOT_BANNED.arguments(
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname())
                    )
            );
            return true;
        }

        if (!banned) {
            MSLogger.severe(
                    sender,
                    COMMAND_PLAYER_BAN_INFO_NOT_BANNED
                    .arguments(
                            playerInfo.getDefaultName(),
                            text(playerInfo.getNickname())
                    )
            );
            return true;
        }

        switch (paramString) {
            case "reason" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            COMMAND_PLAYER_BAN_INFO_GET_REASON
                            .arguments(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    playerInfo.getBanReason()
                            )
                    );
                    return true;
                }

                final String reason = ChatUtils.extractMessage(args, 3);

                playerInfo.setBanReason(reason);
                MSLogger.fine(
                        sender,
                        COMMAND_PLAYER_BAN_INFO_SET_REASON
                        .arguments(
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
                            COMMAND_PLAYER_BAN_INFO_GET_TIME_TO
                            .arguments(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    playerInfo.getBannedTo(sender)
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

                final Date date = Date.from(instant);
                final BanList<PlayerProfile> banList = sender.getServer().getBanList(BanList.Type.PROFILE);
                final var banEntry = banList.getBanEntry(playerInfo.getPlayerProfile());

                playerInfo.setBannedTo(date);

                if (banEntry != null) {
                    banEntry.setExpiration(date);
                    banEntry.save();
                }

                MSLogger.fine(
                        sender,
                        COMMAND_PLAYER_BAN_INFO_SET_TIME_TO
                        .arguments(
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
