package com.minersstudios.msessentials.commands.minecraft.admin.player;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.util.DateUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.IDMap;
import com.minersstudios.msessentials.util.IDUtils;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "player",
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм] [параметры]",
        description = "Команды, отвечающие за параметры игрока",
        permission = "msessentials.player.*",
        permissionDefault = PermissionDefault.OP
)
public final class AdminPlayerCommandHandler extends MSCommandExecutor<MSEssentials> {
    private static final List<String> TAB_2 = ImmutableList.of(
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
    private static final List<String> TAB_3_PRONOUNS = ImmutableList.of(
            "he",
            "she",
            "they"
    );
    private static final List<String> TAB_3_GAME_PARAMS = ImmutableList.of(
            "game-mode",
            "health",
            "air"
    );
    private static final List<String> TAB_3_SETTINGS = ImmutableList.of(
            "resourcepack-type"
    );
    private static final List<String> TAB_3_BAN_MUTE_INFO = ImmutableList.of(
            "reason",
            "time"
    );
    private static final List<String> TAB_3_NAME = ImmutableList.of(
            "reset",
            "first-name",
            "last-name",
            "patronymic"
    );
    private static final List<String> TAB_4_GAME_PARAMS_GAME_MODE = ImmutableList.of(
            "survival",
            "creative",
            "spectator",
            "adventure"
    );
    private static final List<String> TAB_4_GAME_PARAMS_AIR = ImmutableList.of(
            "300",
            "0"
    );
    private static final List<String> TAB_4_GAME_PARAMS_HEALTH = ImmutableList.of(
            "20.0",
            "0.0"
    );
    private static final List<String> TAB_4_SETTINGS_RESOURCEPACK_TYPE = ImmutableList.of(
            "full",
            "lite",
            "none",
            "null"
    );
    private static final List<String> TAB_4_SETTINGS_SKIN = ImmutableList.of(
            "set",
            "remove",
            "add"
    );
    private final List<String> TAB_4_BAN_INFO_REASON = ImmutableList.of(LanguageFile.renderTranslation("ms.command.ban.default_reason"));
    private final List<String> TAB_4_MUTE_INFO_REASON = ImmutableList.of(LanguageFile.renderTranslation("ms.command.mute.default_reason"));
    private static final List<String> TAB_4_NAME_EMPTY = ImmutableList.of("empty");
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

    private static final TranslatableComponent PLAYER_NOT_FOUND = translatable("ms.error.player_not_found");

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length < 2) {
            return false;
        }

        final MSEssentials plugin = this.getPlugin();
        final PlayerInfo playerInfo = PlayerInfo.fromString(plugin, args[0]);

        if (playerInfo == null) {
            MSLogger.severe(sender, PLAYER_NOT_FOUND);
            return true;
        }

        return switch (args[1]) {
            case "update" ->      AdminUpdateCommand.runCommand(sender, playerInfo);
            case "info" ->        AdminInfoCommand.runCommand(plugin, sender, playerInfo);
            case "pronouns" ->    AdminPronounsCommand.runCommand(sender, args, playerInfo);
            case "game-params" -> AdminGameParamsCommand.runCommand(sender, args, playerInfo);
            case "first-join" ->  AdminFirstJoinCommand.runCommand(sender, playerInfo);
            case "settings" ->    AdminSettingsCommand.runCommand(plugin, sender, args, playerInfo);
            case "ban-info" ->    AdminBanInfoCommand.runCommand(sender, args, playerInfo);
            case "mute-info" ->   AdminMuteInfoCommand.runCommand(plugin, sender, args, playerInfo);
            case "name" ->        AdminNameCommand.runCommand(sender, args, playerInfo);
            default -> false;
        };
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        switch (args.length) {
            case 1 -> {
                final var completions = new ArrayList<String>();
                final IDMap idMap = this.getPlugin().getCache().getIdMap();

                for (final var offlinePlayer : sender.getServer().getOfflinePlayers()) {
                    final String nickname = offlinePlayer.getName();

                    if (nickname == null) {
                        continue;
                    }

                    final int id = idMap.getID(offlinePlayer.getUniqueId(), false, false);

                    if (id != -1) {
                        completions.add(String.valueOf(id));
                    }

                    if (offlinePlayer.hasPlayedBefore()) {
                        completions.add(nickname);
                    }
                }

                return completions;
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
                    case "ban-info" -> {
                        switch (args[2]) {
                            case "time" -> {
                                return DateUtils.getTimeSuggestions(args[3]);
                            }
                            case "reason" -> {
                                return TAB_4_BAN_INFO_REASON;
                            }
                        }
                    }
                    case "mute-info" -> {
                        switch (args[2]) {
                            case "time" -> {
                                return DateUtils.getTimeSuggestions(args[3]);
                            }
                            case "reason" -> {
                                return TAB_4_MUTE_INFO_REASON;
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
                        final OfflinePlayer offlinePlayer;

                        if (IDUtils.matchesIDRegex(args[0])) {
                            final IDMap idMap = this.getPlugin().getCache().getIdMap();
                            offlinePlayer = idMap.getPlayerByID(args[0]);
                        } else if (args[0].length() > 2) {
                            offlinePlayer = sender.getServer().getOfflinePlayer(args[0]);
                        } else {
                            return EMPTY_TAB;
                        }

                        final PlayerInfo playerInfo = PlayerInfo.fromOfflinePlayer(this.getPlugin(), offlinePlayer);

                        switch (args[3]) {
                            case "set", "remove" -> {
                                if (playerInfo == null) {
                                    return EMPTY_TAB;
                                }

                                final var skins = playerInfo.getPlayerFile().getSkins();
                                final var names = new ArrayList<String>(skins.size());

                                for (var skin : skins) {
                                    names.add(skin.getName());
                                }

                                return names;
                            }
                        }
                    }
                }
            }
        }

        return EMPTY_TAB;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
