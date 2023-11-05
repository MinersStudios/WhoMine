package com.minersstudios.msdecor.api.action;

import com.minersstudios.msdecor.event.CustomDecorClickEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DecorClickAction {
    DecorClickAction NONE = event -> {};

    void execute(final @NotNull CustomDecorClickEvent event);

    default @NotNull DecorClickAction andThen(final @NotNull DecorClickAction after) {
        return event -> {
            this.execute(event);
            after.execute(event);
        };
    }

    default boolean isSet() {
        return this != NONE;
    }
}
