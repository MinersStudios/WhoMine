package com.github.minersstudios.msessentials.commands.admin.player;

import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.msessentials.player.PlayerFile;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.PlayerSettings;
import com.github.minersstudios.msessentials.player.ResourcePack;
import com.github.minersstudios.msessentials.player.skin.Skin;
import com.github.minersstudios.msessentials.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslation;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class AdminSettingsCommand {

    public static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull [] args,
            @NotNull PlayerInfo playerInfo
    ) {
        if (args.length < 3) {
            MSLogger.severe(sender, translatable("ms.command.player.settings.use_one_of"));
            return true;
        }

        PlayerFile playerFile = playerInfo.getPlayerFile();
        PlayerSettings playerSettings = playerFile.getPlayerSettings();
        Player player = playerInfo.getOnlinePlayer();
        boolean haveArg = args.length >= 4;
        String paramString = args[2];
        String paramArgString = haveArg ? args[3] : "";

        switch (paramString) {
            case "resourcepack-type" -> {
                if (!haveArg) {
                    ResourcePack.Type type = playerSettings.getResourcePackType();
                    MSLogger.fine(
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
                    MSLogger.severe(
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

                MSLogger.fine(
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
            case "skin" -> {
                if (!haveArg) {
                    Skin skin = playerSettings.getSkin();

                    if (skin == null) {
                        MSLogger.warning(
                                sender,
                                translatable(
                                        "ms.command.player.settings.get.no_skin",
                                        playerInfo.getGrayIDGreenName(),
                                        text(playerInfo.getNickname())
                                )
                        );
                    } else {
                        MSLogger.fine(
                                sender,
                                translatable(
                                        "ms.command.player.settings.get.skin",
                                        playerInfo.getGrayIDGreenName(),
                                        text(playerInfo.getNickname()),
                                        text(skin.getName())
                                )
                        );
                    }
                    return true;
                }

                switch (paramArgString) {
                    case "set", "remove" -> {
                        if (args.length < 5) return false;

                        String skinName = args[4];
                        Skin skin = playerFile.getSkin(skinName);

                        if (skin == null) {
                            MSLogger.severe(
                                    sender,
                                    translatable(
                                            "ms.command.player.settings.skin_not_found",
                                            text(skinName)
                                    )
                            );
                        } else {
                            if (paramArgString.equals("set")) {
                                playerInfo.setSkin(skin);
                                MSLogger.fine(
                                        sender,
                                        translatable(
                                                "ms.command.player.settings.set.skin",
                                                playerInfo.getDefaultName(),
                                                text(playerInfo.getNickname()),
                                                text(skinName)
                                        )
                                );
                            } else {
                                playerFile.removeSkin(skin);
                                MSLogger.fine(
                                        sender,
                                        translatable(
                                                "ms.command.player.settings.remove.skin",
                                                text(skinName),
                                                playerInfo.getDefaultName(),
                                                text(playerInfo.getNickname())
                                        )
                                );

                                if (player != null) {
                                    MSLogger.fine(
                                            player,
                                            translatable(
                                                    "ms.discord.skin.successfully_removed.minecraft",
                                                    text(skinName)
                                            )
                                    );
                                }

                                playerInfo.sendPrivateDiscordMessage(MessageUtils.craftEmbed(
                                        renderTranslation(
                                                translatable(
                                                        "ms.discord.skin.successfully_removed",
                                                        text(skinName),
                                                        playerInfo.getDefaultName(),
                                                        text(playerInfo.getNickname())
                                                )
                                        )
                                ));
                            }
                        }
                        return true;
                    }
                    case "add" -> {
                        if (args.length < 6) return false;

                        String skinName = args[4];
                        String skinLink = args[5];

                        if (
                                playerFile.hasAvailableSkinSlot()
                                && !playerFile.containsSkin(skinName)
                        ) {
                            try {
                                Skin skin = Skin.create(skinName, skinLink);

                                if (skin != null) {
                                    playerFile.addSkin(skin);
                                    MSLogger.fine(
                                            sender,
                                            translatable(
                                                    "ms.command.player.settings.add.skin",
                                                    text(skinName),
                                                    playerInfo.getDefaultName(),
                                                    text(playerInfo.getNickname())
                                            )
                                    );

                                    if (player != null) {
                                        MSLogger.fine(
                                                player,
                                                translatable(
                                                        "ms.discord.skin.successfully_added.minecraft",
                                                        text(skinName)
                                                )
                                        );
                                    }

                                    playerInfo.sendPrivateDiscordMessage(MessageUtils.craftEmbed(
                                            renderTranslation(
                                                    translatable(
                                                            "ms.discord.skin.successfully_added",
                                                            text(skinName),
                                                            playerInfo.getDefaultName(),
                                                            text(playerInfo.getNickname())
                                                    )
                                            )
                                    ));
                                    return true;
                                }
                            } catch (IllegalArgumentException ignored) {}
                        }

                        MSLogger.severe(
                                sender,
                                translatable(
                                        "ms.command.player.settings.add.skin.error",
                                        text(skinName),
                                        playerInfo.getDefaultName(),
                                        text(playerInfo.getNickname())
                                )
                        );
                        return true;
                    }
                    default -> {
                        MSLogger.severe(sender, translatable("ms.command.player.settings.skin.use_one_of"));
                        return true;
                    }
                }
            }
            default -> {
                MSLogger.severe(sender, translatable("ms.command.player.settings.use_one_of"));
                return true;
            }
        }
    }
}
