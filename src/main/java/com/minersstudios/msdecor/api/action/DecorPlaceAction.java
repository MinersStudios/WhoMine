package com.minersstudios.msdecor.api.action;

import com.minersstudios.msdecor.event.CustomDecorPlaceEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DecorPlaceAction {
    DecorPlaceAction NONE = event -> {};

    void execute(final @NotNull CustomDecorPlaceEvent event);

    default @NotNull DecorPlaceAction andThen(final @NotNull DecorPlaceAction after) {
        return event -> {
            this.execute(event);
            after.execute(event);
        };
    }

    default boolean isSet() {
        return this != NONE;
    }
}
