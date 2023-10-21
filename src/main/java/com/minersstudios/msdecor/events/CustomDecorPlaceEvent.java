package com.minersstudios.msdecor.events;

import com.minersstudios.msdecor.customdecor.CustomDecor;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class CustomDecorPlaceEvent extends CustomDecorEvent implements Cancellable {
    protected boolean cancelled;
    protected final BlockState replacedBlockState;
    protected final Player player;
    protected final EquipmentSlot hand;

    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Constructs a new CustomBlockPlaceEvent
     *
     * @param customDecor        The custom decor involved in this event
     * @param replacedBlockState The BlockState for the block which was
     *                           replaced
     * @param player             The player who placed the custom decor
     *                           involved in this event
     * @param hand               Main or off-hand, depending on which hand
     *                           was used to place the custom decor
     */
    public CustomDecorPlaceEvent(
            final @NotNull CustomDecor customDecor,
            final @NotNull BlockState replacedBlockState,
            final @NotNull Player player,
            final @NotNull EquipmentSlot hand
    ) {
        super(customDecor);

        this.player = player;
        this.replacedBlockState = replacedBlockState;
        this.hand = hand;
    }

    /**
     * @return The Player who placed the custom decor
     *         involved in this event
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * @return The BlockState for the block which was replaced.
     *         Material type air mostly.
     */
    public @NotNull BlockState getBlockReplacedState() {
        return this.replacedBlockState;
    }

    /**
     * @return Main or off-hand, depending on which hand
     *         was used to place the custom decor
     */
    public @NotNull EquipmentSlot getHand() {
        return this.hand;
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
