package com.github.minersstudios.msessentials.commands.ban;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
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
        permission = "msessentials.ban",
        permissionDefault = PermissionDefault.OP
)
public class UnBanCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("unban")
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
            MSLogger.severe(sender, Component.translatable("ms.error.player_not_found"));
            return true;
        }

        playerInfo.pardon(sender);
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
            for (var offlinePlayer : sender.getServer().getBannedPlayers()) {
                if (offlinePlayer != null && !StringUtils.isBlank(offlinePlayer.getName())) {
                    PlayerInfo playerInfo = PlayerInfo.fromProfile(offlinePlayer.getUniqueId(), offlinePlayer.getName());
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
        return COMMAND_NODE;
    }
}
