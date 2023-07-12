package com.github.minersstudios.msessentials.commands.admin.player;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.DateUtils;
import com.github.minersstudios.mscore.utils.PlayerUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.map.IDMap;
import com.github.minersstudios.msessentials.player.skin.Skin;
import com.github.minersstudios.msessentials.tabcompleters.AllPlayers;
import com.github.minersstudios.msessentials.utils.IDUtils;
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

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "player",
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм] [параметры]",
        description = "Команды, отвечающие за параметры игрока",
        permission = "msessentials.player.*",
        permissionDefault = PermissionDefault.OP
)
public class AdminPlayerCommandHandler implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("player")
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
                            .then(
                                    literal("skin")
                                    .then(
                                            literal("set")
                                            .then(argument("имя", StringArgumentType.word()))
                                    )
                                    .then(
                                            literal("remove")
                                            .then(argument("имя", StringArgumentType.word()))
                                    )
                                    .then(
                                            literal("add")
                                            .then(
                                                    argument("имя", StringArgumentType.word())
                                                    .then(argument("ссылка", StringArgumentType.greedyString()))
                                            )
                                    )
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
    private static final List<String> TAB_2 = List.of(
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
    private static final List<String> TAB_3_PRONOUNS = List.of(
            "he",
            "she",
            "they"
    );
    private static final List<String> TAB_3_GAME_PARAMS = List.of(
            "game-mode",
            "health",
            "air"
    );
    private static final List<String> TAB_3_SETTINGS = List.of(
            "resourcepack-type"
    );
    private static final List<String> TAB_3_BAN_MUTE_INFO = List.of(
            "reason",
            "time"
    );
    private static final List<String> TAB_3_NAME = List.of(
            "reset",
            "first-name",
            "last-name",
            "patronymic"
    );
    private static final List<String> TAB_4_GAME_PARAMS_GAME_MODE = List.of(
            "survival",
            "creative",
            "spectator",
            "adventure"
    );
    private static final List<String> TAB_4_GAME_PARAMS_AIR = List.of(
            "300",
            "0"
    );
    private static final List<String> TAB_4_GAME_PARAMS_HEALTH = List.of(
            "20.0",
            "0.0"
    );
    private static final List<String> TAB_4_SETTINGS_RESOURCEPACK_TYPE = List.of(
            "full",
            "lite",
            "none",
            "null"
    );
    private static final List<String> TAB_4_SETTINGS_SKIN = List.of(
            "set",
            "remove",
            "add"
    );
    private static final List<String> TAB_4_BAN_MUTE_INFO_REASON = List.of("неизвестно");
    private static final List<String> TAB_4_NAME_EMPTY = List.of("empty");

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length < 2) return false;

        OfflinePlayer offlinePlayer;

        if (IDUtils.matchesIDRegex(args[0])) {
            IDMap idMap = MSEssentials.getCache().idMap;
            offlinePlayer = idMap.getPlayerByID(args[0]);
        } else if (args[0].length() > 2) {
            offlinePlayer = PlayerUtils.getOfflinePlayerByNick(args[0]);
        } else {
            ChatUtils.sendWarning(sender, Component.translatable("ms.error.name_length"));
            return true;
        }

        if (offlinePlayer == null || offlinePlayer.getName() == null) {
            ChatUtils.sendError(sender, Component.translatable("ms.error.id_not_found"));
            return true;
        }

        return runCommand(sender, args, offlinePlayer);
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
                return TAB_2;
            }
            case 3 -> {
                switch (args[1]) {
                    case "pronouns" -> {
                        return TAB_3_PRONOUNS;
                    }
                    case "game-params" -> {
                        return TAB_3_GAME_PARAMS;
                    }
                    case "settings" -> {
                        return TAB_3_SETTINGS;
                    }
                    case "ban-info", "mute-info" -> {
                        return TAB_3_BAN_MUTE_INFO;
                    }
                    case "name" -> {
                        return TAB_3_NAME;
                    }
                }
            }
            case 4 -> {
                switch (args[1]) {
                    case "game-params" -> {
                        switch (args[2]) {
                            case "game-mode" -> {
                                return TAB_4_GAME_PARAMS_GAME_MODE;
                            }
                            case "air" -> {
                                return TAB_4_GAME_PARAMS_AIR;
                            }
                            case "health" -> {
                                return TAB_4_GAME_PARAMS_HEALTH;
                            }
                        }
                    }
                    case "settings" -> {
                        switch (args[2]) {
                            case "resourcepack-type" -> {
                                return TAB_4_SETTINGS_RESOURCEPACK_TYPE;
                            }
                            case "skin" -> {
                                return TAB_4_SETTINGS_SKIN;
                            }
                        }
                    }
                    case "ban-info", "mute-info" -> {
                        switch (args[2]) {
                            case "time" -> {
                                return DateUtils.getTimeSuggestions(args[3]);
                            }
                            case "reason" -> {
                                return TAB_4_BAN_MUTE_INFO_REASON;
                            }
                        }
                    }
                    case "name" -> {
                        switch (args[2]) {
                            case "last-name", "patronymic" -> {
                                return TAB_4_NAME_EMPTY;
                            }
                        }
                    }
                }
            }
            case 5 -> {
                switch (args[2]) {
                    case "skin" -> {
                        OfflinePlayer offlinePlayer = null;

                        if (IDUtils.matchesIDRegex(args[0])) {
                            IDMap idMap = MSEssentials.getCache().idMap;
                            offlinePlayer = idMap.getPlayerByID(args[0]);
                        } else if (args[0].length() > 2) {
                            offlinePlayer = PlayerUtils.getOfflinePlayerByNick(args[0]);
                        }

                        if (offlinePlayer == null) return EMPTY_LIST;

                        PlayerInfo playerInfo = PlayerInfo.fromMap(offlinePlayer);

                        switch (args[3]) {
                            case "set", "remove" -> {
                                return playerInfo == null ? EMPTY_LIST : playerInfo.getPlayerFile().getSkins().stream().map(Skin::getName).toList();
                            }
                        }
                    }
                }
            }
        }
        return EMPTY_LIST;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }

    private static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull [] args,
            @NotNull OfflinePlayer offlinePlayer
    ) {
        PlayerInfo playerInfo = PlayerInfo.fromMap(offlinePlayer);

        return playerInfo != null && switch (args[1]) {
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
