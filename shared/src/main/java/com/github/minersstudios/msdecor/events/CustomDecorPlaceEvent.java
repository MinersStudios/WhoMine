package com.github.minersstudios.msdecor.events;

import com.github.minersstudios.msdecor.customdecor.CustomDecor;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomDecorPlaceEvent extends CustomDecorEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected boolean cancel;
	protected final BlockState replacedBlockState;
	protected final Player player;
	protected final EquipmentSlot hand;

	public CustomDecorPlaceEvent(
			@NotNull final CustomDecor placedCustomDecor,
			@NotNull final BlockState replacedBlockState,
			@NotNull final Player player,
			@Nullable final EquipmentSlot hand
	) {
		super(placedCustomDecor);
		this.player = player;
		this.replacedBlockState = replacedBlockState;
		this.hand = hand;
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
	 * Gets the player who placed the custom decor involved in this event
	 *
	 * @return The Player who placed the custom decor involved in this event
	 */
	public @NotNull Player getPlayer() {
		return this.player;
	}

	/**
	 * Gets the BlockState for the block which was replaced. Material type air mostly
	 *
	 * @return The BlockState for the block which was replaced
	 */
	public @NotNull BlockState getBlockReplacedState() {
		return this.replacedBlockState;
	}

	/**
	 * Gets the hand which placed the custom decor
	 *
	 * @return Main or off-hand, depending on which hand was used to place the custom decor
	 */
	public @Nullable EquipmentSlot getHand() {
		return this.hand;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static @NotNull HandlerList getHandlerList() {
		return handlers;
	}
}
