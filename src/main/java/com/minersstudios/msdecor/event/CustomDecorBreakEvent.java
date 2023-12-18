package com.minersstudios.msdecor.event;

import com.minersstudios.msdecor.api.CustomDecor;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomDecorBreakEvent extends CustomDecorEvent implements Cancellable {
    protected boolean cancelled;
    protected final Entity breaker;

    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Constructs a new CustomDecorBreakEvent
     *
     * @param customDecor The custom decor involved in this event
     * @param breaker     The entity who broke the custom decor
     */
    public CustomDecorBreakEvent(
            final @NotNull CustomDecor customDecor,
            final @NotNull Entity breaker
    ) {
        super(customDecor);

        this.breaker = breaker;
    }

    /**
     * @return The entity who broke the custom decor involved in
     *         this event
     */
    public @NotNull Entity getBreaker() {
        return this.breaker;
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
