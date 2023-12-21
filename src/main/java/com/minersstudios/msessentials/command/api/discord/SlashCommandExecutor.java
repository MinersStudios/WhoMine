package com.minersstudios.msessentials.command.api.discord;

import com.minersstudios.msessentials.MSEssentials;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents an abstract class for handling discord slash commands.
 * <br>
 * All slash commands must be annotated using {@link SlashCommand} and must
 * extend this class.
 *
 * @see SlashCommand
 */
public abstract class SlashCommandExecutor {
    private final SlashCommandData data;
    private MSEssentials plugin;

    /**
     * Slash command executor constructor
     *
     * @param data The slash command data
     */
    public SlashCommandExecutor(final SlashCommandData data) {
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
     * Sets the plugin instance, which handles this slash command
     *
     * @param plugin The plugin instance, which handles this slash command
     * @throws IllegalStateException If the plugin is already set
     */
    public final void setPlugin(final @NotNull MSEssentials plugin) throws IllegalStateException {
        if (this.plugin != null) {
            throw new IllegalStateException("Plugin already set");
        }

        this.plugin = plugin;
    }

    /**
     * Called on slash command interaction
     *
     * @param handler The interaction handler
     */
    public abstract void onInteract(final @NotNull InteractionHandler handler);
}
