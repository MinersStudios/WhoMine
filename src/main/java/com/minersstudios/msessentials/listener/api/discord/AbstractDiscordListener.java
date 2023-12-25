package com.minersstudios.msessentials.listener.api.discord;

import com.minersstudios.mscore.listener.api.MSListener;
import com.minersstudios.msessentials.MSEssentials;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * This class used for extending discord listeners with the
 * {@link DiscordListener} annotation
 *
 * @see DiscordListener
 */
public abstract class AbstractDiscordListener extends ListenerAdapter implements MSListener<MSEssentials> {
    private MSEssentials plugin;

    @Override
    public final @NotNull MSEssentials getPlugin() throws IllegalStateException {
        if (!this.isRegistered()) {
            throw new IllegalStateException("Discord listener " + this + " not registered!");
        }

        return this.plugin;
    }

    @Override
    public final boolean isRegistered() {
        return this.plugin != null
                && MSEssentials.singleton().getCache().getDiscordManager().getJda()
                .map(jda -> jda.getRegisteredListeners().contains(this))
                .orElse(false);
    }

    @Override
    public final void register(final @NotNull MSEssentials plugin) throws IllegalStateException {
        if (this.isRegistered()) {
            throw new IllegalStateException("Discord listener " + this + " already registered!");
        }

        this.plugin = plugin;

        plugin.getCache().getDiscordManager().getJda()
        .ifPresent(
                jda -> jda.addEventListener(this)
        );
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{plugin=" + this.plugin + '}';
    }
}
