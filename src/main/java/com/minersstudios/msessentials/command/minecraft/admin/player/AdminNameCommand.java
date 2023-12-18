package com.minersstudios.msessentials.command.minecraft.admin.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.PlayerName;
import com.minersstudios.msessentials.utility.MSPlayerUtils;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class AdminNameCommand {
    private static final TranslatableComponent WRONG_FORMAT = translatable("ms.error.wrong_format");
    private static final TranslatableComponent ABOUT_FORMAT = translatable("ms.command.player.name.about");
    private static final TranslatableComponent FULL_RESET_SENDER = translatable("ms.command.player.name.full_reset.sender");
    private static final TranslatableComponent FULL_RESET_TITLE = translatable("ms.command.player.name.full_reset.receiver.title");
    private static final TranslatableComponent FULL_RESET_SUBTITLE = translatable("ms.command.player.name.full_reset.receiver.subtitle");
    private static final TranslatableComponent GET_FIRST_NAME = translatable("ms.command.player.name.get.first_name");
    private static final TranslatableComponent GET_LAST_NAME = translatable("ms.command.player.name.get.last_name");
    private static final TranslatableComponent GET_PATRONYMIC = translatable("ms.command.player.name.get.patronymic");
    private static final TranslatableComponent NOW_FULL = translatable("ms.command.player.name.now_full");

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
                    ABOUT_FORMAT.args(
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
            MSLogger.severe(sender, WRONG_FORMAT);
            return true;
        }

        switch (paramString) {
            case "reset" -> {
                if (haveArg) {
                    return false;
                }

                MSLogger.fine(
                        sender,
                        FULL_RESET_SENDER.args(
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
                playerInfo.kickPlayer(FULL_RESET_TITLE, FULL_RESET_SUBTITLE);
                return true;
            }
            case "first-name" -> {
                if (!haveArg) {
                    MSLogger.fine(sender, GET_FIRST_NAME.args(text(playerName.getFirstName())));
                    return true;
                }

                playerName.setFirstName(paramArgString);
            }
            case "last-name" -> {
                if (!haveArg) {
                    MSLogger.fine(sender, GET_LAST_NAME.args(text(playerName.getLastName())));
                    return true;
                }

                playerName.setLastName("empty".equals(paramArgString) ? "" : paramArgString);
            }
            case "patronymic" -> {
                if (!haveArg) {
                    MSLogger.fine(sender, GET_PATRONYMIC.args(text(playerName.getPatronymic())));
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
                NOW_FULL.args(
                        playerInfo.getGrayIDGreenName()
                        .appendSpace()
                        .append(text(playerName.getPatronymic(), NamedTextColor.GREEN))
                )
        );
        return true;
    }
}
