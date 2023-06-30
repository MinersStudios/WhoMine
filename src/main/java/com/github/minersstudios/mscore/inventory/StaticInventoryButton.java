package com.github.minersstudios.mscore.inventory;

import org.jetbrains.annotations.NotNull;

/**
 * Functional interface for static inventory buttons.
 * Static buttons are buttons that do not change when the page index changes.
 * <br>
 * Used in {@link ListedInventory} to get static buttons of the inventory.
 */
@FunctionalInterface
public interface StaticInventoryButton {

    /**
     * @param listedInventory Listed inventory where button is located
     * @return Button in the specified listed inventory
     */
    @NotNull InventoryButton getButton(@NotNull ListedInventory listedInventory);
}
