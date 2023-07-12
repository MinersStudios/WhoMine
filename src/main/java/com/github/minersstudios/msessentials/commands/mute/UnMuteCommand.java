package com.github.minersstudios.msessentials.commands.mute;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.DateUtils;
import com.github.minersstudios.mscore.utils.PlayerUtils;
import com.github.minersstudios.msessentials.Cache;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.map.IDMap;
import com.github.minersstudios.msessentials.player.map.MuteMap;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.utils.IDUtils;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "unmute",
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм]",
        description = "Размьютить игрока",
        permission = "msessentials.mute",
        permissionDefault = PermissionDefault.OP
)
public class UnMuteCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("unmute")
            .then(argument("id/никнейм", StringArgumentType.word()))
            .build();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length == 0) return false;

        Cache cache = MSEssentials.getCache();

        if (IDUtils.matchesIDRegex(args[0])) {
            IDMap idMap = cache.idMap;
            OfflinePlayer offlinePlayer = idMap.getPlayerByID(args[0]);

            if (
                    offlinePlayer == null
                    || !offlinePlayer.hasPlayedBefore()
                    || StringUtils.isBlank(offlinePlayer.getName())
            ) {
                ChatUtils.sendError(sender, Component.translatable("ms.error.id_not_found"));
                return true;
            }

            PlayerInfo.fromMap(offlinePlayer.getUniqueId(), offlinePlayer.getName())
                    .setMuted(false, sender);
            return true;
        }

        if (args[0].length() > 2) {
            String name = args[0];
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayerByNick(name);

            if (offlinePlayer == null) {
                ChatUtils.sendError(sender, Component.translatable("ms.error.player_not_found"));
                return true;
            }

            PlayerInfo.fromMap(offlinePlayer.getUniqueId(), name)
                    .setMuted(false, sender);
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
        switch (args.length) {
            case 1 -> {
                Cache cache = MSEssentials.getCache();
                MuteMap muteMap = cache.muteMap;
                IDMap idMap = cache.idMap;

                for (var uuid : muteMap.uuidSet()) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    String nickname = offlinePlayer.getName();

                    MuteMap.Params params = muteMap.getParams(offlinePlayer);
                    if (params != null && params.getExpiration().isBefore(Instant.now())) continue;

                    int id = idMap.getID(uuid, false, false);
                    if (id != -1) {
                        completions.add(String.valueOf(id));
                    }

                    if (nickname != null) {
                        completions.add(nickname);
                    }
                }
            }
            case 2 -> {
                return DateUtils.getTimeSuggestions(args[1]);
            }
        }
        return completions;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
