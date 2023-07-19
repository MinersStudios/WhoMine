package com.minersstudios.msessentials.commands.other;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.menu.ResourcePackMenu;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MSCommand(
        command = "resourcepack",
        aliases = {
                "texturepack",
                "rp"
        },
        usage = " ꀑ §cИспользуй: /<command>",
        description = "Открывает меню с ресурспаками"
)
public class ResourcePackCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE = LiteralArgumentBuilder.literal("resourcepack").build();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (!(sender instanceof Player player)) {
            MSLogger.severe(sender, Component.translatable("ms.error.only_player_command"));
            return true;
        }

        ResourcePackMenu.open(player);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
