package com.github.minersstudios.mscore.inventory.actions;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents action that is performed when button is clicked
 */
@FunctionalInterface
public interface ButtonClickAction {

    /**
     * Do action when button is clicked
     *
     * @param event           Click event that triggered the action
     * @param customInventory Custom inventory that is involved in this event
     */
    void doAction(
            @NotNull InventoryClickEvent event,
            @NotNull CustomInventory customInventory
    );
}
