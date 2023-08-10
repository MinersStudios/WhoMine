package com.minersstudios.msessentials.commands.admin.mute;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.util.DateUtils;
import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.MuteMap;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.translatable;

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
    private static final TranslatableComponent PLAYER_NOT_FOUND = translatable("ms.error.player_not_found");

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
            MSLogger.severe(sender, PLAYER_NOT_FOUND);
            return true;
        }

        playerInfo.unmute(sender);
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        switch (args.length) {
            case 1 -> {
                var completions = new ArrayList<String>();
                Cache cache = MSEssentials.getCache();
                MuteMap muteMap = cache.muteMap;
                Server server = sender.getServer();

                for (var uuid : muteMap.uuidSet()) {
                    OfflinePlayer offlinePlayer = server.getOfflinePlayer(uuid);

                    if (muteMap.isMuted(offlinePlayer)) {
                        String name = offlinePlayer.getName();
                        int id = cache.idMap.getID(uuid, false, false);

                        if (id != -1) {
                            completions.add(String.valueOf(id));
                        }

                        if (name != null) {
                            completions.add(name);
                        }
                    }
                }

                return completions;
            }
            case 2 -> {
                return DateUtils.getTimeSuggestions(args[1]);
            }
            default -> {
                return EMPTY_TAB;
            }
        }
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
