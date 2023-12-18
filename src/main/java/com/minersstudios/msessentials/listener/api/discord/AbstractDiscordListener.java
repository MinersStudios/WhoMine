package com.minersstudios.msessentials.listener.api.discord;

import com.minersstudios.mscore.listener.api.MSListener;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msessentials.MSEssentials;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * This class used for extending discord listeners with the
 * {@link DiscordListener} annotation
 *
 * @param <P> The plugin, that this listener is registered to
 * @see DiscordListener
 */
public abstract class AbstractDiscordListener<P extends MSPlugin<P>> extends ListenerAdapter implements MSListener<P> {
    private P plugin;

    @Override
    public final @NotNull P getPlugin() throws IllegalStateException {
        if (!this.isRegistered()) {
            throw new IllegalStateException("Listener " + this + " not registered!");
        }

        return this.plugin;
    }

    @Override
    public final boolean isRegistered() {
        return this.plugin != null
                && MSEssentials.singleton().getCache().getDiscordHandler().getJda()
                .map(jda -> jda.getRegisteredListeners().contains(this))
                .orElse(false);
    }

    @Override
    public final void register(final @NotNull P plugin) throws IllegalStateException {
        if (this.isRegistered()) {
            throw new IllegalStateException("Listener " + this + " already registered!");
        }

        this.plugin = plugin;

        MSEssentials.singleton().getCache().getDiscordHandler().getJda()
        .ifPresent(jda -> jda.addEventListener(this));
    }

    /**
     * @return A string representation of this listener
     */
    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{plugin=" + this.plugin + '}';
    }
}
