package com.minersstudios.msessentials.player.map;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Mute entry, used in {@link MuteMap}
 * <br>
 * Parameters:
 * <ul>
 *     <li>created - date when the player was muted</li>
 *     <li>expiration - date when the player will be unmuted</li>
 *     <li>reason - mute reason</li>
 *     <li>source - mute source, could be a player's nickname or CONSOLE</li>
 * </ul>
 *
 * @see MuteMap
 */
public class MuteEntry {
    private final Instant created;
    private final Instant expiration;
    private final String reason;
    private final String source;

    private MuteEntry(
            Instant created,
            Instant expiration,
            String reason,
            String source
    ) {
        this.created = created;
        this.expiration = expiration;
        this.reason = reason;
        this.source = source;
    }

    /**
     * Creates a new {@link MuteEntry} with the specified parameters
     *
     * @param created    Date when the player was muted
     * @param expiration Date when the player will be unmuted
     * @param reason     Mute reason
     * @param source     Mute source, could be a player's nickname or CONSOLE
     * @return New {@link MuteEntry}
     */
    @Contract(value = "_, _, _, _ -> new")
    public static @NotNull MuteEntry create(
            @NotNull Instant created,
            @NotNull Instant expiration,
            @NotNull String reason,
            @NotNull String source
    ) {
        return new MuteEntry(created, expiration, reason, source);
    }

    /**
     * @return Date when the player was muted
     */
    public Instant getCreated() {
        return this.created;
    }

    /**
     * @return Date when the player will be unmuted
     */
    public Instant getExpiration() {
        return this.expiration;
    }

    /**
     * @return Mute reason
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * @return Mute source, could be a player's nickname or CONSOLE
     */
    public String getSource() {
        return this.source;
    }

    /**
     * @return True if created, expiration, reason, source are not null
     */
    public boolean isValidate() {
        return this.created != null
                && this.expiration != null
                && this.reason != null
                && this.source != null;
    }
}
