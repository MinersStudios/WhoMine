package com.minersstudios.mscore.listener.event;

import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * This class used for extending listeners with the
 * {@link MSListener} annotation
 *
 * @param <P> The plugin, that this listener is registered to
 * @see MSListener
 * @see MSPlugin#registerListeners()
 */
public abstract class AbstractMSListener<P extends MSPlugin<P>> implements Listener {
    private P plugin;

    /**
     * @return The plugin for this listener or null if not set
     * @throws IllegalStateException If this listener is not registered
     * @see #register(MSPlugin)
     * @see MSPlugin#registerListeners()
     */
    public final @NotNull P getPlugin() throws IllegalStateException {
        if (!this.isRegistered()) {
            throw new IllegalStateException("Listener " + this + " not registered!");
        }

        return this.plugin;
    }

    /**
     * @return True if this listener is registered to a plugin
     */
    public final boolean isRegistered() {
        return this.plugin != null && this.plugin.getListeners().contains(this);
    }

    /**
     * Registers this listener to the plugin
     *
     * @param plugin The plugin to register this listener to
     */
    public final void register(@NotNull P plugin) {
        if (this.isRegistered()) {
            throw new IllegalStateException("Listener " + this + " already registered!");
        }

        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * @return A string representation of this listener
     */
    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{plugin=" + this.plugin + '}';
    }
}
