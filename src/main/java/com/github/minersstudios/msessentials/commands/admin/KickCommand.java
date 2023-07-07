package com.github.minersstudios.msessentials.commands.admin;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.PlayerUtils;
import com.github.minersstudios.msessentials.Cache;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.map.IDMap;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.tabcompleters.AllLocalPlayers;
import com.github.minersstudios.msessentials.utils.IDUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "kick",
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм] [причина]",
        description = "Кикнуть игрока",
        permission = "msessentials.kick",
        permissionDefault = PermissionDefault.OP
)
public class KickCommand implements MSCommandExecutor {

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length == 0) return false;

        Cache cache = MSEssentials.getCache();
        Component reason = args.length > 1
                ? text(ChatUtils.extractMessage(args, 1))
                : text("неизвестно");
        TranslatableComponent playerNotOnline = Component.translatable("ms.error.player_not_online");
        TranslatableComponent kickTitle = Component.translatable("ms.command.kick.message.receiver.title");
        TranslatableComponent kickSubtitle = Component.translatable("ms.command.kick.message.receiver.subtitle", reason);
        TranslatableComponent kickMessageFormat = Component.translatable("ms.command.kick.message.sender");

        if (IDUtils.matchesIDRegex(args[0])) {
            IDMap idMap = cache.idMap;
            OfflinePlayer offlinePlayer = idMap.getPlayerByID(args[0]);

            if (offlinePlayer == null || offlinePlayer.getName() == null) {
                ChatUtils.sendError(sender, Component.translatable("ms.error.id_not_found"));
                return true;
            }

            String nickname = offlinePlayer.getName();
            PlayerInfo playerInfo = PlayerInfo.fromMap(offlinePlayer.getUniqueId(), nickname);

            if (playerInfo.kickPlayer(kickTitle, kickSubtitle)) {
                ChatUtils.sendFine(
                        sender,
                        kickMessageFormat.args(
                                playerInfo.getGrayIDGreenName(),
                                text(nickname),
                                reason
                        )
                );
                return true;
            }

            ChatUtils.sendWarning(sender, playerNotOnline);
            return true;
        }

        if (args[0].length() > 2) {
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayerByNick(args[0]);

            if (offlinePlayer == null) {
                ChatUtils.sendError(sender, Component.translatable("ms.error.player_not_found"));
                return true;
            }

            PlayerInfo playerInfo = PlayerInfo.fromMap(offlinePlayer.getUniqueId(), args[0]);

            if (playerInfo.kickPlayer(kickTitle, kickSubtitle)) {
                ChatUtils.sendFine(
                        sender,
                        kickMessageFormat.args(
                                playerInfo.getGrayIDGreenName(),
                                text(args[0]),
                                reason
                        )
                );
                return true;
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
        return LiteralArgumentBuilder.literal("kick")
                .then(
                        RequiredArgumentBuilder.argument("id/никнейм", StringArgumentType.word())
                        .then(RequiredArgumentBuilder.argument("причина", StringArgumentType.greedyString()))
                ).build();
    }
}
