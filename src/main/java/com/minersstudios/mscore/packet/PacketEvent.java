package com.minersstudios.mscore.packet;

import net.minecraft.network.Connection;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a packet event. It contains the packet container and the player
 * who sent or received the packet. It also implements {@link Cancellable} so
 * the packet can be cancelled. The packet container contains the packet and the
 * packet type and can be modified, but the packet type cannot be changed.
 *
 * @see PacketContainer
 * @see Cancellable
 */
public class PacketEvent implements Cancellable {
    private final PacketContainer packetContainer;
    private final Connection connection;
    private boolean cancelled;

    /**
     * @param packetContainer The packet container
     * @param connection      The connection
     */
    public PacketEvent(
            final @NotNull PacketContainer packetContainer,
            final @NotNull Connection connection
    ) {
        this.packetContainer = packetContainer;
        this.connection = connection;
    }

    /**
     * @return The packet container of this event
     */
    public final @NotNull PacketContainer getPacketContainer() {
        return this.packetContainer;
    }

    /**
     * @return The player who sent or received the packet
     */
    public final @NotNull Connection getConnection() {
        return this.connection;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not be
     * sent or received.
     *
     * @param cancel True if you wish to cancel this event
     */
    @Override
    public final void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * @return True if this event is cancelled
     */
    @Override
    public final boolean isCancelled() {
        return this.cancelled;
    }
}
