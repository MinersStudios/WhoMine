package com.minersstudios.mscore.inventory;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Builder for paged inventories with pages and static buttons
 * <br>
 * Can have:
 * <ul>
 *     <li>Static buttons - buttons that do not change when the page index changes</li>
 *     <li>Pages that are also paged inventories</li>
 * </ul>
 * <br>
 *
 * @see CustomInventory
 * @see PagedCustomInventory
 * @see StaticInventoryButton
 * @see #build()
 */
public class PagedInventory extends PagedCustomInventoryImpl<PagedInventory> implements PagedCustomInventory {

    /**
     * Paged inventory
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     */
    protected PagedInventory(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize
    ) {
        super(title, verticalSize);
    }

    /**
     * Creates a new paged inventory
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     * @return New paged inventory
     */
    public static @NotNull PagedInventory paged(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize
    ) {
        return new PagedInventory(title, verticalSize);
    }
}
