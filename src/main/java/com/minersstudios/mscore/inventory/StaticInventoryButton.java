package com.minersstudios.mscore.inventory;

import org.jetbrains.annotations.NotNull;

/**
 * Functional interface for static inventory buttons.
 * Static buttons are buttons that do not change when the page index changes.
 * <br>
 * Used in {@link PagedInventory} and {@link ElementPagedInventory} to get static buttons of the inventory.
 */
@FunctionalInterface
public interface StaticInventoryButton {

    /**
     * @param pagedCustomInventory Paged inventory where button is located
     * @return Button in the specified paged inventory
     */
    @NotNull InventoryButton getButton(final @NotNull PagedCustomInventory pagedCustomInventory);
}
