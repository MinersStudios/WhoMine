package com.minersstudios.msessentials.command.api.discord.interaction;

import com.minersstudios.msessentials.MSEssentials;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.jetbrains.annotations.NotNull;

public class TabCompleterHandler extends AbstractInteractionHandler<CommandAutoCompleteInteraction> {

    public TabCompleterHandler(
            final @NotNull MSEssentials plugin,
            final @NotNull CommandAutoCompleteInteraction interaction
    ) {
        super(plugin, interaction);
    }

    @Override
    public @NotNull String getCommandName() {
        return this.getInteraction().getName();
    }

    public @NotNull AutoCompleteQuery getCurrentOption() {
        return this.getInteraction().getFocusedOption();
    }
}
