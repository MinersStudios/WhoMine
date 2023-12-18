package com.minersstudios.msessentials.command.discord;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.command.InteractionHandler;
import com.minersstudios.msessentials.discord.command.SlashCommand;
import com.minersstudios.msessentials.discord.command.SlashCommandExecutor;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

@SlashCommand
public final class HelpCommand extends SlashCommandExecutor<MSEssentials> {

    public HelpCommand() {
        super(
                Commands.slash("help", "Help list")
        );
    }

    @Override
    public void onInteract(@NotNull InteractionHandler handler) {
        // TODO
    }
}