package com.minersstudios.msessentials.commands.discord;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.command.InteractionHandler;
import com.minersstudios.msessentials.discord.command.SlashCommand;
import com.minersstudios.msessentials.discord.command.SlashCommandExecutor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

@SlashCommand
public final class EditSkinCommand extends SlashCommandExecutor<MSEssentials> {

    public EditSkinCommand() {
        super(
                Commands.slash("editskin", "Edit skin")
                .addOption(OptionType.INTEGER, "id", "Skin ID", true)
                .addOption(OptionType.STRING, "name", "Skin Name")
                .addOption(OptionType.STRING, "url", "Skin URL")
                .addOption(OptionType.STRING, "value", "Skin Value")
                .addOption(OptionType.STRING, "signature", "Skin Signature")
        );
    }

    @Override
    public void onInteract(@NotNull InteractionHandler handler) {
        // TODO
    }
}
