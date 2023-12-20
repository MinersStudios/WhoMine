package com.minersstudios.mscore.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Map;

/**
 * Implementation of {@link CustomInventory} that allows creating custom
 * inventories with pages and static buttons
 * <br>
 * Can have:
 * <ul>
 *     <li>Static buttons - buttons that do not change when the page index changes</li>
 *     <li>Pages that are also paged inventories</li>
 * </ul>
 * <br>
 *
 * @see CustomInventory
 * @see PagedInventory
 * @see ElementPagedInventory
 * @see StaticInventoryButton
 */
public interface PagedCustomInventory extends CustomInventory {

    /**
     * Used to update static buttons in the inventory. If there are no static
     * buttons, this method does not need to be called.
     *
     * @return Paged inventory
     */
    PagedCustomInventory build();

    /**
     * Integers - slot
     * <br>
     * StaticInventoryButton - static button placed in that slot
     *
     * @return Static button map of this paged inventory
     */
    @NotNull Map<Integer, StaticInventoryButton> staticButtons();

    /**
     * Sets static buttons in this inventory
     *
     * @param buttons Static buttons to set
     * @throws IllegalArgumentException If any of the static buttons is out of
     *                                  inventory size
     * @see #staticButtonAt(int, StaticInventoryButton)
     */
    PagedCustomInventory staticButtons(final @NotNull Map<Integer, StaticInventoryButton> buttons) throws IllegalArgumentException;

    /**
     * @return True if the inventory has any static buttons
     */
    boolean hasStaticButtons();

    /**
     * @param slot Slot to get button from
     * @return {@link StaticInventoryButton} / {@link InventoryButton} at
     *         specified slot or null if there is no button
     */
    @Override
    @Nullable InventoryButton buttonAt(final int slot);

    /**
     * Sets static button at specified slot. Static buttons are buttons that do
     * not change when the page index changes.
     *
     * @param slot   Slot to set static button at
     * @param button Static button to set
     * @throws IllegalArgumentException If the slot is out of inventory size
     */
    PagedCustomInventory staticButtonAt(
            final @Range(from = 0, to = CustomInventoryImpl.LAST_SLOT) int slot,
            final @Nullable StaticInventoryButton button
    ) throws IllegalArgumentException;

    /**
     * Integer - page index
     * <br>
     * pagedInventory - page
     *
     * @return Map of pages
     */
    @NotNull Map<Integer, ?> getPages();

    /**
     * @param page page index
     * @return Page at specified index or null if there is no page
     */
    PagedCustomInventory getPage(final @Range(from = 0, to = Integer.MAX_VALUE) int page);

    /**
     * @return Current page index
     */
    int getPageIndex();

    /**
     * @return Next page index or -1 if there is no next page
     */
    int getNextPageIndex();

    /**
     * @return Previous page index or -1 if there is no previous page
     */
    int getPreviousPageIndex();

    /**
     * @return Pages size
     */
    int getPagesCount();

    /**
     * Adds new page to the {@link PagedCustomInventoryImpl#pages} with next
     * index and static buttons
     *
     * @return New page
     */
    PagedCustomInventory addPage();

    /**
     * Updates static buttons in all pages of the paged inventory
     */
    void updateStaticButtons();

    /**
     * Updates static buttons in specified page
     *
     * @param page Page to update static buttons in
     */
    void updateStaticButtons(final @Range(from = 0, to = Integer.MAX_VALUE) int page);
}
