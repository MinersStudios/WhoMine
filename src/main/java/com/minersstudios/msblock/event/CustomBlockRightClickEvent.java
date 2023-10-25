package com.minersstudios.msblock.event;

import com.minersstudios.msblock.api.CustomBlock;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class CustomBlockRightClickEvent extends CustomBlockEvent implements Cancellable {
    protected boolean cancelled;
    protected final Player player;
    protected final EquipmentSlot hand;
    protected final BlockFace blockFace;
    protected final Location interactionPoint;
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Constructs a new CustomBlockRightClickEvent
     *
     * @param customBlock        The custom block involved in this event
     * @param player             The Player who right-clicked the custom block
     * @param hand               The hand which was used to right-click
     *                           the custom block
     * @param blockFace          The block face on which the interaction occurred
     * @param interactionPoint   The exact point at which the interaction occurred
     */
    public CustomBlockRightClickEvent(
            final @NotNull CustomBlock customBlock,
            final @NotNull Player player,
            final @NotNull EquipmentSlot hand,
            final @NotNull BlockFace blockFace,
            final @NotNull Location interactionPoint
    ) {
        super(customBlock);

        this.player = player;
        this.hand = hand;
        this.blockFace = blockFace;
        this.interactionPoint = interactionPoint;
    }

    /**
     * @return The Player who right-clicked the custom block
     *         involved in this event
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * @return The hand which was used to right-click the custom block
     */
    public @NotNull EquipmentSlot getHand() {
        return this.hand;
    }

    /**
     * @return The block face on which the interaction occurred
     */
    public @NotNull BlockFace getBlockFace() {
        return this.blockFace;
    }

    /**
     * @return The exact point at which the interaction occurred
     */
    public @NotNull Location getInteractionPoint() {
        return this.interactionPoint;
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
