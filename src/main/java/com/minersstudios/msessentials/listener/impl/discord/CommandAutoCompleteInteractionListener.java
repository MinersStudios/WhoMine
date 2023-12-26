package com.minersstudios.msessentials.listener.impl.discord;

import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import com.minersstudios.msessentials.listener.api.discord.AbstractDiscordListener;
import com.minersstudios.msessentials.listener.api.discord.DiscordListener;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;

@DiscordListener
public class CommandAutoCompleteInteractionListener extends AbstractDiscordListener {

    @Override
    public void onCommandAutoCompleteInteraction(final @NotNull CommandAutoCompleteInteractionEvent event) {
        final var commandMap = this.getPlugin().getCache().getDiscordManager().slashCommandMap();
        final SlashCommandExecutor executor = commandMap.get(event.getCommandIdLong());

        if (executor != null) {
            executor.tabComplete(event);
        }
    }
}
