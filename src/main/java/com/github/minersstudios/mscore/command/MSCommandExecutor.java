package com.github.minersstudios.mscore.command;

import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface MSCommandExecutor extends CommandExecutor, TabCompleter {

    /**
     * @return CommandNode for {@link Commodore} registration
     */
    default @Nullable CommandNode<?> getCommandNode() {
        return null;
    }

    @Override
    default @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        return new ArrayList<>();
    }
}
