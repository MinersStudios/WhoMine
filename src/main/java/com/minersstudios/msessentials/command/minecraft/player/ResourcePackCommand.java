package com.minersstudios.msessentials.command.minecraft.player;

import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.CommandExecutor;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.menu.ResourcePackMenu;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Command(
        command = "resourcepack",
        aliases = {
                "texturepack",
                "rp"
        },
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command>",
        description = "Открывает меню с ресурспаками",
        playerOnly = true
)
public final class ResourcePackCommand extends CommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE = LiteralArgumentBuilder.literal("resourcepack").build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        ResourcePackMenu.open((Player) sender);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}