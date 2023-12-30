package com.minersstudios.mscore.listener.api.event;

import com.minersstudios.mscore.listener.api.MSListener;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This class used for extending event listeners with the {@link EventListener}
 * annotation
 *
 * @param <P> The plugin, that this event listener is registered to
 * @see EventListener
 */
public abstract class AbstractEventListener<P extends MSPlugin<P>> implements MSListener<P>, Listener {
    private P plugin;

    @Override
    public final @NotNull P getPlugin() throws IllegalStateException {
        if (!this.isRegistered()) {
            throw new IllegalStateException("Event listener " + this + " not registered!");
        }

        return this.plugin;
    }

    @Override
    public final boolean isRegistered() {
        return this.plugin != null
                && this.plugin.getEventListeners().contains(this);
    }

    @ApiStatus.Internal
    @Override
    public final void register(final @NotNull P plugin) throws IllegalStateException {
        if (this.isRegistered()) {
            throw new IllegalStateException("Event listener " + this + " already registered!");
        }

        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{plugin=" + this.plugin + '}';
    }
}
