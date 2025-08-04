package com.minersstudios.wholib.packet.collection;

import com.minersstudios.wholib.packet.PacketType;
import com.minersstudios.wholib.key.ResourceKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a map of {@link ResourceKey resource keys} to
 * {@link PacketType packet types}.
 * <p>
 * This map is immutable and cannot be modified after creation.
 * <p>
 * To create a new instance of this map, use the following method:
 * {@link #builder()}
 *
 * @see PacketMap
 */
@Immutable
public interface Key2PacketMap extends PacketMap<ResourceKey> {

    /**
     * Creates a new path to packet map builder
     *
     * @return A new path to packet map builder
     */
    @Contract(" -> new")
    static @NotNull Builder builder() {
        return new Key2PacketMapImpl.Path2PacketBuilderImpl();
    }

    /**
     * Represents a builder for a {@link Key2PacketMap path to packet map}.
     * <p>
     * To create a new instance of this builder, use the following method:
     * {@link Key2PacketMap#builder()}
     *
     * @see PacketMap.Builder
     */
    interface Builder extends PacketMap.Builder<Builder, Key2PacketMap, ResourceKey> {

        /**
         * Adds a packet to the map
         *
         * @param packet The packet to add
         * @return This builder for chaining
         * @see #add(PacketType...)
         * @see PacketMap.Builder#add(Object, PacketType)
         */
        @Contract("_ -> this")
        @NotNull Builder add(final @NotNull PacketType packet);

        /**
         * Adds a list of packets to the map
         *
         * @param packets The packets to add
         * @return This builder for chaining
         * @see #add(PacketType)
         */
        @Contract("_ -> this")
        @NotNull Builder add(final PacketType @NotNull ... packets);
    }
}
