package com.minersstudios.msessentials.command.discord;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.command.InteractionHandler;
import com.minersstudios.msessentials.discord.command.SlashCommand;
import com.minersstudios.msessentials.discord.command.SlashCommandExecutor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

@SlashCommand
public final class RemoveSkinCommand extends SlashCommandExecutor<MSEssentials> {

    public RemoveSkinCommand() {
        super(
                Commands.slash("removeskin", "Remove skin")
                .addOption(OptionType.INTEGER, "id", "Skin ID", true)
        );
    }

    @Override
    public void onInteract(@NotNull InteractionHandler handler) {
        // TODO
    }
}
