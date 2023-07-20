package com.minersstudios.mscore.listener.packet;

import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class used for extending packet
 * listeners with the {@link MSPacketListener} annotation.
 *
 * @see MSPacketListener
 * @see MSPlugin#registerPacketListeners()
 */
public abstract class AbstractMSPacketListener {
    private MSPlugin plugin;
    private final Set<PacketType> whiteList = new HashSet<>();

    private static final String TO_STRING_FORMAT = "%s{plugin=%s, whitelist=%s}";

    /**
     * Packet listener constructor
     *
     * @param first The first packet type to listen to
     * @param other The other packet types to listen to (optional)
     * @see PacketType
     * @see #getWhiteList()
     */
    public AbstractMSPacketListener(
            PacketType first,
            PacketType @NotNull ... other
    ) {
        Set<PacketType> whitelist = Set.of(other);

        this.whiteList.add(first);
        this.whiteList.addAll(whitelist);
    }

    /**
     * @return The plugin for this packet listener or null if not set
     * @throws IllegalStateException If this packet listener is not registered
     * @see #register(MSPlugin)
     * @see MSPlugin#registerPacketListeners()
     */
    public final @NotNull MSPlugin getPlugin() throws IllegalStateException {
        if (!this.isRegistered()) {
            throw new IllegalStateException("Packet listener " + this + " not registered!");
        }

        return this.plugin;
    }

    /**
     * @return The packet types that this listener listens to
     * @see PacketType
     */
    public final @NotNull @Unmodifiable Set<PacketType> getWhiteList() {
        return Set.copyOf(this.whiteList);
    }

    /**
     * @return True if this listener is registered to a plugin
     */
    public final boolean isRegistered() {
        return this.plugin != null && this.plugin.getPacketListeners().contains(this);
    }

    /**
     * Registers this packet listener to the plugin
     *
     * @param plugin The plugin to register this listener to
     */
    public final void register(@NotNull MSPlugin plugin) {
        if (this.isRegistered()) {
            throw new IllegalStateException("Packet listener " + this + " already registered!");
        }

        this.plugin = plugin;
        MSPlugin.getGlobalCache().msPacketListeners.add(this);
    }

    /**
     * Packet receive event handler
     *
     * @param event The packet event
     */
    public void onPacketReceive(@NotNull PacketEvent event) {
        throw new UnsupportedOperationException("Packet receive not implemented for " + event.getPacketContainer().getType().getName());
    }

    /**
     * Packet send event handler
     *
     * @param event The packet event
     */
    public void onPacketSend(@NotNull PacketEvent event) {
        throw new UnsupportedOperationException("Packet send not implemented for " + event.getPacketContainer().getType().getName());
    }

    /**
     * @return The string representation of this packet listener
     */
    @Override
    public @NotNull String toString() {
        return String.format(
                TO_STRING_FORMAT,
                this.getClass().getSimpleName(),
                this.plugin,
                Arrays.toString(this.whiteList.toArray())
        );
    }
}
