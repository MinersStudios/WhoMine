package com.minersstudios.msdecor.api.action;

import com.minersstudios.msdecor.event.CustomDecorBreakEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DecorBreakAction {
    DecorBreakAction NONE = event -> {};

    void execute(final @NotNull CustomDecorBreakEvent event);

    default @NotNull DecorBreakAction andThen(final @NotNull DecorBreakAction after) {
        return event -> {
            this.execute(event);
            after.execute(event);
        };
    }

    default boolean isSet() {
        return this != NONE;
    }
}
