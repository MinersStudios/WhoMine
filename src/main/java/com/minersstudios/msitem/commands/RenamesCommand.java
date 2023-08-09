package com.minersstudios.msitem.commands;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.msitem.menu.RenamesMenu;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@MSCommand(
        command = "renames",
        aliases = {"optifine", "rename", "renameables"},
        usage = " ꀑ §cИспользуй: /<command>",
        description = "Открывает меню с переименованиями предметов",
        playerOnly = true
)
public class RenamesCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE = literal("renames").build();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        RenamesMenu.open((Player) sender);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
