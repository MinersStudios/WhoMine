package com.minersstudios.wholib.packet;

import com.minersstudios.wholib.key.Key;
import com.minersstudios.wholib.packet.registry.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Locale;

/**
 * Represents the different packet protocols used in Minecraft.
 * <p>
 * Each protocol corresponds to a specific phase in the network connection
 * process.
 * <p>
 * <table>
 *     <caption>Available Protocols</caption>
 *     <tr>
 *         <th>Protocol</th>
 *         <th>Registry</th>
 *     </tr>
 *     <tr>
 *         <td>{@link #HANDSHAKING}</td>
 *         <td>{@link HandshakePackets}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #PLAY}</td>
 *         <td>{@link PlayPackets}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #STATUS}</td>
 *         <td>{@link StatusPackets}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #LOGIN}</td>
 *         <td>{@link LoginPackets}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #CONFIGURATION}</td>
 *         <td>{@link ConfigurationPackets}</td>
 *     </tr>
 * </table>
 *
 * @see PacketRegistry
 */
@SuppressWarnings("unused")
public enum PacketProtocol {
    /**
     * The handshake protocol
     *
     * @see HandshakePackets
     * @see <a href="https://wiki.vg/Protocol#Handshaking">Protocol Wiki - Handshake</a>
     */
    HANDSHAKING("handshake", HandshakePackets.registry()),
    /**
     * The play protocol
     *
     * @see PlayPackets
     * @see <a href="https://wiki.vg/Protocol#Play">Protocol Wiki - Play</a>
     */
    PLAY("play", PlayPackets.registry()),
    /**
     * The status protocol
     *
     * @see StatusPackets
     * @see <a href="https://wiki.vg/Protocol#Status">Protocol Wiki - Status</a>
     */
    STATUS("status", StatusPackets.registry()),
    /**
     * The login protocol
     *
     * @see LoginPackets
     * @see <a href="https://wiki.vg/Protocol#Login">Protocol Wiki - Login</a>
     */
    LOGIN("login", LoginPackets.registry()),
    /**
     * The configuration protocol
     *
     * @see ConfigurationPackets
     * @see <a href="https://wiki.vg/Protocol#Configuration">Protocol Wiki - Configuration</a>
     */
    CONFIGURATION("configuration", ConfigurationPackets.registry());

    private static final PacketProtocol[] VALUES = values();

    private final String id;
    private final PacketRegistry registry;

    /**
     * Constructor for the {@link PacketProtocol} enum
     *
     * @param id       The state ID of this protocol
     * @param registry The packet registry of this protocol
     */
    PacketProtocol(
            final @Key @NotNull String id,
            final @NotNull PacketRegistry registry
    ) {
        this.id = id;
        this.registry = registry;
    }

    /**
     * Returns the state ID of this protocol.
     * <p>
     * The ID is the same as the ID of the Minecraft protocol.
     *
     * @return The state ID of this protocol
     */
    public @NotNull String getId() {
        return this.id;
    }

    /**
     * Returns the packet registry of this protocol
     *
     * @return The packet registry of this protocol
     */
    public @NotNull PacketRegistry getRegistry() {
        return this.registry;
    }

    /**
     * Returns all available protocols as an array
     *
     * @return All available protocols as an array
     */
    public static PacketProtocol @NotNull [] protocols() {
        return VALUES;
    }

    /**
     * Returns the protocol from the given ordinal
     *
     * @param ordinal The ordinal of the protocol
     * @return The protocol from the given ordinal
     * @throws ArrayIndexOutOfBoundsException If the ordinal is out of bounds
     */
    public static @NotNull PacketProtocol byOrdinal(final @Range(from = 0, to = 4) int ordinal) throws ArrayIndexOutOfBoundsException {
        return VALUES[ordinal];
    }

    /**
     * Returns the protocol from the given state ID (case-insensitive)
     *
     * @param id The ID of the protocol
     * @return The protocol from the given state ID
     * @throws EnumConstantNotPresentException If the state ID is unknown
     * @see #dummyId(String)
     */
    public static @NotNull PacketProtocol fromId(final @Key @NotNull String id) throws EnumConstantNotPresentException {
        return dummyId(id.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Returns the protocol from the given state ID (case-sensitive)
     *
     * @param id The ID of the protocol
     * @return The protocol from the given state ID
     * @throws EnumConstantNotPresentException If the state ID is unknown
     * @see #fromId(String)
     */
    public static @NotNull PacketProtocol dummyId(final @Key @NotNull String id) throws EnumConstantNotPresentException {
        return switch (id) {
            case "handshake"     -> HANDSHAKING;
            case "play"          -> PLAY;
            case "status"        -> STATUS;
            case "login"         -> LOGIN;
            case "configuration" -> CONFIGURATION;
            default              -> throw new EnumConstantNotPresentException(PacketProtocol.class, id);
        };
    }

    /**
     * Returns the protocol from the given state ID (case-insensitive)
     *
     * @param id The ID of the protocol
     * @return The protocol from the given state ID or null if the ID is unknown
     * @see #dummyIdOrNull(String)
     */
    public static @Nullable PacketProtocol fromIdOrNull(final @Key @NotNull String id) {
        return dummyIdOrNull(id.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Returns the protocol from the given state ID (case-sensitive)
     *
     * @param id The ID of the protocol
     * @return The protocol from the given state ID or null if the ID is unknown
     * @see #fromIdOrNull(String)
     */
    public static @Nullable PacketProtocol dummyIdOrNull(final @Key @NotNull String id) {
        return switch (id) {
            case "handshake"     -> HANDSHAKING;
            case "play"          -> PLAY;
            case "status"        -> STATUS;
            case "login"         -> LOGIN;
            case "configuration" -> CONFIGURATION;
            default              -> null;
        };
    }
}
