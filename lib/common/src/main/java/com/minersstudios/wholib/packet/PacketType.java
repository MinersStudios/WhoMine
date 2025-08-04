package com.minersstudios.wholib.packet;

import com.minersstudios.wholib.key.Key;
import com.minersstudios.wholib.key.ResourceKeyed;
import com.minersstudios.wholib.packet.registry.*;
import com.minersstudios.wholib.key.ResourceKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;

import static com.minersstudios.wholib.packet.PacketBound.*;

/**
 * Represents a packet type used in the Minecraft server networking.
 * <p>
 * It contains information about the packet's bound, its ID, and its name.
 * <p>
 * Available packet types are defined in the following registries:
 * <ul>
 *     <li>{@link HandshakePackets}</li>
 *     <li>{@link PlayPackets}</li>
 *     <li>{@link StatusPackets}</li>
 *     <li>{@link LoginPackets}</li>
 *     <li>{@link ConfigurationPackets}</li>
 * </ul>
 *
 * To create a new packet type, use one of the following methods:
 * <ul>
 *     <li>{@link #ofMC(PacketBound, int, String)}</li>
 *     <li>{@link #of(PacketBound, int, ResourceKey)}</li>
 * </ul>
 *
 * @see PacketBound
 * @see ResourceKey
 */
@SuppressWarnings("unused")
@Immutable
public final class PacketType implements ResourceKeyed {

    private final PacketBound bound;
    private final int id;
    private final ResourceKey resourceKey;

    private PacketType(
            final @NotNull PacketBound bound,
            final int id,
            final @NotNull ResourceKey resourceKey
    ) {
        this.bound = bound;
        this.id = id;
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the bound of the packet
     *
     * @return The bound of the packet
     */
    public @NotNull PacketBound getBound() {
        return this.bound;
    }

    /**
     * Returns the ID of the packet
     *
     * @return The ID of the packet
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns the resource key of the packet
     *
     * @return The resource key of the packet
     */
    @Override
    public @NotNull ResourceKey getResourceKey() {
        return this.resourceKey;
    }

    /**
     * Returns whether the packet is {@link PacketBound#SERVERBOUND serverbound}
     *
     * @return True if the packet is {@link PacketBound#SERVERBOUND serverbound}
     */
    public boolean isServerbound() {
        return this.bound == SERVERBOUND;
    }

    /**
     * Returns whether the packet is {@link PacketBound#CLIENTBOUND clientbound}
     *
     * @return True if the packet is {@link PacketBound#CLIENTBOUND clientbound}
     */
    public boolean isClientbound() {
        return this.bound == CLIENTBOUND;
    }

    /**
     * Returns the hash code value for this packet type
     *
     * @return The hash code value for this packet type
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.bound.hashCode();
        result = prime * result + Integer.hashCode(this.id);
        result = prime * result + this.resourceKey.hashCode();

        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this packet type
     *
     * @param obj The reference object with which to compare
     * @return True if this object is the same as the object argument
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof PacketType that
                        && this.getBound() == that.getBound()
                        && this.getId() == that.getId()
                        && this.getResourceKey().equals(that.getResourceKey())
                );
    }

    /**
     * Returns the string representation of this packet type
     *
     * @return The string representation of this packet type
     */
    @Override
    public @NotNull String toString() {
        return this.bound + "/" + this.resourceKey;
    }

    /**
     * Creates a new packet type with the given bound, ID, minecraft resource,
     * and path
     *
     * @param bound The bound of the packet
     * @param id    The ID of the packet
     * @param key   The minecraft key of the packet
     * @return A new packet type with the given bound, ID, minecraft resource,
     *         and path
     * @see ResourceKey#minecraft(String)
     * @see #of(PacketBound, int, ResourceKey)
     */
    @Contract("_, _, _ -> new")
    public static @NotNull PacketType ofMC(
            final @NotNull PacketBound bound,
            final int id,
            final @Key @NotNull String key
    ) {
        return of(bound, id, ResourceKey.minecraft(key));
    }

    /**
     * Creates a new packet type with the given bound, ID, and resource key
     *
     * @param bound         The bound of the packet
     * @param id            The ID of the packet
     * @param resourceKey   The resource key of the packet
     * @return A new packet type with the given bound, ID, and resource key
     */
    @Contract("_, _, _ -> new")
    public static @NotNull PacketType of(
            final @NotNull PacketBound bound,
            final int id,
            final @NotNull ResourceKey resourceKey
    ) {
        return new PacketType(bound, id, resourceKey);
    }
}
