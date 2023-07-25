package com.minersstudios.mscore.packet.collection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.minersstudios.mscore.listener.packet.AbstractMSPacketListener;
import com.minersstudios.mscore.packet.PacketType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * A utility class representing a mapping of packet types to
 * packet listeners in the MSPlugins. This class provides methods
 * to manage and access packet listeners based on their associated
 * packet types.
 */
public class PacketListenersMap {
    private final Multimap<PacketType, AbstractMSPacketListener> receiveWhiteList = HashMultimap.create();
    private final Multimap<PacketType, AbstractMSPacketListener> sendWhiteList = HashMultimap.create();

    /**
     * Gets an unmodifiable view of the receive packet type
     * to packet listener mappings
     *
     * @return The unmodifiable multimap containing receive
     *         packet type to packet listener mappings
     * @see PacketType
     * @see AbstractMSPacketListener
     */
    public @NotNull @Unmodifiable Multimap<PacketType, AbstractMSPacketListener> getReceiveWhiteList() {
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
    public @NotNull @Unmodifiable Multimap<PacketType, AbstractMSPacketListener> getSendWhiteList() {
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
    public @NotNull @Unmodifiable Collection<AbstractMSPacketListener> getListeners(@NotNull PacketType packetType) {
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
    public @NotNull @Unmodifiable Collection<AbstractMSPacketListener> listeners() {
        var listeners = new HashSet<AbstractMSPacketListener>();

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
    public @NotNull @Unmodifiable Collection<AbstractMSPacketListener> receiveListeners() {
        return Collections.unmodifiableCollection(this.receiveWhiteList.values());
    }

    /**
     * Gets an unmodifiable collection of all send packet listeners
     *
     * @return The unmodifiable collection of all send packet listeners
     * @see AbstractMSPacketListener
     */
    public @NotNull @Unmodifiable Collection<AbstractMSPacketListener> sendListeners() {
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
    public @NotNull @Unmodifiable Set<PacketType> packetTypeSet() {
        var packetTypes = new HashSet<PacketType>();

        packetTypes.addAll(this.sendWhiteList.keySet());
        packetTypes.addAll(this.receiveWhiteList.keySet());

        return Set.copyOf(packetTypes);
    }

    /**
     * Gets an unmodifiable set of all receive packet types
     *
     * @return The unmodifiable set of all receive packet types
     * @see PacketType
     */
    public @NotNull @Unmodifiable Set<PacketType> receivePacketTypeSet() {
        return Set.copyOf(this.receiveWhiteList.keySet());
    }

    /**
     * Gets an unmodifiable set of all send packet types
     *
     * @return The unmodifiable set of all send packet types
     * @see PacketType
     */
    public @NotNull @Unmodifiable Set<PacketType> sendPacketTypeSet() {
        return Set.copyOf(this.sendWhiteList.keySet());
    }

    /**
     * Adds a packet listener to the map based on its white lists
     *
     * @param listener The packet listener to add
     * @see AbstractMSPacketListener
     * @see AbstractMSPacketListener#getReceiveWhiteList()
     * @see AbstractMSPacketListener#getSendWhiteList()
     */
    public void addListener(@NotNull AbstractMSPacketListener listener) {
        var receiveWhiteList = listener.getReceiveWhiteList();
        var sendWhiteList = listener.getSendWhiteList();

        if (!receiveWhiteList.isEmpty()) {
            receiveWhiteList.forEach(packetType -> this.receiveWhiteList.put(packetType, listener));
        }

        if (!sendWhiteList.isEmpty()) {
            sendWhiteList.forEach(packetType -> this.sendWhiteList.put(packetType, listener));
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
    public void removeListener(@NotNull AbstractMSPacketListener listener) {
        var receiveWhiteList = listener.getReceiveWhiteList();
        var sendWhiteList = listener.getSendWhiteList();

        if (!receiveWhiteList.isEmpty()) {
            receiveWhiteList.forEach(packetType -> this.receiveWhiteList.remove(packetType, listener));
        }

        if (!sendWhiteList.isEmpty()) {
            sendWhiteList.forEach(packetType -> this.sendWhiteList.remove(packetType, listener));
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
    public void removePacketType(@NotNull PacketType packetType) {
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
    public boolean containsListener(@NotNull AbstractMSPacketListener listener) {
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
    public boolean containsPacketType(@NotNull PacketType packetType) {
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
        return this.listeners().size();
    }
}
