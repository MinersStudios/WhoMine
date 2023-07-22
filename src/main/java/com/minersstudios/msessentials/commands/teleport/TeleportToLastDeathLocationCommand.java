package com.minersstudios.msessentials.commands.teleport;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.tabcompleters.AllLocalPlayers;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "teleporttolastdeathlocation",
        aliases = {
                "teleporttolastdeathloc",
                "tptolastdeathlocation",
                "tptolastdeathloc",
                "tptolastdeath"
        },
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм]",
        description = "Телепортирует игрока на его последнее место смерти",
        permission = "msessentials.teleporttolastdeathlocation",
        permissionDefault = PermissionDefault.OP
)
public class TeleportToLastDeathLocationCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("teleporttolastdeathlocation")
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

        OfflinePlayer offlinePlayer = playerInfo.getOfflinePlayer();
        TranslatableComponent playerNotOnline = Component.translatable("ms.error.player_not_online");

        if (offlinePlayer.getPlayer() == null) {
            MSLogger.warning(sender, playerNotOnline);
            return true;
        }

        Location lastDeathLocation = playerInfo.getPlayerFile().getLastDeathLocation();

        if (lastDeathLocation == null) {
            MSLogger.warning(
                    sender,
                    Component.translatable(
                            "ms.command.teleport_to_last_death.no_position",
                            playerInfo.getGrayIDGoldName(),
                            text(playerInfo.getNickname())
                    )
            );
            return true;
        }

        playerInfo.teleportToLastDeathLocation().thenRun(() ->
                MSLogger.fine(
                        sender,
                        Component.translatable(
                                "ms.command.teleport_to_last_death.sender.message",
                                playerInfo.getGrayIDGreenName(),
                                text(playerInfo.getNickname())
                        )
                )
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        return new AllLocalPlayers().onTabComplete(sender, command, label, args);
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
