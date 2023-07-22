package com.minersstudios.mscore.packet;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a packet event. It contains the packet container
 * and the player who sent or received the packet. It also
 * implements {@link Cancellable} so the packet can be cancelled.
 * The packet container contains the packet and the packet type
 * and can be modified, but the packet type cannot be changed.
 *
 * @see PacketContainer
 * @see Cancellable
 */
public class PacketEvent implements Cancellable {
    private final PacketContainer packetContainer;
    private final Player player;
    private boolean cancelled;

    /**
     * @param packetContainer The packet container
     * @param player          The player who sent or received the packet
     */
    public PacketEvent(
            @NotNull PacketContainer packetContainer,
            @NotNull Player player
    ) {
        this.packetContainer = packetContainer;
        this.player = player;
    }

    /**
     * @return The packet container of this event
     */
    public @NotNull PacketContainer getPacketContainer() {
        return this.packetContainer;
    }

    /**
     * @return The player who sent or received the packet
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * Sets the cancellation state of this event. A cancelled
     * event will not be sent or received.
     *
     * @param cancel True if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * @return True if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
