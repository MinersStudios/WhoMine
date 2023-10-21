package com.minersstudios.msdecor.events;

import com.minersstudios.msdecor.customdecor.CustomDecor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomDecorBreakEvent extends CustomDecorEvent implements Cancellable {
    protected boolean cancelled;
    protected final Player player;

    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Constructs a new CustomDecorBreakEvent
     *
     * @param customDecor     The custom decor involved in this event
     * @param player          The player who broke the custom decor
     *                        involved in this event
     */
    public CustomDecorBreakEvent(
            final @NotNull CustomDecor customDecor,
            final @NotNull Player player
    ) {
        super(customDecor);

        this.player = player;
    }

    /**
     * @return The Player who broke the custom decor
     *         involved in this event
     */
    public @NotNull Player getPlayer() {
        return this.player;
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
