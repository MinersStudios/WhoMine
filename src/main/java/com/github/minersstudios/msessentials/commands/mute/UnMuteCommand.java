package com.github.minersstudios.msessentials.commands.mute;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.DateUtils;
import com.github.minersstudios.msessentials.Cache;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.map.IDMap;
import com.github.minersstudios.msessentials.player.map.MuteMap;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
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

        PlayerInfo playerInfo = PlayerInfo.fromString(args[0]);

        if (playerInfo == null) {
            ChatUtils.sendError(sender, Component.translatable("ms.error.player_not_found"));
            return true;
        }

        playerInfo.setMuted(false, sender);
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
