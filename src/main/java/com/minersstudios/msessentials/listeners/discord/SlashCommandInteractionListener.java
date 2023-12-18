package com.minersstudios.msessentials.listeners.discord;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.command.InteractionHandler;
import com.minersstudios.msessentials.discord.listener.AbstractMSDiscordListener;
import com.minersstudios.msessentials.discord.listener.MSDiscordListener;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@MSDiscordListener
public final class SlashCommandInteractionListener extends AbstractMSDiscordListener<MSEssentials> {

    @Override
    public void onSlashCommandInteraction(final @NotNull SlashCommandInteractionEvent event) {
        this.getPlugin().getCache().getDiscordHandler().slashCommandMap()
        .get(event.getName())
        .onInteract(
                new InteractionHandler(
                        this.getPlugin(),
                        event
                )
        );
    }
}
