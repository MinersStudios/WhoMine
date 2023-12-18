package com.minersstudios.msessentials.commands.minecraft.admin.teleport;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.util.MSPlayerUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

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
public final class TeleportToLastDeathLocationCommand extends MSCommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("teleporttolastdeathlocation")
            .then(argument("id/никнейм", StringArgumentType.word()))
            .build();

    private static final TranslatableComponent PLAYER_NOT_FOUND = translatable("ms.error.player_not_found");
    private static final TranslatableComponent PLAYER_NOT_ONLINE = translatable("ms.error.player_not_online");
    private static final TranslatableComponent TELEPORT_TO_LAST_DEATH_LOCATION = translatable("ms.command.teleport_to_last_death.sender.message");


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
            MSLogger.severe(sender, PLAYER_NOT_FOUND);
            return true;
        }

        if (playerInfo.getOnlinePlayer() == null) {
            MSLogger.warning(sender, PLAYER_NOT_ONLINE);
            return true;
        }

        playerInfo.teleportToLastDeathLocation().thenRun(() ->
                MSLogger.fine(
                        sender,
                        TELEPORT_TO_LAST_DEATH_LOCATION.args(
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
            final @NotNull Command command,
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
