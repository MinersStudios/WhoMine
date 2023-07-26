package com.minersstudios.msessentials.commands.admin.ban;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.config.LanguageFile;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.utils.ChatUtils;
import com.minersstudios.mscore.utils.DateUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
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
import static net.kyori.adventure.text.Component.translatable;

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
                    argument("id/nickname", StringArgumentType.word())
                    .then(
                            argument("time", StringArgumentType.word())
                            .then(argument("reason", StringArgumentType.greedyString()))
                    )
            ).build();

    private static final TranslatableComponent WRONG_FORMAT = translatable("ms.error.wrong_format");
    private static final TranslatableComponent PLAYER_NOT_FOUND = translatable("ms.error.player_not_found");

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
            MSLogger.severe(sender, WRONG_FORMAT);
            return true;
        }

        String reason = args.length > 2
                ? ChatUtils.extractMessage(args, 2)
                : LanguageFile.renderTranslation("ms.command.ban.default_reason");

        PlayerInfo playerInfo = PlayerInfo.fromString(args[0]);

        if (playerInfo == null) {
            MSLogger.severe(sender, PLAYER_NOT_FOUND);
            return true;
        }

        playerInfo.setBanned(true, date, reason, sender);
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
                List<String> completions = new ArrayList<>();

                for (var offlinePlayer : sender.getServer().getOfflinePlayers()) {
                    String nickname = offlinePlayer.getName();
                    UUID uuid = offlinePlayer.getUniqueId();

                    if (
                            StringUtils.isBlank(nickname)
                            || offlinePlayer.isBanned()
                    ) continue;

                    int id = MSEssentials.getCache().idMap.getID(uuid, false, false);

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