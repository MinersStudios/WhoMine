package com.minersstudios.msessentials.commands.player.roleplay;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "sit",
        aliases = {"s"},
        usage = " ꀑ §cИспользуй: /<command> [речь]",
        description = "Сядь на картаны и порви жопу",
        playerOnly = true
)
public class SitCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("sit")
            .then(argument("speech", StringArgumentType.greedyString()))
            .build();

    private static final TranslatableComponent MUTED = translatable("ms.command.mute.already.receiver");
    private static final TranslatableComponent IN_AIR = translatable("ms.command.sit.in_air");

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        final Player player = (Player) sender;
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        if (!player.getLocation().subtract(0.0d, 0.2d, 0.0d).getBlock().getType().isSolid()) {
            MSLogger.warning(player, IN_AIR);
            return true;
        }

        final String messageString = ChatUtils.extractMessage(args, 0);
        final Component message = messageString.isEmpty() ? null : text(messageString);

        if (
                message != null
                && playerInfo.isMuted()
        ) {
            MSLogger.warning(player, MUTED);
            return true;
        }

        if (playerInfo.isSitting()) {
            playerInfo.unsetSitting(message);
        } else {
            playerInfo.setSitting(player.getLocation(), message);
        }
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
