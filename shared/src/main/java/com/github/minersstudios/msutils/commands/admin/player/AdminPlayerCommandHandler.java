package com.github.minersstudios.msutils.commands.admin.player;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.DateUtils;
import com.github.minersstudios.mscore.utils.PlayerUtils;
import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.player.IDMap;
import com.github.minersstudios.msutils.player.PlayerInfo;
import com.github.minersstudios.msutils.player.PlayerInfoMap;
import com.github.minersstudios.msutils.tabcompleters.AllPlayers;
import com.github.minersstudios.msutils.utils.IDUtils;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "player",
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм] [параметры]",
        description = "Команды, отвечающие за параметры игрока",
        permission = "msutils.player.*",
        permissionDefault = PermissionDefault.OP
)
public class AdminPlayerCommandHandler implements MSCommandExecutor {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length < 2) return false;

        if (IDUtils.matchesIDRegex(args[0])) {
            IDMap idMap = MSUtils.getConfigCache().idMap;
            OfflinePlayer offlinePlayer = idMap.getPlayerByID(args[0]);

            if (offlinePlayer == null || offlinePlayer.getName() == null) {
                ChatUtils.sendError(sender, Component.translatable("ms.error.id_not_found"));
                return true;
            }
            return runCommand(sender, args, offlinePlayer);
        }

        if (args[0].length() > 2) {
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayerByNick(args[0]);

            if (offlinePlayer == null) {
                ChatUtils.sendError(sender, Component.translatable("ms.error.player_not_found"));
                return true;
            }
            return runCommand(sender, args, offlinePlayer);
        }

        ChatUtils.sendWarning(sender, Component.translatable("ms.error.name_length"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        switch (args.length) {
            case 1 -> {
                return new AllPlayers().onTabComplete(sender, command, label, args);
            }
            case 2 -> {
                return List.of(
                        "update",
                        "info",
                        "first-join",
                        "pronouns",
                        "game-params",
                        "settings",
                        "ban-info",
                        "mute-info",
                        "name"
                );
            }
            case 3 -> {
                switch (args[1].toLowerCase(Locale.ROOT)) {
                    case "pronouns" -> {
                        return List.of(
                                "he",
                                "she",
                                "they"
                        );
                    }
                    case "game-params" -> {
                        return List.of(
                                "game-mode",
                                "health",
                                "air"
                        );
                    }
                    case "settings" -> {
                        return List.of(
                                "resourcepack-type"
                        );
                    }
                    case "ban-info", "mute-info" -> {
                        return List.of(
                                "reason",
                                "time"
                        );
                    }
                    case "name" -> {
                        return List.of(
                                "reset",
                                "first-name",
                                "last-name",
                                "patronymic"
                        );
                    }
                }
            }
            case 4 -> {
                switch (args[1].toLowerCase(Locale.ROOT)) {
                    case "game-params" -> {
                        switch (args[2].toLowerCase(Locale.ROOT)) {
                            case "game-mode" -> {
                                return List.of(
                                        "survival",
                                        "creative",
                                        "spectator",
                                        "adventure"
                                );
                            }
                            case "air" -> {
                                return List.of(
                                        "0",
                                        "300"
                                );
                            }
                            case "health" -> {
                                return List.of(
                                        "0.0",
                                        "20.0"
                                );
                            }
                        }
                    }
                    case "settings" -> {
                        switch (args[2].toLowerCase(Locale.ROOT)) {
                            case "resourcepack-type" -> {
                                return List.of(
                                        "full",
                                        "lite",
                                        "none",
                                        "null"
                                );
                            }
                        }
                    }
                    case "ban-info", "mute-info" -> {
                        switch (args[2].toLowerCase(Locale.ROOT)) {
                            case "time" -> {
                                return DateUtils.getTimeSuggestions(args[3]);
                            }
                            case "reason" -> {
                                return List.of("неизвестно");
                            }
                        }
                    }
                    case "name" -> {
                        switch (args[2].toLowerCase(Locale.ROOT)) {
                            case "last-name", "patronymic" -> {
                                return List.of(
                                        "empty"
                                );
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return literal("player")
                .then(
                        argument("айди/никнейм", StringArgumentType.word())
                        .then(literal("update"))
                        .then(literal("info"))
                        .then(
                                literal("pronouns")
                                .then(literal("he"))
                                .then(literal("she"))
                                .then(literal("they"))
                        )
                        .then(
                                literal("game-params")
                                .then(
                                        literal("game-mode")
                                        .then(literal("survival"))
                                        .then(literal("creative"))
                                        .then(literal("spectator"))
                                        .then(literal("adventure"))
                                )
                                .then(
                                        literal("health")
                                        .then(argument("значение", DoubleArgumentType.doubleArg()))
                                )
                                .then(
                                        literal("air")
                                        .then(argument("значение", IntegerArgumentType.integer()))
                                )
                        )
                        .then(literal("first-join"))
                        .then(
                                literal("settings")
                                .then(
                                        literal("resourcepack-type")
                                        .then(literal("full"))
                                        .then(literal("lite"))
                                        .then(literal("none"))
                                        .then(literal("null"))
                                )
                        )
                        .then(
                                literal("ban-info")
                                .then(
                                        literal("reason")
                                        .then(argument("причина", StringArgumentType.greedyString()))
                                )
                                .then(
                                        literal("time")
                                        .then(argument("время", StringArgumentType.greedyString()))
                                )
                        )
                        .then(
                                literal("mute-info")
                                .then(
                                        literal("reason")
                                        .then(argument("причина", StringArgumentType.greedyString()))
                                )
                                .then(
                                        literal("time")
                                        .then(argument("время", StringArgumentType.greedyString()))
                                )
                        )
                        .then(
                                literal("name")
                                .then(literal("reset"))
                                .then(
                                        literal("first-name")
                                        .then(argument("имя", StringArgumentType.greedyString()))
                                )
                                .then(
                                        literal("last-name")
                                        .then(literal("empty"))
                                        .then(argument("фамилия", StringArgumentType.greedyString()))
                                )
                                .then(
                                        literal("patronymic")
                                        .then(literal("empty"))
                                        .then(argument("отчество", StringArgumentType.greedyString()))
                                )
                        )
                ).build();
    }

    private static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull [] args,
            @NotNull OfflinePlayer offlinePlayer
    ) {
        PlayerInfoMap playerInfoMap = MSUtils.getConfigCache().playerInfoMap;
        PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(offlinePlayer.getUniqueId(), Objects.requireNonNull(offlinePlayer.getName()));

        return switch (args[1].toLowerCase(Locale.ROOT)) {
            case "update" -> AdminUpdateCommand.runCommand(sender, playerInfo);
            case "info" -> AdminInfoCommand.runCommand(sender, playerInfo);
            case "pronouns" -> AdminPronounsCommand.runCommand(sender, args, playerInfo);
            case "game-params" -> AdminGameParamsCommand.runCommand(sender, args, offlinePlayer, playerInfo);
            case "first-join" -> AdminFirstJoinCommand.runCommand(sender, playerInfo);
            case "settings" -> AdminSettingsCommand.runCommand(sender, args, playerInfo);
            case "ban-info" -> AdminBanInfoCommand.runCommand(sender, args, playerInfo);
            case "mute-info" -> AdminMuteInfoCommand.runCommand(sender, args, playerInfo);
            case "name" -> AdminNameCommand.runCommand(sender, args, playerInfo);
            default -> false;
        };
    }
}
