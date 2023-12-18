package com.minersstudios.msessentials.commands.minecraft.player.roleplay;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.msessentials.util.MessageUtils.RolePlayActionType.DO;
import static com.minersstudios.msessentials.util.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "do",
        usage = " ꀑ §cИспользуй: /<command> [действие]",
        description = "Описывает состояние вашего персонажа и объектов вокруг вас",
        playerOnly = true
)
public final class DoCommand extends MSCommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("do")
            .then(argument("action", StringArgumentType.greedyString()))
            .build();

    private static final TranslatableComponent MUTED = translatable("ms.command.mute.already.receiver");

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length == 0) {
            return false;
        }

        final Player player = (Player) sender;
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(this.getPlugin(), player);

        if (playerInfo.isMuted()) {
            MSLogger.warning(player, MUTED);
            return true;
        }

        sendRPEventMessage(player, text(ChatUtils.extractMessage(args, 0)), DO);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
