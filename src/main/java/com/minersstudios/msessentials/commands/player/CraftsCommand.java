package com.minersstudios.msessentials.commands.player;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.msessentials.menu.CraftsMenu;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MSCommand(
        command = "crafts",
        aliases = {"recipes"},
        usage = " ꀑ §cИспользуй: /<command>",
        description = "Открывает меню с крафтами кастомных предметов/декора/блоков",
        playerOnly = true
)
public class CraftsCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE = LiteralArgumentBuilder.literal("crafts").build();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        CraftsMenu.open(CraftsMenu.Type.MAIN, (Player) sender);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
