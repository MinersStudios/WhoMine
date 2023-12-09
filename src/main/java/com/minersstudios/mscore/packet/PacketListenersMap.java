package com.minersstudios.mscore.packet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.minersstudios.mscore.listener.packet.AbstractMSPacketListener;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A utility class representing a mapping of packet types to
 * packet listeners in the MSPlugins. This class provides methods
 * to manage and access packet listeners based on their associated
 * packet types.
 */
public final class PacketListenersMap {
    private final Multimap<PacketType, AbstractMSPacketListener<? extends MSPlugin<?>>> receiveWhiteList = HashMultimap.create();
    private final Multimap<PacketType, AbstractMSPacketListener<? extends MSPlugin<?>>> sendWhiteList = HashMultimap.create();

    /**
     * Gets an unmodifiable view of the receive packet type
     * to packet listener mappings
     *
     * @return The unmodifiable multimap containing receive
     *         packet type to packet listener mappings
     * @see PacketType
     * @see AbstractMSPacketListener
     */
    public @NotNull @UnmodifiableView Multimap<PacketType, AbstractMSPacketListener<? extends MSPlugin<?>>> getReceiveWhiteList() {
        return Multimaps.unmodifiableMultimap(this.receiveWhiteList);
    }

    /**
     * Gets an unmodifiable view of the send packet type
     * to packet listener mappings
     *
     * @return The unmodifiable multimap containing send
     *         packet type to packet listener mappings
     * @see PacketType
     * @see AbstractMSPacketListener
     */
    public @NotNull @UnmodifiableView Multimap<PacketType, AbstractMSPacketListener<? extends MSPlugin<?>>> getSendWhiteList() {
        return Multimaps.unmodifiableMultimap(this.sendWhiteList);
    }

    /**
     * Gets an unmodifiable collection of packet listeners
     * associated with the specified packet type
     *
     * @param packetType The packet type for which to retrieve
     *                   the associated packet listeners
     * @return The unmodifiable collection of packet listeners
     *         associated with the given packet type
     * @see AbstractMSPacketListener
     * @see PacketType
     */
    public @NotNull @UnmodifiableView Collection<AbstractMSPacketListener<? extends MSPlugin<?>>> getListeners(final @NotNull PacketType packetType) {
        return packetType.isReceive()
                ? Collections.unmodifiableCollection(this.receiveWhiteList.get(packetType))
                : Collections.unmodifiableCollection(this.sendWhiteList.get(packetType));
    }

    /**
     * Gets an unmodifiable collection of all packet listeners
     * associated with any packet type
     *
     * @return The unmodifiable collection of all packet listeners
     *         associated with any packet type
     * @see AbstractMSPacketListener
     */
    public @NotNull @UnmodifiableView Collection<AbstractMSPacketListener<? extends MSPlugin<?>>> listeners() {
        final var listeners = new HashSet<AbstractMSPacketListener<? extends MSPlugin<?>>>(this.listenersSize());

        listeners.addAll(this.sendWhiteList.values());
        listeners.addAll(this.receiveWhiteList.values());

        return Collections.unmodifiableCollection(listeners);
    }

    /**
     * Gets an unmodifiable collection of all receive packet listeners
     *
     * @return The unmodifiable collection of all receive packet listeners
     * @see AbstractMSPacketListener
     */
    public @NotNull @UnmodifiableView Collection<AbstractMSPacketListener<? extends MSPlugin<?>>> receiveListeners() {
        return Collections.unmodifiableCollection(this.receiveWhiteList.values());
    }

    /**
     * Gets an unmodifiable collection of all send packet listeners
     *
     * @return The unmodifiable collection of all send packet listeners
     * @see AbstractMSPacketListener
     */
    public @NotNull @UnmodifiableView Collection<AbstractMSPacketListener<? extends MSPlugin<?>>> sendListeners() {
        return Collections.unmodifiableCollection(this.sendWhiteList.values());
    }

    /**
     * Gets an unmodifiable set of all distinct packet types associated
     * with any packet listener
     *
     * @return The unmodifiable set of all distinct packet types associated
     *         with any packet listener
     * @see PacketType
     */
    public @NotNull @UnmodifiableView Set<PacketType> packetTypeSet() {
        var packetTypes = new HashSet<PacketType>(this.packetTypesSize());

        packetTypes.addAll(this.receiveWhiteList.keySet());
        packetTypes.addAll(this.sendWhiteList.keySet());

        return Collections.unmodifiableSet(packetTypes);
    }

    /**
     * Gets an unmodifiable set of all receive packet types
     *
     * @return The unmodifiable set of all receive packet types
     * @see PacketType
     */
    public @NotNull @UnmodifiableView Set<PacketType> receivePacketTypeSet() {
        return Collections.unmodifiableSet(this.receiveWhiteList.keySet());
    }

    /**
     * Gets an unmodifiable set of all send packet types
     *
     * @return The unmodifiable set of all send packet types
     * @see PacketType
     */
    public @NotNull @UnmodifiableView Set<PacketType> sendPacketTypeSet() {
        return Collections.unmodifiableSet(this.sendWhiteList.keySet());
    }

    /**
     * Adds a packet listener to the map based on its white lists
     *
     * @param listener The packet listener to add
     * @see AbstractMSPacketListener
     * @see AbstractMSPacketListener#getReceiveWhiteList()
     * @see AbstractMSPacketListener#getSendWhiteList()
     */
    public void addListener(final @NotNull AbstractMSPacketListener<? extends MSPlugin<?>> listener) {
        final var receiveWhiteList = listener.getReceiveWhiteList();
        final var sendWhiteList = listener.getSendWhiteList();

        if (!receiveWhiteList.isEmpty()) {
            for (final var packetType : receiveWhiteList) {
                this.receiveWhiteList.put(packetType, listener);
            }
        }

        if (!sendWhiteList.isEmpty()) {
            for (final var packetType : sendWhiteList) {
                this.sendWhiteList.put(packetType, listener);
            }
        }
    }

    /**
     * Removes a packet listener from the map based on its white lists
     *
     * @param listener The packet listener to remove
     * @see AbstractMSPacketListener
     * @see AbstractMSPacketListener#getReceiveWhiteList()
     * @see AbstractMSPacketListener#getSendWhiteList()
     */
    public void removeListener(final @NotNull AbstractMSPacketListener<? extends MSPlugin<?>> listener) {
        final var receiveWhiteList = listener.getReceiveWhiteList();
        final var sendWhiteList = listener.getSendWhiteList();

        if (!receiveWhiteList.isEmpty()) {
            for (final var packetType : receiveWhiteList) {
                this.receiveWhiteList.remove(packetType, listener);
            }
        }

        if (!sendWhiteList.isEmpty()) {
            for (final var packetType : sendWhiteList) {
                this.sendWhiteList.remove(packetType, listener);
            }
        }
    }

    /**
     * Removes all packet listeners associated with the specified
     * packet type
     *
     * @param packetType The packet type for which to remove
     *                   the associated packet listeners
     * @see PacketType
     */
    public void removePacketType(final @NotNull PacketType packetType) {
        if (packetType.isReceive()) {
            this.receiveWhiteList.removeAll(packetType);
        } else {
            this.sendWhiteList.removeAll(packetType);
        }
    }

    /**
     * Checks if the map contains the specified packet listener
     *
     * @param listener The packet listener to check for
     * @return True if the packet listener is present in the map
     */
    public boolean containsListener(final @NotNull AbstractMSPacketListener<? extends MSPlugin<?>> listener) {
        return this.receiveWhiteList.containsValue(listener)
                || this.sendWhiteList.containsValue(listener);
    }

    /**
     * Checks if the map contains packet listeners associated
     * with the specified packet type
     *
     * @param packetType The packet type to check for
     * @return True if the packet type is present in the map
     */
    public boolean containsPacketType(final @NotNull PacketType packetType) {
        return packetType.isReceive()
                ? this.receiveWhiteList.containsKey(packetType)
                : this.sendWhiteList.containsKey(packetType);
    }

    /**
     * Checks if the map is empty (contains no packet listeners)
     *
     * @return True if the map is empty
     */
    public boolean isEmpty() {
        return this.receiveWhiteList.isEmpty() && this.sendWhiteList.isEmpty();
    }

    /**
     * Clears the map by removing all packet listeners.
     */
    public void clear() {
        this.receiveWhiteList.clear();
        this.sendWhiteList.clear();
    }

    /**
     * Gets the total number of distinct packet types associated
     * with all packet listeners
     *
     * @return The total number of distinct packet types associated
     *         with all packet listeners
     * @see PacketType
     * @see AbstractMSPacketListener
     */
    public int packetTypesSize() {
        return this.receiveWhiteList.size() + this.sendWhiteList.size();
    }

    /**
     * Gets the total number of packet listeners associated
     * with all packet types
     *
     * @return The total number of packet listeners associated
     *         with all packet types
     * @see AbstractMSPacketListener
     * @see PacketType
     */
    public int listenersSize() {
        return this.sendWhiteList.values().size() + this.receiveWhiteList.values().size();
    }
}
