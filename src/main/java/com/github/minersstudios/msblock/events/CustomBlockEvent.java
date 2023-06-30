package com.github.minersstudios.msblock.events;

import com.github.minersstudios.msblock.customblock.CustomBlock;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class CustomBlockEvent extends Event {
    protected final @NotNull CustomBlock customBlock;

    public CustomBlockEvent(
            final @NotNull CustomBlock customBlock
    ) {
        this.customBlock = customBlock;
    }

    /**
     * Gets the custom decor involved in this event
     *
     * @return The custom decor which is involved in this event
     */
    public final @NotNull CustomBlock getCustomDecor() {
        return this.customBlock;
    }
}
