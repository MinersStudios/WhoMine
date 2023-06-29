package com.github.minersstudios.msdecor.events;

import com.github.minersstudios.msdecor.customdecor.CustomDecor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CustomDecorBreakEvent extends CustomDecorEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected boolean cancel;
	protected final Player player;

	public CustomDecorBreakEvent(
			@NotNull final CustomDecor breakedCustomDecor,
			@NotNull final Player player
	) {
		super(breakedCustomDecor);
		this.player = player;
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
	 * Gets the player who broke the custom decor involved in this event
	 *
	 * @return The Player who broke the custom decor involved in this event
	 */
	public @NotNull Player getPlayer() {
		return this.player;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static @NotNull HandlerList getHandlerList() {
		return handlers;
	}
}
