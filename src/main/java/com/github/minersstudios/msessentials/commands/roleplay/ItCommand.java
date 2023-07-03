package com.github.minersstudios.msessentials.commands.roleplay;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.minersstudios.msessentials.utils.MessageUtils.RolePlayActionType.IT;
import static com.github.minersstudios.msessentials.utils.MessageUtils.sendRPEventMessage;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "it",
        usage = " ꀑ §cИспользуй: /<command> [действие]",
        description = "Описывает действие, от третьего лица"
)
public class ItCommand implements MSCommandExecutor {

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

        PlayerInfo playerInfo = PlayerInfo.fromMap(player);

        if (args.length == 0) return false;
        if (playerInfo.isMuted()) {
            ChatUtils.sendWarning(player, Component.translatable("ms.command.mute.already.receiver"));
            return true;
        }

        sendRPEventMessage(player, text(ChatUtils.extractMessage(args, 0)), IT);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return LiteralArgumentBuilder.literal("it")
                .then(RequiredArgumentBuilder.argument("действие", StringArgumentType.greedyString()))
                .build();
    }
}
