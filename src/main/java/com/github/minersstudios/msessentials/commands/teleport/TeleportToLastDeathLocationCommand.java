package com.github.minersstudios.msessentials.commands.teleport;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.PlayerUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.IDMap;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.PlayerInfoMap;
import com.github.minersstudios.msessentials.tabcompleters.AllLocalPlayers;
import com.github.minersstudios.msessentials.utils.IDUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
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

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length == 0) return false;

        if (IDUtils.matchesIDRegex(args[0])) {
            IDMap idMap = MSEssentials.getConfigCache().idMap;
            OfflinePlayer offlinePlayer = idMap.getPlayerByID(args[0]);

            if (offlinePlayer == null) {
                ChatUtils.sendError(sender, Component.translatable("ms.error.id_not_found"));
                return true;
            }

            teleportToLastDeathLocation(sender, offlinePlayer);
            return true;
        }

        if (args[0].length() > 2) {
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayerByNick(args[0]);

            if (offlinePlayer == null) {
                ChatUtils.sendError(sender, Component.translatable("ms.error.player_not_found"));
                return true;
            }

            teleportToLastDeathLocation(sender, offlinePlayer);
            return true;
        }

        ChatUtils.sendWarning(sender, Component.translatable("ms.error.name_length"));
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

    private static void teleportToLastDeathLocation(
            @NotNull CommandSender sender,
            @NotNull OfflinePlayer offlinePlayer
    ) {
        if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getName() == null) {
            ChatUtils.sendError(sender, Component.translatable("ms.error.player_not_found"));
            return;
        }

        PlayerInfoMap playerInfoMap = MSEssentials.getConfigCache().playerInfoMap;
        PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(offlinePlayer.getUniqueId(), offlinePlayer.getName());
        Location lastDeathLocation = playerInfo.getPlayerFile().getLastDeathLocation();
        TranslatableComponent playerNotOnline = Component.translatable("ms.error.player_not_online");

        if (offlinePlayer.getPlayer() == null) {
            ChatUtils.sendWarning(sender, playerNotOnline);
            return;
        }

        if (lastDeathLocation == null) {
            ChatUtils.sendWarning(
                    sender,
                    Component.translatable(
                            "ms.command.teleport_to_last_death.no_position",
                            playerInfo.getGrayIDGoldName(),
                            text(offlinePlayer.getName())
                    )
            );
            return;
        }

        playerInfo.teleportToLastDeathLocation();
        ChatUtils.sendFine(
                sender,
                Component.translatable(
                        "ms.command.teleport_to_last_death.sender.message",
                        playerInfo.getGrayIDGreenName(),
                        text(offlinePlayer.getName())
                )
        );
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return LiteralArgumentBuilder.literal("teleporttolastdeathlocation")
                .then(RequiredArgumentBuilder.argument("id/никнейм", StringArgumentType.word()))
                .build();
    }
}
