package com.minersstudios.msessentials.listeners.discord;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.command.InteractionHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandInteractionListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(final @NotNull SlashCommandInteractionEvent event) {
        for (final var executor : MSEssentials.getSlashCommands()) {
            if (executor.getData().getName().equals(event.getName())) {
                executor.onInteract(new InteractionHandler(event));
                break;
            }
        }
    }
}
