package com.minersstudios.msessentials.command.impl.minecraft.admin.mute;

import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.command.api.MSCommand;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.minersstudios.mscore.locale.Translations.*;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "mute",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [id/никнейм] [время][s/m/h/d/M/y] [причина]",
        description = "Покажи кто тут главный и замьють игрока",
        permission = "msessentials.mute",
        permissionDefault = PermissionDefault.OP
)
public final class MuteCommand extends AbstractCommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("mute")
            .then(
                    argument("id/никнейм", StringArgumentType.word())
                    .then(
                            argument("время", StringArgumentType.word())
                            .then(argument("причина", StringArgumentType.greedyString()))
                    )
            ).build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length < 2) {
            return false;
        }

        final Instant date = DateUtils.getDateFromString(args[1], false);

        if (date == null) {
            MSLogger.severe(
                    sender,
                    ERROR_WRONG_FORMAT.asTranslatable()
            );

            return true;
        }

        final String reason = args.length > 2
                ? ChatUtils.extractMessage(args, 2)
                : COMMAND_MUTE_DEFAULT_REASON.asString();

        final PlayerInfo playerInfo = PlayerInfo.fromString(this.getPlugin(), args[0]);

        if (playerInfo == null) {
            MSLogger.severe(
                    sender,
                    ERROR_PLAYER_NOT_FOUND.asTranslatable()
            );

            return true;
        }

        playerInfo.setMuted(true, date, reason, sender);

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

                for (final var offlinePlayer : sender.getServer().getOfflinePlayers()) {
                    final String nickname = offlinePlayer.getName();
                    final UUID uuid = offlinePlayer.getUniqueId();

                    if (
                            ChatUtils.isBlank(nickname)
                            || muteMap.isMuted(offlinePlayer)
                    ) {
                        continue;
                    }

                    final int id = idMap.getID(uuid, false, false);

                    if (id != -1) {
                        completions.add(String.valueOf(id));
                    }

                    if (offlinePlayer.hasPlayedBefore()) {
                        completions.add(nickname);
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
