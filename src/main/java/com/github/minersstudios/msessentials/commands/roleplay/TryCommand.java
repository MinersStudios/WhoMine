package com.github.minersstudios.msessentials.commands.roleplay;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;

import static com.github.minersstudios.msessentials.utils.MessageUtils.RolePlayActionType.ME;
import static com.github.minersstudios.msessentials.utils.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "try",
        usage = " ꀑ §cИспользуй: /<command> [действие]",
        description = "Рандомно определяет исход того, что делает ваш персонаж"
)
public class TryCommand implements MSCommandExecutor {
    private final SecureRandom random = new SecureRandom();
    private static final CommandNode<?> COMMAND_NODE =
            literal("try")
            .then(argument("действие", StringArgumentType.greedyString()))
            .build();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (!(sender instanceof Player player)) {
            ChatUtils.sendError(sender, Component.translatable("ms.error.only_player_command"));
            return true;
        }

        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        if (args.length == 0) return false;
        if (playerInfo.isMuted()) {
            ChatUtils.sendWarning(player, Component.translatable("ms.command.mute.already.receiver"));
            return true;
        }

        sendRPEventMessage(
                player,
                text(ChatUtils.extractMessage(args, 0))
                .append(Component.space())
                .append(new Component[]{
                        translatable("ms.command.try.variant.success", NamedTextColor.GREEN),
                        translatable("ms.command.try.variant.fail", NamedTextColor.RED)
                }[this.random.nextInt(2)]),
                ME
        );
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
