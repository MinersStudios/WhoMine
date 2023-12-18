package com.minersstudios.msessentials.discord.command;

import com.minersstudios.mscore.plugin.MSPlugin;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public abstract class SlashCommandExecutor<P extends MSPlugin<P>> {
    private final SlashCommandData data;
    private P plugin;

    public SlashCommandExecutor(final SlashCommandData data) {
        this.data = data;
    }

    public final @NotNull SlashCommandData getData() {
        return this.data;
    }

    public final @UnknownNullability P getPlugin() {
        return this.plugin;
    }

    @SuppressWarnings("unchecked")
    public final void setPlugin(final @NotNull MSPlugin<?> plugin) throws IllegalStateException, IllegalArgumentException {
        if (this.plugin != null) {
            throw new IllegalStateException("Plugin already set");
        }

        if (!plugin.getClass().isAssignableFrom(this.getClass())) {
            throw new IllegalArgumentException("Plugin is not assignable from this class");
        }

        this.plugin = (P) plugin;
    }

    public abstract void onInteract(final @NotNull InteractionHandler handler);
}
