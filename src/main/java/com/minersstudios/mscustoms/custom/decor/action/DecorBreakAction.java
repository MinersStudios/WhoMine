package com.minersstudios.mscustoms.custom.decor.action;

import com.minersstudios.mscustoms.event.decor.CustomDecorBreakEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an action to be performed when a custom decor is broken
 */
@FunctionalInterface
public interface DecorBreakAction {
    DecorBreakAction NONE = event -> {};

    /**
     * Performs this action on the given event
     *
     * @param event Custom decor break event
     */
    void execute(final @NotNull CustomDecorBreakEvent event);

    /**
     * @param after The operation to perform after this operation
     * @return A composed {@code DecorBreakAction} that performs in sequence this
     *         operation followed by the {@code after} operation
     * @throws NullPointerException If {@code after} is null
     */
    default @NotNull DecorBreakAction andThen(final @NotNull DecorBreakAction after) {
        return event -> {
            this.execute(event);
            after.execute(event);
        };
    }

    /**
     * @return Whether this action is set
     */
    default boolean isSet() {
        return this != NONE;
    }
}
