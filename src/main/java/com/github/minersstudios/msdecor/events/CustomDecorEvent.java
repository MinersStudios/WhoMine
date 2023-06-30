package com.github.minersstudios.msdecor.events;

import com.github.minersstudios.msdecor.customdecor.CustomDecor;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class CustomDecorEvent extends Event {
    protected final CustomDecor customDecor;

    public CustomDecorEvent(
            final @NotNull CustomDecor customDecor
    ) {
        this.customDecor = customDecor;
    }

    /**
     * Gets the custom decor involved in this event
     *
     * @return The custom decor which is involved in this event
     */
    public final @NotNull CustomDecor getCustomDecor() {
        return this.customDecor;
    }
}
