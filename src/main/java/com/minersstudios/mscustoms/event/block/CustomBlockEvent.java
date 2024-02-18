package com.minersstudios.mscustoms.event.block;

import com.minersstudios.mscustoms.custom.block.CustomBlock;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class CustomBlockEvent extends Event {
    private final CustomBlock customBlock;

    /**
     * Constructs a new CustomBlockEvent
     *
     * @param customBlock The custom block involved in this event
     */
    public CustomBlockEvent(final @NotNull CustomBlock customBlock) {
        this.customBlock = customBlock;
    }

    /**
     * @return The custom block which is involved in this event
     */
    public final @NotNull CustomBlock getCustomBlock() {
        return this.customBlock;
    }
}
