package com.minersstudios.msessentials.command.impl.minecraft.admin.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static net.kyori.adventure.text.Component.text;

public final class AdminGameParamsCommand {

    public static boolean runCommand(
            final @NotNull CommandSender sender,
            final String @NotNull [] args,
            final @NotNull PlayerInfo playerInfo
    ) {
        if (args.length < 3) {
            MSLogger.severe(
                    sender,
                    COMMAND_PLAYER_GAME_PARAMS_USE_ONE_OF
            );
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
                            ? COMMAND_PLAYER_GAME_PARAMS_GET_GAME_MODE.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(playerFile.getGameMode().name().toLowerCase(Locale.ROOT))
                            )
                            : COMMAND_PLAYER_GAME_PARAMS_GET_GAME_MODE_FULL.args(
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
                    MSLogger.severe(
                            sender,
                            COMMAND_PLAYER_GAME_PARAMS_GAME_MODE_USE_ONE_OF
                    );
                    return true;
                }

                if (player != null) {
                    player.setGameMode(gameMode);
                }

                playerFile.setGameMode(gameMode);
                playerFile.save();
                MSLogger.fine(
                        sender,
                        COMMAND_PLAYER_GAME_PARAMS_SET_GAME_MODE
                        .args(
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
                            ? COMMAND_PLAYER_GAME_PARAMS_GET_HEALTH.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(Double.toString(playerFile.getHealth()))
                            )
                            : COMMAND_PLAYER_GAME_PARAMS_GET_HEALTH_FULL.args(
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
                    MSLogger.severe(
                            sender,
                            ERROR_WRONG_FORMAT
                    );
                    return true;
                }

                if (player != null) {
                    player.setHealthScale(health);
                }

                playerFile.setHealth(health);
                playerFile.save();
                MSLogger.fine(
                        sender,
                        COMMAND_PLAYER_GAME_PARAMS_SET_HEALTH
                        .args(
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
                            ? COMMAND_PLAYER_GAME_PARAMS_GET_AIR.args(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname()),
                                    text(playerFile.getAir())
                            )
                            : COMMAND_PLAYER_GAME_PARAMS_GET_AIR_FULL.args(
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
                    MSLogger.severe(
                            sender,
                            ERROR_WRONG_FORMAT
                    );
                    return true;
                }

                if (player != null) {
                    player.setRemainingAir(air);
                }

                playerFile.setAir(air);
                playerFile.save();
                MSLogger.fine(
                        sender,
                        COMMAND_PLAYER_GAME_PARAMS_SET_AIR
                        .args(
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
