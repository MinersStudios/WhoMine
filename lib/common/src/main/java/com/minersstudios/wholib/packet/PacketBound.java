package com.minersstudios.wholib.packet;

import com.minersstudios.wholib.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Locale;

/**
 * This enum represents a packet's bound in the Minecraft server networking.
 * <p>
 * Available bounds are:
 * <ul>
 *     <li>{@link #SERVERBOUND} - from the client to the server</li>
 *     <li>{@link #CLIENTBOUND} - from the server to the client</li>
 * </ul>
 *
 * @see PacketType
 * @see PacketProtocol
 */
@SuppressWarnings("unused")
public enum PacketBound {
    /** The serverbound packet flow */
    SERVERBOUND("serverbound"),
    /** The clientbound packet flow */
    CLIENTBOUND("clientbound");

    private static final PacketBound[] VALUES = values();

    private final String id;

    /**
     * Packet bound constructor
     *
     * @param id The state ID of the bound
     */
    PacketBound(final @Key @NotNull String id) {
        this.id = id;
    }

    /**
     * Returns the state ID of the bound
     *
     * @return The state ID of the bound
     */
    public @Key @NotNull String getId() {
        return this.id;
    }

    /**
     * Returns the opposite bound
     *
     * @return The opposite bound
     */
    public @NotNull PacketBound getOpposite() {
        return this == CLIENTBOUND
               ? SERVERBOUND
               : CLIENTBOUND;
    }

    /**
     * Returns all available bounds as an array
     *
     * @return All available bounds as an array
     */
    public static PacketBound @NotNull [] bounds() {
        return VALUES;
    }

    /**
     * Returns the bound from the given ordinal
     *
     * @param ordinal The ordinal of the bound
     * @return The bound from the given ordinal
     * @throws ArrayIndexOutOfBoundsException If the ordinal is out of bounds
     */
    public static @NotNull PacketBound byOrdinal(final @Range(from = 0, to = 1) int ordinal) throws ArrayIndexOutOfBoundsException {
        return VALUES[ordinal];
    }

    /**
     * Returns the bound from the given state ID (case-insensitive)
     *
     * @param id The state ID of the bound
     * @return The bound from the given ID
     * @throws EnumConstantNotPresentException If the state ID is unknown
     */
    public static @NotNull PacketBound fromId(final @Key @NotNull String id) throws EnumConstantNotPresentException {
        return dummyId(id.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Returns the bound from the given state ID (case-sensitive)
     *
     * @param id The state ID of the bound
     * @return The bound from the given ID
     * @throws EnumConstantNotPresentException If the state ID is unknown
     */
    public static @NotNull PacketBound dummyId(final @Key @NotNull String id) throws EnumConstantNotPresentException {
        return switch (id) {
            case "serverbound" -> SERVERBOUND;
            case "clientbound" -> CLIENTBOUND;
            default            -> throw new EnumConstantNotPresentException(PacketBound.class, id);
        };
    }

    /**
     * Returns the bound from the given state ID (case-insensitive)
     *
     * @param id The state ID of the bound
     * @return The bound from the given ID, or null if the state ID is unknown
     */
    public static @Nullable PacketBound fromIdOrNull(final @Key @NotNull String id) {
        return dummyIdOrNull(id.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Returns the bound from the given state ID (case-sensitive)
     *
     * @param id The state ID of the bound
     * @return The bound from the given ID, or null if the state ID is unknown
     */
    public static @Nullable PacketBound dummyIdOrNull(final @Key @NotNull String id) {
        return switch (id) {
            case "serverbound" -> SERVERBOUND;
            case "clientbound" -> CLIENTBOUND;
            default            -> null;
        };
    }
}
