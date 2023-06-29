package com.github.minersstudios.mscore.inventory.actions;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface InventoryAction<E extends Event> {

    /**
     * Performs action when event is triggered
     *
     * @param event           Event that triggered the action
     * @param customInventory Custom inventory that is involved in this event
     */
    void doAction(
            @NotNull E event,
            @NotNull CustomInventory customInventory
    );
}
