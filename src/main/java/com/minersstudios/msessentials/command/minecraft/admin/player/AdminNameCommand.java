package com.minersstudios.msessentials.command.minecraft.admin.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.PlayerName;
import com.minersstudios.msessentials.utility.MSPlayerUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static net.kyori.adventure.text.Component.text;

public final class AdminNameCommand {
    private static final String EMPTY = "empty";

    public static boolean runCommand(
            final @NotNull CommandSender sender,
            final String @NotNull [] args,
            final @NotNull PlayerInfo playerInfo
    ) {
        final PlayerFile playerFile = playerInfo.getPlayerFile();
        final YamlConfiguration yaml = playerFile.getConfig();
        final PlayerName playerName = playerFile.getPlayerName();
        final boolean haveArg = args.length >= 4;
        final String paramString = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        final String paramArgString = haveArg ? args[3].toLowerCase(Locale.ROOT) : "";

        if (args.length == 2) {
            MSLogger.fine(
                    sender,
                    COMMAND_PLAYER_NAME_ABOUT
                    .args(
                            playerInfo.getGrayIDGreenName()
                            .appendSpace()
                            .append(text(playerName.getPatronymic(), NamedTextColor.GREEN)),
                            text(playerInfo.getNickname()),
                            text(playerInfo.getUuid().toString())
                    )
            );
            return true;
        }

        if (
                !paramArgString.isEmpty()
                && !EMPTY.equals(paramArgString)
                && !MSPlayerUtils.matchesNameRegex(paramArgString)
        ) {
            MSLogger.severe(
                    sender,
                    ERROR_WRONG_FORMAT
            );
            return true;
        }

        switch (paramString) {
            case "reset" -> {
                if (haveArg) {
                    return false;
                }

                MSLogger.fine(
                        sender,
                        COMMAND_PLAYER_NAME_FULL_RESET_SENDER
                        .args(
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname())
                        )
                );

                yaml.set("name.first-name", null);
                yaml.set("name.last-name", null);
                yaml.set("name.patronymic", null);
                yaml.set("pronouns", null);
                playerFile.save();
                playerInfo.initNames();
                playerInfo.kickPlayer(
                        COMMAND_PLAYER_NAME_FULL_RESET_RECEIVER_TITLE,
                        COMMAND_PLAYER_NAME_FULL_RESET_RECEIVER_SUBTITLE
                );

                return true;
            }
            case "first-name" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            COMMAND_PLAYER_NAME_GET_FIRST_NAME
                            .args(text(playerName.getFirstName()))
                    );
                    return true;
                }

                playerName.setFirstName(paramArgString);
            }
            case "last-name" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            COMMAND_PLAYER_NAME_GET_LAST_NAME
                            .args(text(playerName.getLastName()))
                    );
                    return true;
                }

                playerName.setLastName(EMPTY.equals(paramArgString) ? "" : paramArgString);
            }
            case "patronymic" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            COMMAND_PLAYER_NAME_GET_PATRONYMIC
                            .args(text(playerName.getPatronymic()))
                    );
                    return true;
                }

                playerName.setPatronymic(EMPTY.equals(paramArgString) ? "" : paramArgString);
            }
        }

        playerFile.updateName();
        playerFile.save();
        playerInfo.initNames();
        MSLogger.fine(
                sender,
                COMMAND_PLAYER_NAME_NOW_FULL
                .args(
                        playerInfo.getGrayIDGreenName()
                        .appendSpace()
                        .append(text(playerName.getPatronymic(), NamedTextColor.GREEN))
                )
        );

        return true;
    }
}
