package com.github.minersstudios.msessentials.commands.admin.player;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.PlayerSettings;
import com.github.minersstudios.msessentials.player.ResourcePack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class AdminSettingsCommand {

    public static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull [] args,
            @NotNull PlayerInfo playerInfo
    ) {
        if (args.length < 3) {
            ChatUtils.sendError(sender, translatable("ms.command.player.settings.use_one_of"));
            return true;
        }

        PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();
        boolean haveArg = args.length >= 4;
        String paramString = args[2].toLowerCase(Locale.ROOT);
        String paramArgString = haveArg ? args[3].toLowerCase(Locale.ROOT) : "";

        switch (paramString) {
            case "resourcepack-type" -> {
                if (!haveArg) {
                    ResourcePack.Type type = playerSettings.getResourcePackType();
                    ChatUtils.sendFine(
                            sender,
                            translatable(
                                    "ms.command.player.settings.get.resourcepack_type",
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(type.name().toLowerCase(Locale.ROOT))
                            )
                    );
                    return true;
                }

                ResourcePack.Type type = switch (paramArgString) {
                    case "full" -> ResourcePack.Type.FULL;
                    case "lite" -> ResourcePack.Type.LITE;
                    case "none" -> ResourcePack.Type.NONE;
                    case "null" -> ResourcePack.Type.NULL;
                    default -> null;
                };

                if (type == null) {
                    ChatUtils.sendError(
                            sender,
                            translatable(
                                    "ms.command.player.settings.resourcepack_type_use_one_of",
                                    text(Arrays.toString(ResourcePack.Type.values()).toLowerCase().replaceAll("[\\[\\]]", ""))
                            )
                    );
                    return true;
                }

                playerSettings.setResourcePackType(type);
                playerSettings.save();

                if (type == ResourcePack.Type.NONE || type == ResourcePack.Type.NULL) {
                    playerInfo.kickPlayer(
                            Component.translatable("ms.menu.resource_pack.button.none.kick.title"),
                            Component.translatable("ms.menu.resource_pack.button.none.kick.subtitle")
                    );
                }

                ChatUtils.sendFine(
                        sender,
                        translatable(
                                "ms.command.player.settings.set.resourcepack_type",
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname()),
                                text(paramArgString)
                        )
                );
                return true;
            }
        }
        return false;
    }
}
