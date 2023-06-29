package com.github.minersstudios.msessentials.commands.other;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.PlayerUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.config.ConfigCache;
import com.github.minersstudios.msessentials.player.IDMap;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.PlayerInfoMap;
import com.github.minersstudios.msessentials.tabcompleters.AllLocalPlayers;
import com.github.minersstudios.msessentials.utils.IDUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.minersstudios.msessentials.utils.MessageUtils.sendPrivateMessage;
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

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length < 2) return false;

        ConfigCache configCache = MSEssentials.getConfigCache();
        PlayerInfoMap playerInfoMap = configCache.playerInfoMap;
        PlayerInfo senderInfo = sender instanceof Player player
                ? playerInfoMap.getPlayerInfo(player)
                : MSEssentials.getConsolePlayerInfo();
        TranslatableComponent playerNotOnline = Component.translatable("ms.error.player_not_online");

        if (senderInfo.isMuted()) {
            ChatUtils.sendWarning(sender, Component.translatable("ms.command.mute.already.receiver"));
            return true;
        }

        String message = ChatUtils.extractMessage(args, 1);

        if (IDUtils.matchesIDRegex(args[0])) {
            IDMap idMap = configCache.idMap;
            OfflinePlayer offlinePlayer = idMap.getPlayerByID(args[0]);

            if (!(offlinePlayer instanceof Player player)) {
                ChatUtils.sendWarning(sender, playerNotOnline);
                return true;
            }

            PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(player);

            if (!playerInfo.isOnline() && !sender.hasPermission("msessentials.*")) {
                ChatUtils.sendWarning(sender, playerNotOnline);
                return true;
            }

            sendPrivateMessage(senderInfo, playerInfo, text(message));
            return true;
        }

        if (args[0].length() > 2) {
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayerByNick(args[0]);

            if (offlinePlayer instanceof Player player) {
                PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(player);

                if (playerInfo.isOnline() || sender.hasPermission("msessentials.*")) {
                    sendPrivateMessage(senderInfo, playerInfo, text(message));
                    return true;
                }
            }

            ChatUtils.sendWarning(sender, playerNotOnline);
            return true;
        }

        ChatUtils.sendWarning(sender, Component.translatable("ms.error.name_length"));
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
        return literal("privatemessage")
                .then(
                        argument("id/никнейм", StringArgumentType.word())
                        .then(argument("сообщение", StringArgumentType.greedyString()))
                ).build();
    }
}
