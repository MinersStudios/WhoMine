package com.minersstudios.msessentials.commands.minecraft.player.roleplay;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.msessentials.util.MessageUtils.RolePlayActionType.TODO;
import static com.minersstudios.msessentials.util.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "todo",
        usage = " ꀑ §cИспользуй: /<command> [речь] * [действие]",
        description = "Описывает действие и речь в чате",
        playerOnly = true
)
public final class TodoCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("todo")
            .then(
                    argument("speech", StringArgumentType.greedyString())
                    .then(
                            literal("*")
                            .then(argument("action", StringArgumentType.greedyString()))
                    )
            ).build();

    private static final TranslatableComponent MUTED = translatable("ms.command.mute.already.receiver");

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        final String message = ChatUtils.extractMessage(args, 0);

        if (
                args.length < 3
                || !message.contains("*")
        ) return false;

        final Player player = (Player) sender;
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        if (playerInfo.isMuted()) {
            MSLogger.warning(player, MUTED);
            return true;
        }

        final String action = message.substring(message.indexOf('*') + 1).trim();
        final String speech = message.substring(0, message.indexOf('*')).trim();

        if (
                action.isEmpty()
                || speech.isEmpty()
        ) return false;

        sendRPEventMessage(player, text(speech), text(action), TODO);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}