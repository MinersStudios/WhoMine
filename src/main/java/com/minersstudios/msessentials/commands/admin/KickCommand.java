package com.minersstudios.msessentials.commands.admin;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.msessentials.util.MSPlayerUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "kick",
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм] [причина]",
        description = "Кикнуть игрока",
        permission = "msessentials.kick",
        permissionDefault = PermissionDefault.OP
)
public class KickCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("kick")
            .then(
                    argument("id/никнейм", StringArgumentType.word())
                    .then(argument("причина", StringArgumentType.greedyString()))
            ).build();

    private static final TranslatableComponent PLAYER_NOT_FOUND = translatable("ms.error.player_not_found");
    private static final TranslatableComponent PLAYER_NOT_ONLINE = translatable("ms.error.player_not_online");
    private static final TranslatableComponent KICK_TITLE = translatable("ms.command.kick.message.receiver.title");
    private static final TranslatableComponent KICK_SUBTITLE = translatable("ms.command.kick.message.receiver.subtitle");
    private static final TranslatableComponent KICK_SENDER = translatable("ms.command.kick.message.sender");

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length == 0) return false;

        final Component reason = args.length > 1
                ? text(ChatUtils.extractMessage(args, 1))
                : LanguageFile.renderTranslationComponent("ms.command.kick.default_reason");

        final PlayerInfo playerInfo = PlayerInfo.fromString(args[0]);

        if (playerInfo == null) {
            MSLogger.severe(sender, PLAYER_NOT_FOUND);
            return true;
        }

        final Player player = playerInfo.getOnlinePlayer();

        if (player == null) {
            MSLogger.warning(sender, PLAYER_NOT_ONLINE);
            return true;
        }

        playerInfo.kickPlayer(KICK_TITLE, KICK_SUBTITLE.args(reason));
        MSLogger.fine(
                sender,
                KICK_SENDER.args(
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
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return args.length == 1
                ? MSPlayerUtils.getLocalPlayerNames()
                : EMPTY_TAB;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
