package com.minersstudios.wholib.packet.collection;

import com.minersstudios.wholib.packet.PacketBound;
import com.minersstudios.wholib.packet.PacketType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/**
 * Represents a packet map that maps a key to a packet type.
 * <p>
 * To create a new packet map, use one of the following methods:
 * <ul>
 *     <li>{@link #key2PacketBuilder()} -  Creates a new path to packet map
 *                                          builder</li>
 *     <li>{@link #class2PacketBuilder()} - Creates a new class to packet map
 *                                          builder</li>
 * </ul>
 *
 * @param <K> The key type of the packet map
 *
 * @see Key2PacketMap
 * @see Class2PacketMap
 */
@SuppressWarnings("unused")
public interface PacketMap<K> {

    /**
     * Returns the unmodifiable client delegate packet map
     *
     * @return The unmodifiable client delegate packet map
     */
    @NotNull @Unmodifiable Map<K, PacketType> clientDelegate();

    /**
     * Returns the unmodifiable server delegate packet map
     *
     * @return The unmodifiable server delegate packet map
     */
    @NotNull @Unmodifiable Map<K, PacketType> serverDelegate();

    /**
     * Returns the packet of the specified bound and key
     *
     * @param bound The bound of the packet
     * @param key   The key of the packet
     * @return The packet of the specified bound and key
     */
    @Nullable PacketType get(
            final @NotNull PacketBound bound,
            final @NotNull K key
    );

    /**
     * Returns true if the packet map contains the specified packet
     *
     * @param bound The bound of the packet
     * @param key   The key of the packet
     * @return True if the packet map contains the specified packet
     */
    boolean contains(
            final @NotNull PacketBound bound,
            final @NotNull K key
    );

    /**
     * Returns true if the packet map contains the specified key
     *
     * @param key The key of the packet
     * @return True if the packet map contains the specified key
     */
    boolean contains(final @NotNull K key);

    /**
     * Returns true if the packet map contains the specified packet
     *
     * @param packetType The packet type
     * @return True if the packet map contains the specified packet
     */
    boolean contains(final @NotNull PacketType packetType);

    /**
     * Returns the number of packets in the packet map
     *
     * @return The number of packets in the packet map
     */
    int size();

    /**
     * Returns the number of packets in the packet map of the specified bound
     *
     * @param bound The bound of the packets
     * @return The number of packets in the packet map of the specified bound
     */
    int size(final @NotNull PacketBound bound);

    /**
     * Creates a new key to packet map builder
     *
     * @return A new key to packet map builder
     * @see Key2PacketMap
     */
    @Contract(" -> new")
    static @NotNull Key2PacketMap.Builder key2PacketBuilder() {
        return Key2PacketMap.builder();
    }

    /**
     * Creates a new class to packet map builder
     *
     * @return A new class to packet map builder
     * @see Class2PacketMap
     */
    @Contract(" -> new")
    static @NotNull Class2PacketMap.Builder class2PacketBuilder() {
        return Class2PacketMap.builder();
    }

    /**
     * Represents a packet map builder
     *
     * @param <B> The builder type
     * @param <M> The packet map type
     * @param <K> The key type of the packet map
     */
    interface Builder<B extends Builder<B, M, K>, M extends PacketMap<K>, K> {

        /**
         * Binds the specified key to the packet type
         *
         * @param key        The key of the packet
         * @param packetType The packet type
         * @return This builder for chaining
         */
        @Contract("_, _ -> this")
        @NotNull B add(
                final @NotNull K key,
                final @NotNull PacketType packetType
        );

        /**
         * Builds the packet map
         *
         * @return The built packet map
         */
        @Contract(" -> new")
        @NotNull M build();
    }
}
