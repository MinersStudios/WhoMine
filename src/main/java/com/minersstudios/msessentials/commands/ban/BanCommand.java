package com.minersstudios.msessentials.commands.ban;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.config.LanguageFile;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.utils.ChatUtils;
import com.minersstudios.mscore.utils.DateUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.map.IDMap;
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

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "ban",
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм] [время][s/m/h/d/M/y] [причина]",
        description = "Покажи кто тут главный и забань игрока",
        permission = "msessentials.ban",
        permissionDefault = PermissionDefault.OP
)
public class BanCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("ban")
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
                : LanguageFile.renderTranslation("ms.command.ban.default_reason");

        PlayerInfo playerInfo = PlayerInfo.fromString(args[0]);

        if (playerInfo == null) {
            MSLogger.severe(sender, Component.translatable("ms.error.player_not_found"));
            return true;
        }

        playerInfo.setBanned(true, date, reason, sender);
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
        IDMap idMap = MSEssentials.getCache().idMap;

        switch (args.length) {
            case 1 -> {
                for (var offlinePlayer : sender.getServer().getOfflinePlayers()) {
                    String nickname = offlinePlayer.getName();
                    UUID uuid = offlinePlayer.getUniqueId();

                    if (StringUtils.isBlank(nickname) || offlinePlayer.isBanned()) continue;

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