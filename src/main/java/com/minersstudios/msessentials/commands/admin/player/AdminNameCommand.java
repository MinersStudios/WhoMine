package com.minersstudios.msessentials.commands.admin.player;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.PlayerName;
import com.minersstudios.msessentials.utils.MSPlayerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class AdminNameCommand {

    public static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull [] args,
            @NotNull PlayerInfo playerInfo
    ) {
        PlayerFile playerFile = playerInfo.getPlayerFile();
        YamlConfiguration yamlConfiguration = playerFile.getConfig();
        PlayerName playerName = playerFile.getPlayerName();
        boolean haveArg = args.length >= 4;
        String paramString = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        String paramArgString = haveArg ? args[3].toLowerCase(Locale.ROOT) : "";

        if (args.length == 2) {
            MSLogger.fine(
                    sender,
                    translatable(
                            "ms.command.player.name.about",
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
                && !"empty".equals(paramArgString)
                && !MSPlayerUtils.matchesNameRegex(paramArgString)
        ) {
            MSLogger.severe(sender, Component.translatable("ms.error.format"));
            return true;
        }

        switch (paramString) {
            case "reset" -> {
                if (haveArg) return false;

                MSLogger.fine(
                        sender,
                        translatable(
                                "ms.command.player.name.full_reset.sender",
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname())
                        )
                );

                yamlConfiguration.set("name.first-name", null);
                yamlConfiguration.set("name.last-name", null);
                yamlConfiguration.set("name.patronymic", null);
                yamlConfiguration.set("pronouns", null);
                playerFile.save();
                playerInfo.initNames();
                playerInfo.kickPlayer(
                        translatable("ms.command.player.name.full_reset.receiver.title"),
                        translatable("ms.command.player.name.full_reset.receiver.subtitle")
                );
                return true;
            }
            case "first-name" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            translatable("ms.command.player.name.get.first_name", text(playerName.getFirstName()))
                    );
                    return true;
                }

                playerName.setFirstName(paramArgString);
            }
            case "last-name" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            translatable("ms.command.player.name.get.last_name", text(playerName.getLastName()))
                    );
                    return true;
                }

                playerName.setLastName("empty".equals(paramArgString) ? "" : paramArgString);
            }
            case "patronymic" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            translatable("ms.command.player.name.get.patronymic", text(playerName.getPatronymic()))
                    );
                    return true;
                }

                playerName.setPatronymic("empty".equals(paramArgString) ? "" : paramArgString);
            }
        }

        playerFile.updateName();
        playerFile.save();
        playerInfo.initNames();
        MSLogger.fine(
                sender,
                translatable(
                        "ms.command.player.name.now_full",
                        playerInfo.getGrayIDGreenName()
                        .appendSpace()
                        .append(text(playerName.getPatronymic(), NamedTextColor.GREEN))
                )
        );
        return true;
    }
}
