package com.minersstudios.mscore.listener;

import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * This class used for extending listeners with the {@link MSListener} annotation
 *
 * @see MSListener
 * @see MSPlugin#registerListeners()
 */
public abstract class AbstractMSListener implements Listener {
    private MSPlugin plugin;

    /**
     * @return The plugin for this listener or null if not set
     * @throws IllegalStateException If this listener is not registered
     * @see #register(MSPlugin)
     * @see MSPlugin#registerListeners()
     */
    public @NotNull MSPlugin getPlugin() throws IllegalStateException {
        if (!this.isRegistered()) {
            throw new IllegalStateException("Listener " + this + " not registered!");
        }

        return this.plugin;
    }

    /**
     * @return True if this listener is registered to a plugin
     */
    public boolean isRegistered() {
        return this.plugin != null && this.plugin.getListeners().contains(this);
    }

    /**
     * Registers this listener to the plugin
     *
     * @param plugin The plugin to register this listener to
     */
    public void register(@NotNull MSPlugin plugin) {
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
        return this.getClass().getSimpleName() + "{plugin=" + this.plugin + "}";
    }
}
