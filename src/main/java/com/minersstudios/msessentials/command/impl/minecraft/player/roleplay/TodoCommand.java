package com.minersstudios.msessentials.command.impl.minecraft.player.roleplay;

import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.command.api.MSCommand;
import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.msessentials.utility.MessageUtils.RolePlayActionType.TODO;
import static com.minersstudios.msessentials.utility.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "todo",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [речь] * [действие]",
        description = "Описывает действие и речь в чате",
        playerOnly = true
)
public final class TodoCommand extends AbstractCommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("todo")
            .then(
                    argument("speech", StringArgumentType.greedyString())
                    .then(
                            literal("*")
                            .then(argument("action", StringArgumentType.greedyString()))
                    )
            ).build();

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
        ) {
            return false;
        }

        final Player player = (Player) sender;
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(this.getPlugin(), player);

        if (playerInfo.isMuted()) {
            MSLogger.warning(
                    player,
                    Translations.COMMAND_MUTE_ALREADY_RECEIVER.asTranslatable()
            );

            return true;
        }

        final String action = message.substring(message.indexOf('*') + 1).trim();
        final String speech = message.substring(0, message.indexOf('*')).trim();

        if (
                action.isEmpty()
                || speech.isEmpty()
        ) {
            return false;
        }

        sendRPEventMessage(
                player,
                text(speech),
                text(action),
                TODO
        );

        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
