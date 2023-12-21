package com.minersstudios.msessentials.command.impl.discord;

import com.minersstudios.msessentials.command.api.discord.InteractionHandler;
import com.minersstudios.msessentials.command.api.discord.SlashCommand;
import com.minersstudios.msessentials.command.api.discord.SlashCommandExecutor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

@SlashCommand
public final class RemoveSkinCommand extends SlashCommandExecutor {

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
