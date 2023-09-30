package com.minersstudios.msessentials.discord.command;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public abstract class SlashCommandExecutor {
    private final SlashCommandData data;

    public SlashCommandExecutor(final SlashCommandData data) {
        this.data = data;
    }

    public @NotNull SlashCommandData getData() {
        return this.data;
    }

    public abstract void onInteract(final @NotNull InteractionHandler handler);
}
