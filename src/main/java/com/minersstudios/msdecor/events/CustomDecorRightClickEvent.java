package com.minersstudios.msdecor.events;

import com.minersstudios.msdecor.customdecor.CustomDecorData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CustomDecorRightClickEvent extends CustomDecorEvent implements Cancellable {
    protected boolean cancelled;
    protected final Player player;
    protected final EquipmentSlot hand;
    protected final Vector clickedPosition;
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Constructs a new CustomBlockRightClickEvent
     *
     * @param customDecorData    The custom decor data involved in this event
     * @param player             The Player who right-clicked the custom decor
     * @param hand               The hand which was used to right-click
     *                           the custom decor
     * @param clickedPosition    The clicked position
     */
    public CustomDecorRightClickEvent(
            final @NotNull CustomDecorData<?> customDecorData,
            final @NotNull Player player,
            final @NotNull EquipmentSlot hand,
            final @NotNull Vector clickedPosition
    ) {
        super(customDecorData);

        this.player = player;
        this.hand = hand;
        this.clickedPosition = clickedPosition;
    }

    /**
     * @return The Player who right-clicked the custom decor
     *         involved in this event
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * @return The hand which was used to right-click the custom decor
     */
    public @NotNull EquipmentSlot getHand() {
        return this.hand;
    }

    /**
     * @return The clicked position
     */
    public @NotNull Vector getClickedPosition() {
        return this.clickedPosition;
    }

    /**
     * Sets the cancellation state of this event. A cancelled
     * event will not be executed in the server, but will still
     * pass to other plugins.
     *
     * @param cancel True if you wish to cancel this event
     */
    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * @return True if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * @return The handler list of this event
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * @return The handler list of this event
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
