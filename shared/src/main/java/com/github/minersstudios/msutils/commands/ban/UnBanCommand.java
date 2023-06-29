package com.github.minersstudios.msutils.commands.ban;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.PlayerUtils;
import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.config.ConfigCache;
import com.github.minersstudios.msutils.player.IDMap;
import com.github.minersstudios.msutils.player.PlayerInfo;
import com.github.minersstudios.msutils.player.PlayerInfoMap;
import com.github.minersstudios.msutils.utils.IDUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "unban",
        aliases = {"pardon"},
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм]",
        description = "Разбанить игрока",
        permission = "msutils.ban",
        permissionDefault = PermissionDefault.OP
)
public class UnBanCommand implements MSCommandExecutor {

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length == 0) return false;

        ConfigCache configCache = MSUtils.getConfigCache();
        PlayerInfoMap playerInfoMap = configCache.playerInfoMap;

        if (IDUtils.matchesIDRegex(args[0])) {
            IDMap idMap = configCache.idMap;
            OfflinePlayer offlinePlayer = idMap.getPlayerByID(args[0]);

            if (
                    offlinePlayer == null
                    || !offlinePlayer.hasPlayedBefore()
                    || StringUtils.isBlank(offlinePlayer.getName())
            ) {
                ChatUtils.sendError(sender, Component.translatable("ms.error.id_not_found"));
                return true;
            }

            playerInfoMap.getPlayerInfo(offlinePlayer.getUniqueId(), offlinePlayer.getName())
                    .setBanned(false, sender);
            return true;
        }

        if (args[0].length() > 2) {
            String name = args[0];
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayerByNick(name);

            if (offlinePlayer == null) {
                ChatUtils.sendError(sender, Component.translatable("ms.error.player_not_found"));
                return true;
            }

            playerInfoMap.getPlayerInfo(offlinePlayer.getUniqueId(), name)
                    .setBanned(false, sender);
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
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            PlayerInfoMap playerInfoMap = MSUtils.getConfigCache().playerInfoMap;

            for (OfflinePlayer offlinePlayer : Bukkit.getBannedPlayers()) {
                if (offlinePlayer != null && !StringUtils.isBlank(offlinePlayer.getName())) {
                    PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(offlinePlayer.getUniqueId(), offlinePlayer.getName());
                    int id = playerInfo.getID(false, false);

                    if (id != -1) {
                        completions.add(String.valueOf(id));
                    }

                    completions.add(offlinePlayer.getName());
                }
            }
            return completions;
        }
        return completions;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return literal("unban")
                .then(argument("id/никнейм", StringArgumentType.word()))
                .build();
    }
}
