package com.github.minersstudios.mscore.inventory;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder for listed inventories with pages and static buttons
 * <br>
 * Can have:
 * <ul>
 *     <li>Static buttons - buttons that do not change when the page index changes</li>
 *     <li>Pages that are also listed inventories</li>
 * </ul>
 * <br>
 *
 * @see CustomInventory
 * @see StaticInventoryButton
 * @see #build()
 */
public class ListedInventory extends CustomInventory {
    protected final @NotNull Map<Integer, StaticInventoryButton> staticButtons = new HashMap<>();
    protected final @NotNull Map<Integer, ListedInventory> pages = new HashMap<>();
    protected int page;
    protected int pagesSize;

    /**
     * Listed inventory with pages
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     */
    protected ListedInventory(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize
    ) {
        super(title, verticalSize);
    }

    /**
     * Creates a new listed inventory with pages
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     * @return New listed inventory
     */
    public static @NotNull ListedInventory create(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize
    ) {
        return new ListedInventory(title, verticalSize);
    }

    /**
     * Used to update static buttons in the inventory.
     * If there are no static buttons, this method does not need to be called.
     *
     * @return Listed inventory
     */
    public @NotNull ListedInventory build() {
        this.updateStaticButtons();
        return this;
    }

    /**
     * Integers - slot
     * <br>
     * StaticInventoryButton - static button placed in that slot
     *
     * @return Static button map of this listed inventory
     */
    public @NotNull Map<Integer, StaticInventoryButton> staticButtons() {
        return this.staticButtons;
    }

    /**
     * Sets static buttons in this inventory
     *
     * @param buttons Static buttons to set
     * @throws IllegalArgumentException If any of the static buttons is out of inventory size
     * @see #staticButtonAt(int, StaticInventoryButton)
     */
    public @NotNull ListedInventory staticButtons(@NotNull Map<Integer, StaticInventoryButton> buttons) throws IllegalArgumentException {
        buttons.forEach(this::staticButtonAt);
        return this;
    }

    /**
     * @return True if the inventory has any static buttons
     */
    public boolean hasStaticButtons() {
        return !this.staticButtons.isEmpty();
    }

    /**
     * @param slot Slot to get button from
     * @return {@link StaticInventoryButton} / {@link InventoryButton} at specified slot or null if there is no button
     */
    @Override
    public @Nullable InventoryButton buttonAt(@Range(from = 0, to = Integer.MAX_VALUE) int slot) {
        StaticInventoryButton staticButton = this.staticButtons.get(slot);
        return staticButton == null
                ? this.buttons.getOrDefault(slot, null)
                : staticButton.getButton(this);
    }

    /**
     * Sets static button at specified slot.
     * Static buttons are buttons that do not change when the page index changes.
     *
     * @param slot   Slot to set static button at
     * @param button Static button to set
     * @throws IllegalArgumentException If slot is out of inventory size
     */
    public @NotNull ListedInventory staticButtonAt(
            @Range(from = 0, to = LAST_SLOT) int slot,
            @Nullable StaticInventoryButton button
    ) throws IllegalArgumentException {
        this.validateSlot(slot);
        this.staticButtons.put(slot, button);
        return this;
    }

    /**
     * Integer - page index
     * <br>
     * ListedInventory - page
     *
     * @return Map of pages
     */
    public @NotNull Map<Integer, ListedInventory> getPages() {
        return this.pages;
    }

    /**
     * @param page page index
     * @return Page at specified index or null if there is no page
     */
    public @Nullable ListedInventory getPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        return this.pages.getOrDefault(page, null);
    }

    /**
     * @return Current page index
     */
    public int getPageIndex() {
        return this.page;
    }

    /**
     * Sets current page index
     *
     * @param page Page index to set
     */
    protected void setPageIndex(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        this.page = page;
    }

    /**
     * @return Next page index or -1 if there is no next page
     */
    public int getNextPageIndex() {
        int next = this.page + 1;
        return next >= this.pagesSize ? -1 : next;
    }

    /**
     * @return Previous page index or -1 if there is no previous page
     */
    public int getPreviousPageIndex() {
        int previous = this.page - 1;
        return previous < 0 ? -1 : previous;
    }

    /**
     * @return Pages size
     */
    public int getPagesSize() {
        return this.pagesSize;
    }

    /**
     * Sets pages size.
     * This method is used to update pages size in all pages.
     *
     * @param pagesSize Pages size to set
     */
    protected void setPagesSize(@Range(from = 0, to = Integer.MAX_VALUE) int pagesSize) {
        for (var listedInventory : this.pages.values()) {
            listedInventory.pagesSize = pagesSize;
        }
    }

    /**
     * Adds new page to the {@link #pages} with next index and static buttons
     *
     * @return New page
     */
    public @NotNull ListedInventory addPage() {
        int page = this.pagesSize;
        ListedInventory listedInventory = (ListedInventory) this.clone();

        listedInventory.setPageIndex(page);
        this.pages.put(page, listedInventory);
        this.updateStaticButtons(page);
        this.setPagesSize(this.pages.size());
        return listedInventory;
    }

    /**
     * Updates static buttons in all pages of the listed inventory
     */
    public void updateStaticButtons() {
        if (this.hasStaticButtons()) {
            this.staticButtons.forEach((slot, button) -> {
                for (var listedInventory : this.pages.values()) {
                    listedInventory.setItem(slot, button == null ? EMPTY_ITEM : button.getButton(listedInventory).item());
                }
            });
        }
    }

    /**
     * Updates static buttons in specified page
     *
     * @param page Page to update static buttons in
     */
    public void updateStaticButtons(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        ListedInventory listedInventory = this.pages.get(page);
        if (this.hasStaticButtons()) {
            for (var entry : this.staticButtons.entrySet()) {
                listedInventory.setItem(entry.getKey(), entry.getValue().getButton(listedInventory).item());
            }
        }
    }
}
