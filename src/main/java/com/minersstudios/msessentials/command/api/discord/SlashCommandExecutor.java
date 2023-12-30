package com.minersstudios.msessentials.command.api.discord;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.command.api.discord.interaction.CommandHandler;
import com.minersstudios.msessentials.command.api.discord.interaction.TabCompleterHandler;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an abstract class for handling discord slash commands.
 * <br>
 * All slash commands must be annotated using {@link SlashCommand} and must
 * extend this class.
 *
 * @see SlashCommand
 */
public abstract class SlashCommandExecutor {
    private final Map<User, Map.Entry<Long, TabCompleterHandler>> userRemainingTabCompletes;
    private final SlashCommandData data;
    private MSEssentials plugin;
    private long id;

    public static final int MAX_TAB_SIZE = 25;
    public static final List<Command.Choice> EMPTY_TAB = Collections.emptyList();

    /**
     * Slash command executor constructor
     *
     * @param data The slash command data
     */
    public SlashCommandExecutor(final SlashCommandData data) {
        this.userRemainingTabCompletes = new ConcurrentHashMap<>();
        this.data = data;
    }

    /**
     * @return The slash command data of this slash command
     */
    public final @NotNull SlashCommandData getData() {
        return this.data;
    }

    /**
     * @return The plugin instance, which handles this slash command
     */
    public final @UnknownNullability MSEssentials getPlugin() {
        return this.plugin;
    }

    /**
     * @return The id of this slash command
     */
    public final long getId() {
        return this.id;
    }

    /**
     * Sets up the slash command
     *
     * @param plugin The plugin instance, which handles this slash command
     * @param id     The id of this slash command
     * @throws IllegalStateException If the command is already set
     */
    @ApiStatus.Internal
    public final void setUp(
            final @NotNull MSEssentials plugin,
            final long id
    ) throws IllegalStateException {
        if (this.plugin != null) {
            throw new IllegalStateException("Plugin already set");
        }

        this.plugin = plugin;
        this.id = id;
    }

    /**
     * Executes the slash command
     *
     * @param event The slash command interaction event
     */
    @ApiStatus.Internal
    public final void execute(final @NotNull SlashCommandInteractionEvent event) {
        this.onCommand(
                new CommandHandler(
                        this.plugin,
                        event.getInteraction()
                )
        );
    }

    /**
     * Handles the tab complete event
     *
     * @param event The tab complete event
     * @throws IllegalStateException If the choices are more than 25
     */
    @ApiStatus.Internal
    public final void tabComplete(final @NotNull CommandAutoCompleteInteractionEvent event) throws IllegalStateException {
        final CommandAutoCompleteInteraction interaction = event.getInteraction();
        final var entry = this.userRemainingTabCompletes.get(interaction.getUser());
        final TabCompleterHandler handler;

        if (
                entry == null
                || entry.getKey() != interaction.getCommandIdLong()
        ) {
            handler = new TabCompleterHandler(this.plugin, interaction);

            this.userRemainingTabCompletes.put(
                    interaction.getUser(),
                    Map.entry(
                            interaction.getCommandIdLong(),
                            handler
                    )
            );
        } else {
            handler = entry.getValue();
        }

        final var choices = this.onTabComplete(handler);
        final int size = choices.size();

        if (size > MAX_TAB_SIZE) {
            throw new IllegalStateException("Choices cannot be more than 25! Handle it yourself!");
        }

        if (size != 0) {
            event.replyChoices(choices).queue();
        }
    }

    /**
     * Called on slash command interaction
     *
     * @param handler The command handler
     */
    protected abstract void onCommand(final @NotNull CommandHandler handler);

    /**
     * Called on tab complete
     * <br>
     * <b>NOTE:</b> The choices cannot be more than 25
     *
     * @param handler The tab completer handler
     * @return The tab complete choices
     */
    protected @NotNull List<Command.Choice> onTabComplete(final @NotNull TabCompleterHandler handler) {
        return EMPTY_TAB;
    }
}
