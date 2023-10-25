package com.minersstudios.msessentials.commands.minecraft.admin.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.PlayerSettings;
import com.minersstudios.msessentials.player.ResourcePack;
import com.minersstudios.msessentials.player.skin.Skin;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class AdminSettingsCommand {
    private static final TranslatableComponent USE_ONE_OF = translatable("ms.command.player.settings.use_one_of");
    private static final TranslatableComponent USE_ONE_OF_RESOURCEPACK_TYPE = translatable("ms.command.player.settings.resourcepack_type_use_one_of");

    public static boolean runCommand(
            final @NotNull CommandSender sender,
            final String @NotNull [] args,
            final @NotNull PlayerInfo playerInfo
    ) {
        if (args.length < 3) {
            MSLogger.severe(sender, USE_ONE_OF);
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
                    final ResourcePack.Type type = playerSettings.getResourcePackType();
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
                            USE_ONE_OF_RESOURCEPACK_TYPE.args(text(
                                    Arrays.toString(ResourcePack.Type.values())
                                    .toLowerCase()
                                    .replaceAll("[\\[\\]]", "")
                            ))
                    );
                    return true;
                }

                playerSettings.setResourcePackType(type);
                playerSettings.save();

                if (type == ResourcePack.Type.NONE || type == ResourcePack.Type.NULL) {
                    playerInfo.kickPlayer(
                            translatable("ms.menu.resource_pack.button.none.kick.title"),
                            translatable("ms.menu.resource_pack.button.none.kick.subtitle")
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
                    final Skin skin = playerSettings.getSkin();

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

                        final String skinName = args[4];
                        final Skin skin = playerFile.getSkin(skinName);

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

                                playerInfo.sendPrivateDiscordMessage(BotHandler.craftEmbed(
                                        LanguageFile.renderTranslation(
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

                        final String skinName = args[4];
                        final String skinLink = args[5];

                        if (
                                playerFile.hasAvailableSkinSlot()
                                && !playerFile.containsSkin(skinName)
                        ) {
                            try {
                                final Skin skin = Skin.create(skinName, skinLink);

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

                                    playerInfo.sendPrivateDiscordMessage(BotHandler.craftEmbed(
                                            LanguageFile.renderTranslation(
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
                            } catch (final IllegalArgumentException ignored) {}
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
