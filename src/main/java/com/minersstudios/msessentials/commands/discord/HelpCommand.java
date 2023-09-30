package com.minersstudios.msessentials.commands.discord;

import com.minersstudios.msessentials.discord.command.InteractionHandler;
import com.minersstudios.msessentials.discord.command.SlashCommand;
import com.minersstudios.msessentials.discord.command.SlashCommandExecutor;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

@SlashCommand
public class HelpCommand extends SlashCommandExecutor {

    public HelpCommand() {
        super(
                Commands.slash("help", "Help list")
        );
    }

    @Override
    public void onInteract(@NotNull InteractionHandler handler) {

    }
}
