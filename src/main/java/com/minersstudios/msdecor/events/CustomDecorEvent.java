package com.minersstudios.msdecor.events;

import com.minersstudios.msdecor.customdecor.CustomDecorData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class CustomDecorEvent extends Event {
    protected final CustomDecorData<?> customDecorData;

    /**
     * Constructs a new CustomDecorEvent
     *
     * @param customDecorData The custom decor data involved in this event
     */
    public CustomDecorEvent(final @NotNull CustomDecorData<?> customDecorData) {
        this.customDecorData = customDecorData;
    }

    /**
     * @return The custom decor which is involved in this event
     */
    public final @NotNull CustomDecorData<?> getCustomDecorData() {
        return this.customDecorData;
    }
}
