package com.github.minersstudios.msblock.events;

import com.github.minersstudios.msblock.customblock.CustomBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CustomBlockDamageEvent extends CustomBlockEvent implements Cancellable {
	private static final @NotNull HandlerList HANDLER_LIST = new HandlerList();
	protected boolean cancel;

	protected final @NotNull Player player;
	protected final @NotNull ItemStack itemStack;

	public CustomBlockDamageEvent(
			final @NotNull CustomBlock damagedCustomBlock,
			final @NotNull Player player,
			final @NotNull ItemStack itemStack
	) {
		super(damagedCustomBlock);
		this.player = player;
		this.itemStack = itemStack;
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
	 * Gets the ItemStack for the item currently in the player's hand
	 *
	 * @return The ItemStack for the item currently in the player's hand
	 */
	public @NotNull ItemStack getItemInHand() {
		return this.itemStack;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
