package com.minersstudios.msdecor.event;

import com.minersstudios.msdecor.api.CustomDecor;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class CustomDecorEvent extends Event {
    protected final CustomDecor customDecor;

    /**
     * Constructs a new CustomDecorEvent
     *
     * @param customDecor The custom decor which is involved in this event
     */
    public CustomDecorEvent(final @NotNull CustomDecor customDecor) {
        this.customDecor = customDecor;
    }

    /**
     * @return The custom decor which is involved in this event
     */
    public final @NotNull CustomDecor getCustomDecor() {
        return this.customDecor;
    }
}
