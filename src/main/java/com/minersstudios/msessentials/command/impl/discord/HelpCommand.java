package com.minersstudios.msessentials.command.impl.discord;

import com.minersstudios.msessentials.command.api.discord.interaction.CommandHandler;
import com.minersstudios.msessentials.command.api.discord.SlashCommand;
import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

@SlashCommand
public final class HelpCommand extends SlashCommandExecutor {

    public HelpCommand() {
        super(
                Commands.slash("help", "Help list")
        );
    }

    @Override
    public void onCommand(final @NotNull CommandHandler handler) {
        // TODO
    }
}
