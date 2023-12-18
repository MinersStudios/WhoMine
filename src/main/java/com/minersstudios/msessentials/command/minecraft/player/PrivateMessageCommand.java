package com.minersstudios.msessentials.command.minecraft.player;

import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.CommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.utility.MSPlayerUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.minersstudios.msessentials.utility.MessageUtils.sendPrivateMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@Command(
        command = "privatemessage",
        aliases = {
                "pmessage",
                "pm",
                "w",
                "tell",
                "msg"
        },
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [id/никнейм] [сообщение]",
        description = "Отправь другому игроку приватное сообщение"
)
public final class PrivateMessageCommand extends CommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("privatemessage")
            .then(
                    argument("id/никнейм", StringArgumentType.word())
                    .then(argument("сообщение", StringArgumentType.greedyString()))
            ).build();

    private static final TranslatableComponent MUTED = translatable("ms.command.mute.already.receiver");
    private static final TranslatableComponent PLAYER_NOT_FOUND = translatable("ms.error.player_not_found");
    private static final TranslatableComponent PLAYER_NOT_ONLINE = translatable("ms.error.player_not_online");

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length < 2) {
            return false;
        }

        final MSEssentials plugin = this.getPlugin();
        final PlayerInfo senderInfo =
                sender instanceof Player player
                ? PlayerInfo.fromOnlinePlayer(plugin, player)
                : plugin.getCache().getConsolePlayerInfo();

        if (senderInfo.isMuted()) {
            MSLogger.warning(sender, MUTED);
            return true;
        }

        final PlayerInfo receiverInfo = PlayerInfo.fromString(plugin, args[0]);

        if (receiverInfo == null) {
            MSLogger.severe(sender, PLAYER_NOT_FOUND);
            return true;
        }

        if (
                !receiverInfo.isOnline()
                && !sender.hasPermission("msessentials.*")
        ) {
            MSLogger.warning(sender, PLAYER_NOT_ONLINE);
            return true;
        }

        sendPrivateMessage(
                senderInfo,
                receiverInfo,
                text(ChatUtils.extractMessage(args, 1))
        );

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return args.length == 1
                ? MSPlayerUtils.getLocalPlayerNames(this.getPlugin())
                : EMPTY_TAB;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
