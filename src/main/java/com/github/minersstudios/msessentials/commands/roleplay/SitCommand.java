package com.github.minersstudios.msessentials.commands.roleplay;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "sit",
        aliases = {"s"},
        usage = " ꀑ §cИспользуй: /<command> [речь]",
        description = "Сядь на картаны и порви жопу"
)
public class SitCommand implements MSCommandExecutor {

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (!(sender instanceof Player player)) {
            ChatUtils.sendError(sender, Component.translatable("ms.error.only_player_command"));
            return true;
        }

        PlayerInfo playerInfo = PlayerInfo.fromMap(player);

        if (!player.getLocation().subtract(0.0d, 0.2d, 0.0d).getBlock().getType().isSolid()) {
            ChatUtils.sendWarning(player, Component.translatable("ms.command.sit.in_air"));
            return true;
        }

        String messageString = ChatUtils.extractMessage(args, 0);
        Component message = messageString.isEmpty() ? null : text(messageString);
        if (playerInfo.isSitting()) {
            playerInfo.unsetSitting(message);
        } else {
            playerInfo.setSitting(player.getLocation(), message);
        }
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return LiteralArgumentBuilder.literal("sit")
                .then(RequiredArgumentBuilder.argument("речь", StringArgumentType.greedyString()))
                .build();
    }
}
