package com.github.minersstudios.mscore.inventory.actions;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.inventory.InventoryButton;
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
     * @param event           Event that triggered the action
     * @param customInventory Custom inventory that is involved in this event
     * @param button          Button that was clicked
     */
    void doAction(
            @NotNull InventoryClickEvent event,
            @NotNull CustomInventory customInventory,
            @NotNull InventoryButton button
    );
}
