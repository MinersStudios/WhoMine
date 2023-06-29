package com.github.minersstudios.msessentials.commands.roleplay;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.PlayerInfoMap;
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

import static com.github.minersstudios.msessentials.utils.MessageUtils.RolePlayActionType.TODO;
import static com.github.minersstudios.msessentials.utils.MessageUtils.sendRPEventMessage;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "todo",
        usage = " ꀑ §cИспользуй: /<command> [речь] * [действие]",
        description = "Описывает действие и речь в чате"
)
public class TodoCommand implements MSCommandExecutor {

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

        PlayerInfoMap playerInfoMap = MSEssentials.getConfigCache().playerInfoMap;
        PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(player);

        if (!playerInfo.isOnline()) return true;

        String message = ChatUtils.extractMessage(args, 0);

        if (args.length < 3 || !message.contains("*")) return false;
        if (playerInfo.isMuted()) {
            ChatUtils.sendWarning(player, Component.translatable("ms.command.mute.already.receiver"));
            return true;
        }

        String
                action = message.substring(message.indexOf('*') + 1).trim(),
                speech = message.substring(0, message.indexOf('*')).trim();

        if (action.isEmpty() || speech.isEmpty()) return false;

        sendRPEventMessage(player, text(speech), text(action), TODO);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return LiteralArgumentBuilder.literal("todo")
                .then(
                        RequiredArgumentBuilder.argument("речь", StringArgumentType.greedyString())
                        .then(
                                LiteralArgumentBuilder.literal("*")
                                .then(RequiredArgumentBuilder.argument("действие", StringArgumentType.greedyString()))
                        )
                ).build();
    }
}
