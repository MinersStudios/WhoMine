package com.minersstudios.mscore.listener.api.event;

import com.minersstudios.mscore.listener.api.MSListener;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * This class used for extending listeners with the
 * {@link EventListener} annotation
 *
 * @param <P> The plugin, that this listener is registered to
 * @see EventListener
 * @see MSPlugin#registerListeners()
 */
public abstract class AbstractEventListener<P extends MSPlugin<P>> implements MSListener<P>, Listener {
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
        return this.plugin != null && this.plugin.getListeners().contains(this);
    }

    @Override
    public final void register(final @NotNull P plugin) throws IllegalStateException {
        if (this.isRegistered()) {
            throw new IllegalStateException("Listener " + this + " already registered!");
        }

        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{plugin=" + this.plugin + '}';
    }
}