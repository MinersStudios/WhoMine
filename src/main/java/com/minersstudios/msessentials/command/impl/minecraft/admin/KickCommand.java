package com.minersstudios.msessentials.command.impl.minecraft.admin;

import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.CommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.utility.MSPlayerUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@Command(
        command = "kick",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [id/никнейм] [причина]",
        description = "Кикнуть игрока",
        permission = "msessentials.kick",
        permissionDefault = PermissionDefault.OP
)
public final class KickCommand extends CommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("kick")
            .then(
                    argument("id/никнейм", StringArgumentType.word())
                    .then(argument("причина", StringArgumentType.greedyString()))
            ).build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length == 0) {
            return false;
        }

        final Component reason = args.length > 1
                ? text(ChatUtils.extractMessage(args, 1))
                : COMMAND_KICK_DEFAULT_REASON;

        final PlayerInfo playerInfo = PlayerInfo.fromString(this.getPlugin(), args[0]);

        if (playerInfo == null) {
            MSLogger.severe(
                    sender,
                    ERROR_PLAYER_NOT_FOUND
            );
            return true;
        }

        final Player player = playerInfo.getOnlinePlayer();

        if (player == null) {
            MSLogger.warning(
                    sender,
                    ERROR_PLAYER_NOT_ONLINE
            );
            return true;
        }

        playerInfo.kickPlayer(
                COMMAND_KICK_MESSAGE_RECEIVER_TITLE,
                COMMAND_KICK_MESSAGE_RECEIVER_SUBTITLE
                        .args(reason)
        );
        MSLogger.fine(
                sender,
                COMMAND_KICK_MESSAGE_SENDER
                .args(
                        playerInfo.getGrayIDGreenName(),
                        text(playerInfo.getNickname()),
                        reason
                )
        );

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return args.length == 1
                ? MSPlayerUtils.getLocalPlayerNames(this.getPlugin())
                : EMPTY_TAB;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
