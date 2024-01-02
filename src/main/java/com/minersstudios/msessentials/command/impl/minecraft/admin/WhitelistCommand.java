package com.minersstudios.msessentials.command.impl.minecraft.admin;

import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.command.api.MSCommand;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "whitelist",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [add/remove/reload] [id/никнейм]",
        description = "Удаляет/добавляет игрока в вайтлист, или перезагружает его",
        permission = "msessentials.whitelist",
        permissionDefault = PermissionDefault.OP
)
public final class WhitelistCommand extends AbstractCommandExecutor<MSEssentials> {
    private static final List<String> TAB = Arrays.asList("add", "remove", "reload");
    private static final CommandNode<?> COMMAND_NODE =
            literal("whitelist")
            .then(
                    literal("add")
                    .then(argument("никнейм", StringArgumentType.word()))
            )
            .then(
                    literal("remove")
                    .then(argument("id/никнейм", StringArgumentType.word()))
            )
            .then(literal("reload"))
            .build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length == 0) {
            return false;
        }

        final Server server = sender.getServer();
        final String actionArg = args[0];
        final String playerArg = args.length == 2 ? args[1] : null;

        switch (actionArg) {
            case "reload" -> {
                server.reloadWhitelist();
                MSLogger.fine(
                        sender,
                        COMMAND_WHITE_LIST_RELOAD
                );

                return true;
            }
            case "add" -> {
                if (playerArg == null) {
                    return false;
                }

                final PlayerInfo playerInfo = PlayerInfo.fromString(this.getPlugin(), playerArg);

                if (playerInfo == null) {
                    MSLogger.severe(
                            sender,
                            ERROR_PLAYER_NOT_FOUND
                    );
                    return true;
                }

                if (playerInfo.setWhiteListed(true)) {
                    MSLogger.fine(
                            sender,
                            COMMAND_WHITE_LIST_ADD_SENDER_MESSAGE
                            .arguments(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname())
                            )
                    );
                    return true;
                }

                MSLogger.warning(
                        sender,
                        COMMAND_WHITE_LIST_ADD_ALREADY
                        .arguments(
                                playerInfo.getGrayIDGoldName(),
                                text(playerInfo.getNickname())
                        )
                );

                return true;
            }
            case "remove" -> {
                if (playerArg == null) {
                    return false;
                }

                final PlayerInfo playerInfo = PlayerInfo.fromString(this.getPlugin(), playerArg);

                if (playerInfo == null) {
                    MSLogger.severe(
                            sender,
                            ERROR_PLAYER_NOT_FOUND
                    );
                    return true;
                }

                if (playerInfo.setWhiteListed(false)) {
                    MSLogger.fine(
                            sender,
                            COMMAND_WHITE_LIST_REMOVE_SENDER_MESSAGE
                            .arguments(
                                    playerInfo.getGrayIDGreenName(),
                                    text(playerInfo.getNickname())
                            )
                    );
                    return true;
                }

                MSLogger.warning(
                        sender,
                        COMMAND_WHITE_LIST_REMOVE_NOT_FOUND
                        .arguments(
                                playerInfo.getGrayIDGoldName(),
                                text(playerArg)
                        )
                );

                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return switch (args.length) {
            case 1 -> TAB;
            case 2 -> {
                final var completions = new ObjectArrayList<String>();

                if (args[0].equals("remove")) {
                    final PlayerInfoMap playerInfoMap = this.getPlugin().getCache().getPlayerInfoMap();

                    for (final var offlinePlayer : sender.getServer().getWhitelistedPlayers()) {
                        final int id =
                                playerInfoMap
                                .get(offlinePlayer.getUniqueId(), args[1])
                                .getID(false, false);

                        if (id != -1) {
                            completions.add(String.valueOf(id));
                        }

                        completions.add(offlinePlayer.getName());
                    }
                }

                yield completions;
            }
            default -> EMPTY_TAB;
        };
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
