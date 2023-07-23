package com.minersstudios.mscore.command;

import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Represents a class which contains a bunch of methods for handling commands
 */
public interface MSCommandExecutor extends CommandExecutor, TabCompleter {
    List<String> EMPTY_TAB = Collections.emptyList();

    /**
     * Executes the given command, returning its success.
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return True if a valid command, otherwise false
     */
    boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    );

    /**
     * Returns the components and its definition for better tab completion
     * <br>
     * <b>Example:</b>
     * <pre>
     *     literal("example")
     *     .then(
     *           literal("someLiteralArgument1")
     *           .then(argument("some string argument", StringArgumentType.greedyString())))
     *     )
     *     .then(
     *           literal("someLiteralArgument2")
     *           .then(argument("some integer argument", IntegerArgumentType.integer())))
     *     )
     *     .build();
     * </pre>
     *
     * @return CommandNode for {@link Commodore} registration
     */
    default @Nullable CommandNode<?> getCommandNode() {
        return null;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.
     *                For players tab-completing a command inside a command block,
     *                this will be the player, not the command block.
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    The arguments passed to the command,
     *                including final partial argument to be completed
     * @return A List of possible completions for the final argument, or null to default to the command executor
     */
    @Override
    default @NotNull List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        return EMPTY_TAB;
    }
}
