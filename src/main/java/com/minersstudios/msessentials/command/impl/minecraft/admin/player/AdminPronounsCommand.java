package com.minersstudios.msessentials.command.impl.minecraft.admin.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.Pronouns;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

import static com.minersstudios.mscore.locale.Translations.*;
import static net.kyori.adventure.text.Component.text;

public final class AdminPronounsCommand {

    public static boolean runCommand(
            final @NotNull CommandSender sender,
            final String @NotNull [] args,
            final @NotNull PlayerInfo playerInfo
    ) {
        final PlayerFile playerFile = playerInfo.getPlayerFile();

        if (args.length == 2) {
            MSLogger.fine(
                    sender,
                    COMMAND_PLAYER_PRONOUNS_GET.asTranslatable()
                    .arguments(
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname()),
                            text(playerFile.getPronouns().name().toLowerCase(Locale.ROOT))
                    )
            );

            return true;
        } else if (args.length == 3) {
            final String pronounsString = args[2];
            final Pronouns pronouns;

            try {
                pronouns = Pronouns.valueOf(pronounsString.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException ignore) {
                MSLogger.severe(
                        sender,
                        COMMAND_PLAYER_PRONOUNS_USE_ONE_OF.asTranslatable()
                        .arguments(text(
                                Arrays.toString(Pronouns.values())
                                .toLowerCase()
                                .replaceAll("[\\[\\]]", "")
                        ))
                );

                return true;
            }

            playerFile.setPronouns(pronouns);
            playerFile.save();
            MSLogger.fine(
                    sender,
                    COMMAND_PLAYER_PRONOUNS_SET.asTranslatable()
                    .arguments(
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname()),
                            text(pronouns.name().toLowerCase(Locale.ROOT))
                    )
            );

            return true;
        }

        return false;
    }
}
