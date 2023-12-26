package com.minersstudios.msessentials.command.impl.minecraft.player.roleplay;

import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.msessentials.utility.MessageUtils.RolePlayActionType.ME;
import static com.minersstudios.msessentials.utility.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@Command(
        command = "me",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [действие]",
        description = "Описывает, что делает ваш персонаж",
        playerOnly = true
)
public final class MeCommand extends AbstractCommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("me")
            .then(argument("action", StringArgumentType.greedyString()))
            .build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length == 0) {
            return false;
        }

        final Player player = (Player) sender;
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(this.getPlugin(), player);

        if (playerInfo.isMuted()) {
            MSLogger.warning(
                    player,
                    LanguageRegistry.Components.COMMAND_MUTE_ALREADY_RECEIVER
            );
            return true;
        }

        sendRPEventMessage(
                player,
                text(ChatUtils.extractMessage(args, 0)),
                ME
        );

        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
