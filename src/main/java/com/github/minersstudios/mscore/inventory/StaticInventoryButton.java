package com.github.minersstudios.mscore.inventory;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StaticInventoryButton {

    /**
     * @param listedInventory Listed inventory where button is located
     * @return Button in the specified listed inventory
     */
    @NotNull InventoryButton getButton(@NotNull ListedInventory listedInventory);
}
