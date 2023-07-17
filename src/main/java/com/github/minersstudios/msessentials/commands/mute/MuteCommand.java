package com.github.minersstudios.msessentials.commands.mute;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.logger.MSLogger;
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
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslation;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "mute",
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм] [время][s/m/h/d/M/y] [причина]",
        description = "Покажи кто тут главный и замьють игрока",
        permission = "msessentials.mute",
        permissionDefault = PermissionDefault.OP
)
public class MuteCommand implements MSCommandExecutor {
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
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length < 2) return false;

        Instant date = DateUtils.getDateFromString(args[1], false);

        if (date == null) {
            MSLogger.severe(sender, Component.translatable("ms.error.format"));
            return true;
        }

        String reason = args.length > 2
                ? ChatUtils.extractMessage(args, 2)
                : renderTranslation("ms.command.mute.default_reason");

        PlayerInfo playerInfo = PlayerInfo.fromString(args[0]);

        if (playerInfo == null) {
            MSLogger.severe(sender, Component.translatable("ms.error.player_not_found"));
            return true;
        }

        playerInfo.setMuted(true, date, reason, sender);
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

                for (var offlinePlayer : sender.getServer().getOfflinePlayers()) {
                    String nickname = offlinePlayer.getName();
                    UUID uuid = offlinePlayer.getUniqueId();

                    if (StringUtils.isBlank(nickname) || muteMap.isMuted(offlinePlayer)) continue;

                    int id = idMap.getID(uuid, false, false);

                    if (id != -1) {
                        completions.add(String.valueOf(id));
                    }

                    if (offlinePlayer.hasPlayedBefore()) {
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
