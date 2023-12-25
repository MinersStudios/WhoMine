package com.minersstudios.msessentials.listener.impl.discord;

import com.minersstudios.msessentials.command.api.discord.InteractionHandler;
import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import com.minersstudios.msessentials.listener.api.discord.AbstractDiscordListener;
import com.minersstudios.msessentials.listener.api.discord.DiscordListener;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@DiscordListener
public final class SlashCommandInteractionListener extends AbstractDiscordListener {

    @Override
    public void onSlashCommandInteraction(final @NotNull SlashCommandInteractionEvent event) {
        final SlashCommandExecutor executor = this.getPlugin().getCache().getDiscordManager().slashCommandMap().get(event.getName());

        if (executor != null) {
            executor.onInteract(
                    new InteractionHandler(
                            this.getPlugin(),
                            event
                    )
            );
        }
    }
}
