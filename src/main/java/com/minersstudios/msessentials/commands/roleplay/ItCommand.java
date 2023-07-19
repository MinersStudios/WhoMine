package com.minersstudios.msessentials.commands.roleplay;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.utils.ChatUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.msessentials.utils.MessageUtils.RolePlayActionType.IT;
import static com.minersstudios.msessentials.utils.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "it",
        usage = " ꀑ §cИспользуй: /<command> [действие]",
        description = "Описывает действие, от третьего лица"
)
public class ItCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("it")
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
            MSLogger.severe(sender, Component.translatable("ms.error.only_player_command"));
            return true;
        }

        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        if (args.length == 0) return false;
        if (playerInfo.isMuted()) {
            MSLogger.warning(player, Component.translatable("ms.command.mute.already.receiver"));
            return true;
        }

        sendRPEventMessage(player, Component.text(ChatUtils.extractMessage(args, 0)), IT);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
