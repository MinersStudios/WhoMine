package com.minersstudios.msessentials.commands.other;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.utils.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.tabcompleters.AllLocalPlayers;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.minersstudios.msessentials.utils.MessageUtils.sendPrivateMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "privatemessage",
        aliases = {
                "pmessage",
                "pm",
                "w",
                "tell",
                "msg"
        },
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм] [сообщение]",
        description = "Отправь другому игроку приватное сообщение"
)
public class PrivateMessageCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("privatemessage")
            .then(
                    argument("id/никнейм", StringArgumentType.word())
                    .then(argument("сообщение", StringArgumentType.greedyString()))
            ).build();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length < 2) return false;

        PlayerInfo senderInfo = sender instanceof Player player
                ? PlayerInfo.fromOnlinePlayer(player)
                : MSEssentials.getConsolePlayerInfo();

        if (senderInfo.isMuted()) {
            MSLogger.warning(sender, Component.translatable("ms.command.mute.already.receiver"));
            return true;
        }

        String message = ChatUtils.extractMessage(args, 1);

        PlayerInfo playerInfo = PlayerInfo.fromString(args[0]);

        if (playerInfo == null) {
            MSLogger.severe(sender, Component.translatable("ms.error.player_not_found"));
            return true;
        }

        if (!playerInfo.isOnline() && !sender.hasPermission("msessentials.*")) {
            MSLogger.warning(sender, Component.translatable("ms.error.player_not_online"));
            return true;
        }

        sendPrivateMessage(senderInfo, playerInfo, text(message));
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
