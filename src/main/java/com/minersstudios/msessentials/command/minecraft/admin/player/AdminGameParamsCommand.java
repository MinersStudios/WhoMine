package com.minersstudios.msessentials.command.minecraft.admin.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class AdminGameParamsCommand {
    private static final TranslatableComponent WRONG_FORMAT = translatable("ms.error.wrong_format");
    private static final TranslatableComponent USE_ONE_OF = translatable("ms.command.player.game_params.use_one_of");
    private static final TranslatableComponent GAME_MODE_USE_ONE_OF = translatable("ms.command.player.game_params.game_mode_use_one_of");
    private static final TranslatableComponent GET_GAME_MODE = translatable("ms.command.player.game_params.get.game_mode");
    private static final TranslatableComponent GET_GAME_MODE_FULL = translatable("ms.command.player.game_params.get.game_mode_full");
    private static final TranslatableComponent SET_GAME_MODE = translatable("ms.command.player.game_params.set.game_mode");
    private static final TranslatableComponent GET_HEALTH = translatable("ms.command.player.game_params.get.health");
    private static final TranslatableComponent GET_HEALTH_FULL = translatable("ms.command.player.game_params.get.health_full");
    private static final TranslatableComponent SET_HEALTH = translatable("ms.command.player.game_params.set.health");
    private static final TranslatableComponent GET_AIR = translatable("ms.command.player.game_params.get.air");
    private static final TranslatableComponent GET_AIR_FULL = translatable("ms.command.player.game_params.get.air_full");
    private static final TranslatableComponent SET_AIR = translatable("ms.command.player.game_params.set.air");

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
        final boolean haveArg = args.length >= 4;
        final String paramString = args[2].toLowerCase(Locale.ROOT);
        final String paramArgString = haveArg ? args[3].toLowerCase(Locale.ROOT) : "";
        final Player player = playerInfo.getOnlinePlayer();

        switch (paramString) {
            case "game-mode" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            player == null
                            ? GET_GAME_MODE.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(playerFile.getGameMode().name().toLowerCase(Locale.ROOT))
                            )
                            : GET_GAME_MODE_FULL.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(playerFile.getGameMode().name().toLowerCase(Locale.ROOT)),
                                    text(player.getGameMode().name().toLowerCase(Locale.ROOT))
                            )
                    );
                    return true;
                }

                final GameMode gameMode;
                try {
                    gameMode = GameMode.valueOf(paramArgString.toUpperCase(Locale.ROOT));
                } catch (final IllegalArgumentException ignore) {
                    MSLogger.severe(sender, GAME_MODE_USE_ONE_OF);
                    return true;
                }

                if (player != null) {
                    player.setGameMode(gameMode);
                }

                playerFile.setGameMode(gameMode);
                playerFile.save();
                MSLogger.fine(
                        sender,
                        SET_GAME_MODE.args(
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname()),
                                text(paramArgString)
                        )
                );
                return true;
            }
            case "health" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            player == null
                            ? GET_HEALTH.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(Double.toString(playerFile.getHealth()))
                            )
                            : GET_HEALTH_FULL.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(Double.toString(playerFile.getHealth())),
                                    text(Double.toString(player.getHealth()))
                            )
                    );
                    return true;
                }

                final double health;
                try {
                    health = Double.parseDouble(paramArgString);
                } catch (final NumberFormatException ignore) {
                    MSLogger.severe(sender, WRONG_FORMAT);
                    return true;
                }

                if (player != null) {
                    player.setHealthScale(health);
                }

                playerFile.setHealth(health);
                playerFile.save();
                MSLogger.fine(
                        sender,
                        SET_HEALTH.args(
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname()),
                                text(paramArgString)
                        )
                );
                return true;
            }
            case "air" -> {
                if (!haveArg) {
                    MSLogger.fine(
                            sender,
                            player == null
                            ? GET_AIR.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(playerFile.getAir())
                            )
                            : GET_AIR_FULL.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(playerFile.getAir()),
                                    text(player.getRemainingAir())
                            )
                    );
                    return true;
                }

                final int air;
                try {
                    air = Integer.parseInt(paramArgString);
                } catch (final NumberFormatException ignore) {
                    MSLogger.severe(sender, WRONG_FORMAT);
                    return true;
                }

                if (player != null) {
                    player.setRemainingAir(air);
                }

                playerFile.setAir(air);
                playerFile.save();
                MSLogger.fine(
                        sender,
                        SET_AIR.args(
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
