package com.minersstudios.msblock.events;

import com.minersstudios.msblock.customblock.CustomBlock;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class CustomBlockRightClickEvent extends CustomBlockEvent implements Cancellable {
    private static final @NotNull HandlerList HANDLER_LIST = new HandlerList();
    protected boolean cancel;

    protected final @NotNull Player player;
    protected final @NotNull EquipmentSlot hand;
    protected final @NotNull BlockFace blockFace;
    protected final @NotNull Location interactionPoint;

    public CustomBlockRightClickEvent(
            @NotNull CustomBlock damagedCustomBlock,
            @NotNull Player player,
            @NotNull EquipmentSlot hand,
            @NotNull BlockFace blockFace,
            @NotNull Location interactionPoint
    ) {
        super(damagedCustomBlock);

        this.player = player;
        this.hand = hand;
        this.blockFace = blockFace;
        this.interactionPoint = interactionPoint;
        this.cancel = false;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Gets the player who damaged the custom block involved in this event
     *
     * @return The Player who damaged the custom block involved in this event
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * The hand used to perform this interaction
     *
     * @return The hand used to interact
     */
    public @NotNull EquipmentSlot getHand() {
        return this.hand;
    }

    /**
     * Returns the face of the custom block that was clicked
     *
     * @return BlockFace returns the face of the custom block that was clicked
     */
    public @NotNull BlockFace getBlockFace() {
        return this.blockFace;
    }

    /**
     * The exact point at which the interaction occurred
     *
     * @return The exact interaction point
     */
    public @NotNull Location getInteractionPoint() {
        return this.interactionPoint;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
