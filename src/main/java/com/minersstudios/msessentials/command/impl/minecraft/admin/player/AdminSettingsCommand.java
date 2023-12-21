package com.minersstudios.msessentials.command.impl.minecraft.admin.player;

import com.minersstudios.mscore.language.LanguageFile;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.PlayerSettings;
import com.minersstudios.msessentials.player.ResourcePack;
import com.minersstudios.msessentials.player.skin.Skin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static net.kyori.adventure.text.Component.text;

public final class AdminSettingsCommand {

    public static boolean runCommand(
            final @NotNull MSEssentials plugin,
            final @NotNull CommandSender sender,
            final String @NotNull [] args,
            final @NotNull PlayerInfo playerInfo
    ) {
        if (args.length < 3) {
            MSLogger.severe(
                    sender,
                    COMMAND_PLAYER_SETTINGS_USE_ONE_OF
            );
            return true;
        }

        final PlayerFile playerFile = playerInfo.getPlayerFile();
        final PlayerSettings playerSettings = playerFile.getPlayerSettings();
        final Player player = playerInfo.getOnlinePlayer();
        final boolean haveArg = args.length >= 4;
        final String paramString = args[2];
        final String paramArgString = haveArg ? args[3] : "";

        switch (paramString) {
            case "resourcepack-type" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            COMMAND_PLAYER_SETTINGS_GET_RESOURCEPACK_TYPE
                            .args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(playerSettings.getResourcePackType().name().toLowerCase(Locale.ROOT))
                            )
                    );
                    return true;
                }

                final ResourcePack.Type type = switch (paramArgString) {
                    case "full" -> ResourcePack.Type.FULL;
                    case "lite" -> ResourcePack.Type.LITE;
                    case "none" -> ResourcePack.Type.NONE;
                    case "null" -> ResourcePack.Type.NULL;
                    default -> null;
                };

                if (type == null) {
                    MSLogger.severe(
                            sender,
                            COMMAND_PLAYER_SETTINGS_RESOURCEPACK_TYPE_USE_ONE_OF
                            .args(
                                    text(
                                            Arrays.toString(ResourcePack.Type.values())
                                            .toLowerCase()
                                            .replaceAll("[\\[\\]]", "")
                                    )
                            )
                    );
                    return true;
                }

                playerSettings.setResourcePackType(type);
                playerSettings.save();

                if (type == ResourcePack.Type.NONE || type == ResourcePack.Type.NULL) {
                    playerInfo.kickPlayer(
                            MENU_RESOURCE_PACK_BUTTON_NONE_KICK_TITLE,
                            MENU_RESOURCE_PACK_BUTTON_NONE_KICK_SUBTITLE
                    );
                }

                MSLogger.fine(
                        sender,
                        COMMAND_PLAYER_SETTINGS_SET_RESOURCEPACK_TYPE
                        .args(
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname()),
                                text(paramArgString)
                        )
                );

                return true;
            }
            case "skin" -> {
                if (!haveArg) {
                    final Skin skin = playerSettings.getSkin();

                    if (skin == null) {
                        MSLogger.warning(
                                sender,
                                COMMAND_PLAYER_SETTINGS_GET_NO_SKIN
                                .args(
                                        playerInfo.getGrayIDGreenName(),
                                        text(playerInfo.getNickname())
                                )
                        );
                    } else {
                        MSLogger.fine(
                                sender,
                                COMMAND_PLAYER_SETTINGS_GET_SKIN
                                .args(
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
                        if (args.length < 5) {
                            return false;
                        }

                        final String skinName = args[4];
                        final Skin skin = playerFile.getSkin(skinName);

                        if (skin == null) {
                            MSLogger.severe(
                                    sender,
                                    COMMAND_PLAYER_SETTINGS_SKIN_NOT_FOUND
                                    .args(text(skinName))
                            );
                        } else {
                            if (paramArgString.equals("set")) {
                                playerInfo.setSkin(skin);
                                MSLogger.fine(
                                        sender,
                                        COMMAND_PLAYER_SETTINGS_SET_SKIN
                                        .args(
                                                playerInfo.getDefaultName(),
                                                text(playerInfo.getNickname()),
                                                text(skinName)
                                        )
                                );
                            } else {
                                playerFile.removeSkin(skin);
                                MSLogger.fine(
                                        sender,
                                        COMMAND_PLAYER_SETTINGS_REMOVE_SKIN
                                        .args(
                                                text(skinName),
                                                playerInfo.getDefaultName(),
                                                text(playerInfo.getNickname())
                                        )
                                );

                                if (player != null) {
                                    MSLogger.fine(
                                            player,
                                            DISCORD_SKIN_SUCCESSFULLY_REMOVED_MINECRAFT
                                            .args(text(skinName))
                                    );
                                }

                                playerInfo.sendPrivateDiscordMessage(BotHandler.craftEmbed(
                                        LanguageFile.renderTranslation(
                                                DISCORD_SKIN_SUCCESSFULLY_REMOVED
                                                .args(
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
                        if (args.length < 6) {
                            return false;
                        }

                        final String skinName = args[4];
                        final String skinLink = args[5];

                        if (
                                playerFile.hasAvailableSkinSlot()
                                && !playerFile.containsSkin(skinName)
                        ) {
                            try {
                                final Skin skin = Skin.create(plugin, skinName, skinLink);

                                if (skin != null) {
                                    playerFile.addSkin(skin);
                                    MSLogger.fine(
                                            sender,
                                            COMMAND_PLAYER_SETTINGS_ADD_SKIN
                                            .args(
                                                    text(skinName),
                                                    playerInfo.getDefaultName(),
                                                    text(playerInfo.getNickname())
                                            )
                                    );

                                    if (player != null) {
                                        MSLogger.fine(
                                                player,
                                                DISCORD_SKIN_SUCCESSFULLY_ADDED_MINECRAFT
                                                .args(text(skinName))
                                        );
                                    }

                                    playerInfo.sendPrivateDiscordMessage(BotHandler.craftEmbed(
                                            LanguageFile.renderTranslation(
                                                    DISCORD_SKIN_SUCCESSFULLY_ADDED
                                                    .args(
                                                            text(skinName),
                                                            playerInfo.getDefaultName(),
                                                            text(playerInfo.getNickname())
                                                    )
                                            )
                                    ));

                                    return true;
                                }
                            } catch (final IllegalArgumentException ignored) {}
                        }

                        MSLogger.severe(
                                sender,
                                COMMAND_PLAYER_SETTINGS_ADD_SKIN_ERROR
                                .args(
                                        text(skinName),
                                        playerInfo.getDefaultName(),
                                        text(playerInfo.getNickname())
                                )
                        );

                        return true;
                    }
                    default -> {
                        MSLogger.severe(
                                sender,
                                COMMAND_PLAYER_SETTINGS_SKIN_USE_ONE_OF
                        );
                        return true;
                    }
                }
            }
            default -> {
                MSLogger.severe(
                        sender,
                        COMMAND_PLAYER_SETTINGS_USE_ONE_OF
                );
                return true;
            }
        }
    }
}
