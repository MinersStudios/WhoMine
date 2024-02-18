package com.minersstudios.msessentials.command.impl.minecraft.admin.mute;

import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.command.api.MSCommand;
import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.DateUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.EssentialsCache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.IDMap;
import com.minersstudios.msessentials.player.collection.MuteMap;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "unmute",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [id/никнейм]",
        description = "Размьютить игрока",
        permission = "msessentials.mute",
        permissionDefault = PermissionDefault.OP
)
public final class UnMuteCommand extends AbstractCommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("unmute")
            .then(argument("id/никнейм", StringArgumentType.word()))
            .build();

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

        final PlayerInfo playerInfo = PlayerInfo.fromString(this.getPlugin(), args[0]);

        if (playerInfo == null) {
            MSLogger.severe(
                    sender,
                    Translations.ERROR_PLAYER_NOT_FOUND.asString()
            );

            return true;
        }

        playerInfo.unmute(sender);

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        switch (args.length) {
            case 1 -> {
                final var completions = new ObjectArrayList<String>();
                final EssentialsCache cache = this.getPlugin().getCache();
                final MuteMap muteMap = cache.getMuteMap();
                final IDMap idMap = cache.getIdMap();
                final Server server = sender.getServer();

                for (final var uuid : muteMap.uuidSet()) {
                    final OfflinePlayer offlinePlayer = server.getOfflinePlayer(uuid);

                    if (muteMap.isMuted(offlinePlayer)) {
                        final int id = idMap.getID(uuid, false, false);
                        final String name = offlinePlayer.getName();

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
