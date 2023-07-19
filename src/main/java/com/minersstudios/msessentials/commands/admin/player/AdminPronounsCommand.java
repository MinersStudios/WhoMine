package com.minersstudios.msessentials.commands.admin.player;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.Pronouns;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class AdminPronounsCommand {

    public static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull [] args,
            @NotNull PlayerInfo playerInfo
    ) {
        PlayerFile playerFile = playerInfo.getPlayerFile();

        if (args.length == 2) {
            MSLogger.fine(
                    sender,
                    translatable(
                            "ms.command.player.pronouns.get",
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname()),
                            text(playerFile.getPronouns().name().toLowerCase(Locale.ROOT))
                    )
            );
            return true;
        } else if (args.length == 3) {
            String pronounsString = args[2];

            Pronouns pronouns;
            try {
                pronouns = Pronouns.valueOf(pronounsString.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignore) {
                MSLogger.severe(
                        sender,
                        translatable(
                                "ms.command.player.pronouns.use_one_of",
                                text(Arrays.toString(Pronouns.values()).toLowerCase().replaceAll("[\\[\\]]", ""))
                        )
                );
                return true;
            }

            playerFile.setPronouns(pronouns);
            playerFile.save();
            MSLogger.fine(
                    sender,
                    translatable(
                            "ms.command.player.pronouns.set",
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
