package com.minersstudios.wholib.packet.collection;

import com.minersstudios.wholib.packet.PacketType;
import com.minersstudios.wholib.key.ResourceKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

final class Key2PacketMapImpl extends PacketMapImpl<ResourceKey> implements Key2PacketMap {

    Key2PacketMapImpl(
            final @NotNull Map<ResourceKey, PacketType> clientMap,
            final @NotNull Map<ResourceKey, PacketType> serverMap
    ) {
        super(clientMap, serverMap);
    }

    public static final class Path2PacketBuilderImpl
            extends BuilderImpl<Key2PacketMap.Builder, Key2PacketMap, ResourceKey>
            implements Key2PacketMap.Builder {

        @Contract("_ -> this")
        @Override
        public @NotNull Key2PacketMap.Builder add(final @NotNull PacketType packetType) {
            return this.add(packetType.getResourceKey(), packetType);
        }

        @Contract("_ -> this")
        @Override
        public @NotNull Key2PacketMap.Builder add(final PacketType @NotNull ... packets) {
            for (final var packet : packets) {
                this.add(packet);
            }

            return this;
        }

        @Contract(" -> new")
        @Override
        public @NotNull Key2PacketMap build() {
            return new Key2PacketMapImpl(
                    this.clientMap,
                    this.serverMap
            );
        }
    }
}
