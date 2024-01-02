package com.minersstudios.msessentials.command.impl.minecraft.admin.ban;

import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.command.api.MSCommand;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.DateUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
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

import static com.minersstudios.mscore.language.LanguageRegistry.Components.ERROR_PLAYER_NOT_FOUND;
import static com.minersstudios.mscore.language.LanguageRegistry.Components.ERROR_WRONG_FORMAT;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "ban",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [id/никнейм] [время][s/m/h/d/M/y] [причина]",
        description = "Покажи кто тут главный и забань игрока",
        permission = "msessentials.ban",
        permissionDefault = PermissionDefault.OP
)
public final class BanCommand extends AbstractCommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("ban")
            .then(
                    argument("id/nickname", StringArgumentType.word())
                    .then(
                            argument("time", StringArgumentType.word())
                            .then(argument("reason", StringArgumentType.greedyString()))
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
                    ERROR_WRONG_FORMAT
            );
            return true;
        }

        final String reason = args.length > 2
                ? ChatUtils.extractMessage(args, 2)
                : LanguageRegistry.Strings.COMMAND_BAN_DEFAULT_REASON;

        final PlayerInfo playerInfo = PlayerInfo.fromString(this.getPlugin(), args[0]);

        if (playerInfo == null) {
            MSLogger.severe(
                    sender,
                    ERROR_PLAYER_NOT_FOUND
            );
            return true;
        }

        playerInfo.setBanned(true, date, reason, sender);
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

                for (final var offlinePlayer : sender.getServer().getOfflinePlayers()) {
                    final String nickname = offlinePlayer.getName();
                    final UUID uuid = offlinePlayer.getUniqueId();

                    if (
                            ChatUtils.isBlank(nickname)
                            || offlinePlayer.isBanned()
                    ) {
                        continue;
                    }

                    final int id = this.getPlugin().getCache().getIdMap().getID(uuid, false, false);

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
