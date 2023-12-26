package com.minersstudios.mscore.command.api;

import com.minersstudios.mscore.plugin.MSPlugin;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collections;
import java.util.List;

/**
 * Represents a class which contains a bunch of methods for handling commands
 */
public abstract class AbstractCommandExecutor<P extends MSPlugin<P>> implements CommandExecutor, TabCompleter {
    private P plugin;

    /**
     * An empty tab completion list. Used when no tab completion is needed or
     * when the tab completion is handled by the command executor itself.
     */
    public static final List<String> EMPTY_TAB = Collections.emptyList();

    protected AbstractCommandExecutor() {
        this.plugin = null;
    }

    /**
     * Returns the plugin instance
     *
     * @return The plugin instance
     */
    public final @UnknownNullability P getPlugin() {
        return this.plugin;
    }

    /**
     * Sets the plugin instance
     *
     * @param plugin The plugin instance
     * @throws IllegalStateException If the plugin is already set
     */
    @SuppressWarnings("unchecked")
    public void setPlugin(final @NotNull MSPlugin<?> plugin) throws IllegalStateException {
        if (this.plugin != null) {
            throw new IllegalStateException("Plugin already set");
        }

        this.plugin = (P) plugin;
    }

    /**
     * Returns the components and its definition for better tab completion
     * <br>
     * <b>Example:</b>
     * <pre>{@code
     * literal("example")
     * .then(
     *       literal("someLiteralArgument1")
     *       .then(argument("some string argument", StringArgumentType.greedyString())))
     * )
     * .then(
     *       literal("someLiteralArgument2")
     *       .then(argument("some integer argument", IntegerArgumentType.integer())))
     * )
     * .build();
     * }</pre>
     *
     * @return CommandNode for {@link Commodore} registration
     */
    public @Nullable CommandNode<?> getCommandNode() {
        return null;
    }

    /**
     * Executes the given command, returning its success. If false is returned,
     * then the "usage" plugin.yml entry for this command (if defined) will be
     * sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return True if a valid command, otherwise false
     */
    @Override
    public abstract boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    );

    /**
     * Requests a list of possible completions for a command argument
     *
     * @param sender  Source of the command. For player tab-completing a command
     *                inside a command block, this will be the player, not the
     *                command block.
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed
     * @return A List of possible completions for the final argument, or null to
     *         default to the command executor
     */
    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return EMPTY_TAB;
    }
}
