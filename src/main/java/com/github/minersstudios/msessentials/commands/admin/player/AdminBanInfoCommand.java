package com.github.minersstudios.msessentials.commands.admin.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.DateUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.BanList;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class AdminBanInfoCommand {

    public static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull [] args,
            @NotNull PlayerInfo playerInfo
    ) {
        boolean banned = playerInfo.isBanned();
        boolean haveArg = args.length >= 4;
        String paramString = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        String paramArgString = haveArg ? args[3].toLowerCase(Locale.ROOT) : "";

        if (args.length == 2) {
            MSLogger.fine(
                    sender,
                    banned
                    ? translatable(
                            "ms.command.player.ban_info.info",
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname()),
                            text(playerInfo.getBannedBy()),
                            playerInfo.getBanReason(),
                            playerInfo.getBannedFrom(sender),
                            playerInfo.getBannedTo(sender)
                    )
                    : translatable(
                            "ms.command.player.ban_info.info_not_banned",
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname())
                    )
            );
            return true;
        }

        if (!banned) {
            MSLogger.severe(
                    sender,
                    translatable(
                            "ms.command.player.ban_info.not_banned",
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
                            translatable(
                                    "ms.command.player.ban_info.get.reason",
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    playerInfo.getBanReason()
                            )
                    );
                    return true;
                }

                String reason = ChatUtils.extractMessage(args, 3);

                playerInfo.setBanReason(reason);
                MSLogger.fine(
                        sender,
                        translatable(
                                "ms.command.player.ban_info.set.reason",
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
                            translatable(
                                    "ms.command.player.ban_info.get.time_to",
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    playerInfo.getBannedTo(sender)
                            )
                    );
                    return true;
                }

                Instant instant = DateUtils.getDateFromString(paramArgString, false);

                if (instant == null) {
                    MSLogger.severe(sender, Component.translatable("ms.error.wrong_format"));
                    return true;
                }

                Date date = Date.from(instant);
                BanList<PlayerProfile> banList = sender.getServer().getBanList(BanList.Type.PROFILE);
                var banEntry = banList.getBanEntry(playerInfo.getPlayerProfile());

                playerInfo.setBannedTo(date);

                if (banEntry != null) {
                    banEntry.setExpiration(date);
                    banEntry.save();
                }

                MSLogger.fine(
                        sender,
                        translatable(
                                "ms.command.player.ban_info.set.time_to",
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
