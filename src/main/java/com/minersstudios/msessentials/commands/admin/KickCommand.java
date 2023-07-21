package com.minersstudios.msessentials.commands.admin;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.utils.ChatUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.tabcompleters.AllLocalPlayers;
import com.minersstudios.mscore.config.LanguageFile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

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

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length == 0) return false;

        Component reason = args.length > 1
                ? Component.text(ChatUtils.extractMessage(args, 1))
                : LanguageFile.renderTranslationComponent("ms.command.kick.default_reason");

        PlayerInfo playerInfo = PlayerInfo.fromString(args[0]);

        if (playerInfo == null) {
            MSLogger.severe(sender, Component.translatable("ms.error.player_not_found"));
            return true;
        }

        if (playerInfo.isOnline(true)) {
            MSLogger.warning(sender, Component.translatable("ms.error.player_not_online"));
            return true;
        }

        playerInfo.kickPlayer(
                Component.translatable("ms.command.kick.message.receiver.title"),
                Component.translatable("ms.command.kick.message.receiver.subtitle", reason)
        );
        MSLogger.fine(
                sender,
                Component.translatable("ms.command.kick.message.sender").args(
                        playerInfo.getGrayIDGreenName(),
                        text(playerInfo.getNickname()),
                        reason
                )
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        return new AllLocalPlayers().onTabComplete(sender, command, label, args);
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}