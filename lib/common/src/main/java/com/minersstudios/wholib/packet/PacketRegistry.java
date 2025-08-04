package com.minersstudios.wholib.packet;

import com.minersstudios.wholib.packet.collection.Class2PacketMap;
import com.minersstudios.wholib.packet.collection.PacketMap;
import com.minersstudios.wholib.packet.collection.Key2PacketMap;
import com.minersstudios.wholib.packet.registry.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a packet registry, which contains the packet maps.
 * <p>
 * The packet registry contains two packet maps:
 * <ul>
 *     <li>The path packet map, which maps the packet's path to the packet
 *         type</li>
 *     <li>The class packet map, which maps the packet's class to the packet
 *         type</li>
 * </ul>
 * <p>
 * <table>
 *     <caption>Available factory methods</caption>
 *     <tr>
 *         <th>Method</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>{@link #create(Key2PacketMap)}</td>
 *         <td>Creates a new packet registry with the given path packet map
 *             and an empty mutable class packet map</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #create(Key2PacketMap, Class2PacketMap)}</td>
 *         <td>Creates a new packet registry with the given packet maps, the
 *             class packet map will be a mutable map</td>
 *     </tr>
 * </table>
 * <p>
 * Next packet type registries are available by default:
 * <ul>
 *     <li>{@link HandshakePackets}</li>
 *     <li>{@link PlayPackets}</li>
 *     <li>{@link StatusPackets}</li>
 *     <li>{@link LoginPackets}</li>
 *     <li>{@link ConfigurationPackets}</li>
 * </ul>
 *
 * @see Key2PacketMap
 * @see Class2PacketMap
 */
@SuppressWarnings("unused")
public final class PacketRegistry {

    private final Key2PacketMap byPath;
    private final Class2PacketMap byClass;

    private PacketRegistry(
            final @NotNull Key2PacketMap byPath,
            final @NotNull Class2PacketMap byClass
    ) {
        this.byPath = byPath;
        this.byClass = byClass;
    }

    /**
     * Returns the packet map, which maps the packet's path to the packet type
     *
     * @return The packet map, which maps the packet's path to the packet type
     */
    public @NotNull Key2PacketMap byPath() {
        return this.byPath;
    }

    /**
     * Returns the packet map, which maps the packet's class to the packet type
     *
     * @return The packet map, which maps the packet's class to the packet type
     */
    public @NotNull Class2PacketMap byClass() {
        return this.byClass;
    }

    /**
     * Returns the hash code value for this packet registry
     *
     * @return The hash code value for this packet registry
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.byPath.hashCode();
        result = prime * result + this.byClass.hashCode();

        return result;
    }

    /**
     * Returns whether the given object is equal to this packet registry.
     * <p>
     * To be equal, the given object must be an instance of
     * {@link PacketRegistry} and have the same packet maps with same packet
     * types.
     *
     * @param obj The object to compare
     * @return True if the given object is equal to this packet registry
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
               || (
                       obj instanceof PacketRegistry that
                       && this.byPath.equals(that.byPath)
                       && this.byClass.equals(that.byClass)
               );
    }

    /**
     * Returns the string representation of this packet registry
     *
     * @return The string representation of this packet registry
     */
    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + '{' +
                "pathToPacketMap=" + this.byPath +
                ", classToPacketMap=" + this.byClass +
                '}';
    }

    /**
     * Creates a new packet registry with the given path packet map.
     * <p>
     * The class packet map will be created as an empty mutable map.
     *
     * @param byPath The packet map, which maps the packet's path to the packet
     *               type
     * @return A new packet registry with the given path packet map
     * @see #create(Key2PacketMap, Class2PacketMap)
     */
    @Contract("_ -> new")
    public static @NotNull PacketRegistry create(final @NotNull Key2PacketMap byPath) {
        return new PacketRegistry(byPath, PacketMap.class2PacketBuilder().build());
    }

    /**
     * Creates a new packet registry with the given packet maps.
     * <p>
     * <b>NOTE:</b> The class packet map will be a mutable map, check its
     *              documentation for more information.
     *
     * @param byPath  The packet map, which maps the packet's path to the packet
     *                type
     * @param byClass The packet map, which maps the packet's class to the
     *                packet type
     * @return A new packet registry with the given packet maps
     */
    @Contract("_, _ -> new")
    public static @NotNull PacketRegistry create(
            final @NotNull Key2PacketMap byPath,
            final @NotNull Class2PacketMap byClass
    ) {
        return new PacketRegistry(byPath, byClass);
    }
}
