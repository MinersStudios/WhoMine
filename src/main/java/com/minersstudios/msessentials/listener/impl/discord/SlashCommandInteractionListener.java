package com.minersstudios.msessentials.listener.impl.discord;

import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import com.minersstudios.msessentials.listener.api.discord.AbstractDiscordListener;
import com.minersstudios.msessentials.listener.api.discord.DiscordListener;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@DiscordListener
public final class SlashCommandInteractionListener extends AbstractDiscordListener {

    @Override
    public void onSlashCommandInteraction(final @NotNull SlashCommandInteractionEvent event) {
        final var commandMap = this.getPlugin().getCache().getDiscordManager().slashCommandMap();
        final SlashCommandExecutor executor = commandMap.get(event.getCommandIdLong());

        if (executor != null) {
            executor.execute(event);
        }
    }
}
