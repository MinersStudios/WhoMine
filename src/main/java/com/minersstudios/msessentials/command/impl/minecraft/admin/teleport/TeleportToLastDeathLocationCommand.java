package com.minersstudios.msessentials.command.impl.minecraft.admin.teleport;

import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.CommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.utility.MSPlayerUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@Command(
        command = "teleporttolastdeathlocation",
        aliases = {
                "teleporttolastdeathloc",
                "tptolastdeathlocation",
                "tptolastdeathloc",
                "tptolastdeath"
        },
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [id/никнейм]",
        description = "Телепортирует игрока на его последнее место смерти",
        permission = "msessentials.teleporttolastdeathlocation",
        permissionDefault = PermissionDefault.OP
)
public final class TeleportToLastDeathLocationCommand extends CommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("teleporttolastdeathlocation")
            .then(argument("id/никнейм", StringArgumentType.word()))
            .build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
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
                    ERROR_PLAYER_NOT_FOUND
            );
            return true;
        }

        if (playerInfo.getOnlinePlayer() == null) {
            MSLogger.warning(
                    sender,
                    ERROR_PLAYER_NOT_ONLINE
            );
            return true;
        }

        playerInfo.teleportToLastDeathLocation().thenRun(() ->
                MSLogger.fine(
                        sender,
                        COMMAND_TELEPORT_TO_LAST_DEATH_SENDER_MESSAGE
                        .args(
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname())
                        )
                )
        );

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return args.length == 1
                ? MSPlayerUtils.getLocalPlayerNames(this.getPlugin())
                : EMPTY_TAB;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
