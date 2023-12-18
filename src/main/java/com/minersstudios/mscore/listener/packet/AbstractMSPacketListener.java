package com.minersstudios.mscore.listener.packet;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class used for extending packet
 * listeners with the {@link MSPacketListener} annotation.
 *
 * @param <P> The plugin, that this packet listener is registered to
 * @see MSPacketListener
 * @see MSPlugin#registerPacketListeners()
 */
public abstract class AbstractMSPacketListener<P extends MSPlugin<P>> implements MSListener<P> {
    private P plugin;
    private final Set<PacketType> sendWhiteList;
    private final Set<PacketType> receiveWhiteList;

    /**
     * Packet listener constructor
     *
     * @param first The first packet type to listen to
     * @param rest  The other packet types to listen to (optional)
     * @see PacketType
     */
    public AbstractMSPacketListener(
            final @NotNull PacketType first,
            final PacketType @NotNull ... rest
    ) {
        this.sendWhiteList = new HashSet<>();
        this.receiveWhiteList = new HashSet<>();
        final PacketType[] whitelist = new PacketType[rest.length + 1];

        System.arraycopy(rest, 0, whitelist, 0, rest.length);
        whitelist[rest.length] = first;

        for (final var packetType : whitelist) {
            if (packetType.isReceive()) {
                this.receiveWhiteList.add(packetType);
            } else {
                this.sendWhiteList.add(packetType);
            }
        }
    }

    @Override
    public final @NotNull P getPlugin() throws IllegalStateException {
        if (this.plugin == null) {
            throw new IllegalStateException("Packet listener " + this + " not registered!");
        }

        return this.plugin;
    }

    /**
     * @return Types of received packets listened to by this listener
     * @see PacketType
     */
    public final @NotNull @UnmodifiableView Set<PacketType> getReceiveWhiteList() {
        return Collections.unmodifiableSet(this.receiveWhiteList);
    }

    /**
     * @return Types of sent packets listened to by this listener
     * @see PacketType
     */
    public final @NotNull @UnmodifiableView Set<PacketType> getSendWhiteList() {
        return Collections.unmodifiableSet(this.sendWhiteList);
    }

    @Override
    public final boolean isRegistered() {
        return this.plugin != null
                && this.plugin.getPacketListeners().contains(this);
    }

    @Override
    public final void register(final @NotNull P plugin) throws IllegalStateException {
        if (this.isRegistered()) {
            throw new IllegalStateException("Packet listener " + this + " already registered!");
        }

        this.plugin = plugin;

        MSPlugin.globalCache().packetListenerMap.addListener(this);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() +
                "plugin=" + this.plugin +
                ", sendWhiteList=" + this.sendWhiteList +
                ", receiveWhiteList=" + this.receiveWhiteList +
                '}';
    }

    /**
     * Packet receive event handler
     *
     * @param event The packet event
     */
    public void onPacketReceive(final @NotNull PacketEvent event) {
        throw new UnsupportedOperationException("Packet receive not implemented for " + event.getPacketContainer().getType().getName());
    }

    /**
     * Packet send event handler
     *
     * @param event The packet event
     */
    public void onPacketSend(final @NotNull PacketEvent event) {
        throw new UnsupportedOperationException("Packet send not implemented for " + event.getPacketContainer().getType().getName());
    }
}
