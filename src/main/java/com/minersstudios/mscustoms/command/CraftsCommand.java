package com.minersstudios.mscustoms.command;

import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.command.api.MSCommand;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.menu.CraftsMenu;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MSCommand(
        command = "crafts",
        aliases = "recipes",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command>",
        description = "Открывает меню с крафтами кастомных предметов/декора/блоков",
        playerOnly = true
)
public final class CraftsCommand extends AbstractCommandExecutor<MSCustoms> {
    private static final CommandNode<?> COMMAND_NODE = LiteralArgumentBuilder.literal("crafts").build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        CraftsMenu.open(
                CraftsMenu.Type.MAIN,
                (Player) sender
        );

        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
