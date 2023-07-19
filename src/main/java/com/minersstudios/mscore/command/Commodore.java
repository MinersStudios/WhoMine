package com.minersstudios.mscore.command;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

/**
 * API for registering commands with Mojang's Brigadier command system
 */
public final class Commodore {
    public final Set<Command> commands = new HashSet<>();
    private final String pluginName;

    private static final Field CHILDREN_FIELD;
    private static final Field LITERALS_FIELD;
    private static final Field ARGUMENTS_FIELD;
    private static final Field CUSTOM_SUGGESTIONS_FIELD;
    private static final Field COMMAND_EXECUTE_FUNCTION_FIELD;

    private static final com.mojang.brigadier.Command<?> COMMAND;
    private static final SuggestionProvider<?> SUGGESTION_PROVIDER;

    static {
        try {
            CHILDREN_FIELD = CommandNode.class.getDeclaredField("children");
            LITERALS_FIELD = CommandNode.class.getDeclaredField("literals");
            ARGUMENTS_FIELD = CommandNode.class.getDeclaredField("arguments");
            CUSTOM_SUGGESTIONS_FIELD = ArgumentCommandNode.class.getDeclaredField("customSuggestions");
            COMMAND_EXECUTE_FUNCTION_FIELD = CommandNode.class.getDeclaredField("command");

            CHILDREN_FIELD.setAccessible(true);
            LITERALS_FIELD.setAccessible(true);
            ARGUMENTS_FIELD.setAccessible(true);
            CUSTOM_SUGGESTIONS_FIELD.setAccessible(true);
            COMMAND_EXECUTE_FUNCTION_FIELD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }

        COMMAND = (context) -> {
            throw new UnsupportedOperationException();
        };
        SUGGESTION_PROVIDER = (context, builder) -> {
            throw new UnsupportedOperationException();
        };
    }

    /**
     * API for registering commands with Mojang's Brigadier command system
     *
     * @param plugin Plugin to register Commodore for
     */
    public Commodore(@NotNull Plugin plugin) {
        this.pluginName = plugin.getName().toLowerCase().trim();

        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @SuppressWarnings({"UnstableApiUsage"})
            @EventHandler
            public void onPlayerSendCommandsEvent(@NotNull AsyncPlayerSendCommandsEvent<?> event) {
                if (event.isAsynchronous() || !event.hasFiredAsync()) {
                    Player player = event.getPlayer();
                    var commandNode = event.getCommandNode();

                    Commodore.this.commands.forEach(command -> command.apply(player, commandNode));
                }
            }
        }, plugin);
    }

    /**
     * Registers a command with Commodore
     *
     * @param command        Command to register
     * @param node           Command node to register
     * @param permissionTest Permission test to apply to the command
     */
    @SuppressWarnings("unchecked")
    public void register(
            @NotNull PluginCommand command,
            @NotNull LiteralCommandNode<?> node,
            @NotNull Predicate<? super CommandSender> permissionTest
    ) {
        setFields(node, SUGGESTION_PROVIDER);

        var aliases = this.getAliases(command);

        if (!aliases.contains(node.getLiteral())) {
            node = renameLiteralNode(node, command.getName());
        }

        for (var alias : aliases) {
            var targetNode = node.getLiteral().equals(alias)
                    ? node
                    : literal(alias)
                    .redirect((CommandNode<Object>) node)
                    .build();

            this.commands.add(new Command(targetNode, permissionTest));
        }
    }

    /**
     * Gets the default aliases of a command, and with the plugin name prepended
     * <br>
     * Example: /command -> /command, /plugin:command
     *
     * @param command Command to get aliases for
     * @return Aliases of the command
     */
    private @NotNull List<String> getAliases(@NotNull PluginCommand command) {
        return Stream.concat(Stream.of(command.getLabel()), command.getAliases().stream())
                .flatMap(alias -> Stream.of(alias, this.pluginName + ":" + alias))
                .distinct()
                .toList();
    }

    /**
     * Removes a child command from a root node
     *
     * @param root Root node
     * @param name Name of the child
     */
    private static void removeChild(
            @NotNull RootCommandNode<?> root,
            @NotNull String name
    ) {
        try {
            ((Map<?, ?>) CHILDREN_FIELD.get(root)).remove(name);
            ((Map<?, ?>) LITERALS_FIELD.get(root)).remove(name);
            ((Map<?, ?>) ARGUMENTS_FIELD.get(root)).remove(name);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to remove child", e);
        }
    }

    /**
     * Sets the command fields for a command node and its children.
     * Also sets the suggestion provider for {@link ArgumentCommandNode}
     *
     * @param node               Command node
     * @param suggestionProvider Suggestion provider
     */
    private static void setFields(
            @NotNull CommandNode<?> node,
            @Nullable SuggestionProvider<?> suggestionProvider
    ) {
        try {
            COMMAND_EXECUTE_FUNCTION_FIELD.set(node, COMMAND);

            if (
                    suggestionProvider != null
                    && node instanceof ArgumentCommandNode<?, ?> argumentNode
            ) {
                CUSTOM_SUGGESTIONS_FIELD.set(argumentNode, suggestionProvider);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set fields", e);
        }

        for (var child : node.getChildren()) {
            setFields(child, suggestionProvider);
        }
    }

    /**
     * Renames a literal command node
     *
     * @param node    Command node to rename
     * @param literal New literal for the command node
     * @return Command node with the new literal
     */
    private static <S> @NotNull LiteralCommandNode<S> renameLiteralNode(
            @NotNull LiteralCommandNode<S> node,
            @NotNull String literal
    ) {
        var clone = new LiteralCommandNode<>(
                literal,
                node.getCommand(),
                node.getRequirement(),
                node.getRedirect(),
                node.getRedirectModifier(),
                node.isFork()
        );

        for (var child : node.getChildren()) {
            clone.addChild(child);
        }

        return clone;
    }

    public record Command(
            @NotNull CommandNode<?> node,
            @NotNull Predicate<? super CommandSender> permissionTest
    ) {

        /**
         * Applies the command to a sender
         *
         * @param sender Sender to apply the command to
         * @param root   Root node to apply the command to
         */
        @SuppressWarnings({"unchecked"})
        public <S> void apply(
                @NotNull CommandSender sender,
                @NotNull RootCommandNode<S> root
        ) {
            if (!this.permissionTest.test(sender)) return;
            removeChild(root, this.node.getName());
            root.addChild((CommandNode<S>) this.node);
        }
    }
}
