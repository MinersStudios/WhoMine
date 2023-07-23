package com.minersstudios.msessentials.commands.admin.player;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.Pronouns;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class AdminPronounsCommand {
    private static final TranslatableComponent USE_ONE_OF = translatable("ms.command.player.pronouns.use_one_of");
    private static final TranslatableComponent GET_PRONOUNS = translatable("ms.command.player.pronouns.get");
    private static final TranslatableComponent SET_PRONOUNS = translatable("ms.command.player.pronouns.set");

    public static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull [] args,
            @NotNull PlayerInfo playerInfo
    ) {
        PlayerFile playerFile = playerInfo.getPlayerFile();

        if (args.length == 2) {
            MSLogger.fine(
                    sender,
                    GET_PRONOUNS.args(
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
                        USE_ONE_OF.args(text(
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
                    SET_PRONOUNS.args(
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
