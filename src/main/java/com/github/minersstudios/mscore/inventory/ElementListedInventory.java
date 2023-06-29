package com.github.minersstudios.mscore.inventory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ElementListedInventory extends ListedInventory {
    protected final @NotNull Multimap<Integer, InventoryButton> elements;
    protected final int[] elementSlots;

    /**
     * Inventory with elements and pages
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     * @param elementSlots Slots of the elements in the inventory
     */
    protected ElementListedInventory(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize,
            int @Range(from = 0, to = Integer.MAX_VALUE) [] elementSlots
    ) {
        super(title, verticalSize);
        this.elementSlots = elementSlots;
        this.elements = ArrayListMultimap.create();
    }

    /**
     * Creates a new inventory with elements and pages
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     * @param elementSlots Slots of the elements in the inventory
     * @return New element listed inventory
     */
    @Contract("_, _, _ -> new")
    public static @NotNull ElementListedInventory create(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize,
            int @Range(from = 0, to = Integer.MAX_VALUE) [] elementSlots
    ) {
        return new ElementListedInventory(title, verticalSize, elementSlots);
    }

    /**
     * Used to update the pages of the inventory
     *
     * @return Listed inventory
     */
    @Override
    public @NotNull ElementListedInventory build() {
        this.updatePages();
        return this;
    }

    /**
     * @return Elements of the inventory
     */
    @Contract(" -> new")
    public @NotNull Multimap<Integer, InventoryButton> elements() {
        return ArrayListMultimap.create(this.elements);
    }

    /**
     * Set the elements of the inventory
     * <br>
     * <b>NOTE:</b> This will also update the pages and buttons
     *
     * @param elements New elements of the inventory
     * @return This inventory
     */
    public @NotNull ElementListedInventory elements(@NotNull List<InventoryButton> elements) {
        this.elements.clear();
        this.setPagesSize((int) Math.ceil((double) elements.size() / this.elementSlots.length));

        for (int page = 0; page < this.pagesSize; page++) {
            for (int element = 0; element < this.elementSlots.length; element++) {
                int index = element + (page * this.elementSlots.length);
                if (index >= elements.size()) break;
                this.elements.put(page, elements.get(index));
            }
        }

        this.updatePages();
        this.buttons(this.getPageContents(this.page));

        return this;
    }

    /**
     * Gets copy of the element slots
     *
     * @return element slot array
     */
    @Contract(" -> new")
    public int[] getElementSlots() {
        return this.elementSlots.clone();
    }

    /**
     * @param page Page index
     * @return Elements of the page
     */
    public @NotNull Map<Integer, InventoryButton> getPageContents(int page) {
        Map<Integer, InventoryButton> content = new HashMap<>(this.elementSlots.length);
        int i = 0;

        for (InventoryButton inventoryButton : this.elements.get(page)) {
            content.put(this.elementSlots[i], inventoryButton);
            i++;
        }

        Arrays.stream(this.elementSlots)
                .filter(slot -> !content.containsKey(slot))
                .forEach(slot -> content.put(slot, null));

        return content;
    }

    /**
     * Creates an inventory page with the specified index and content
     *
     * @param page Page index
     * @return Page of the inventory
     */
    public @Nullable ListedInventory createPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        if (page >= this.pagesSize) return null;

        ListedInventory listedInventory = (ListedInventory) this.clone();
        listedInventory.setPageIndex(page);
        listedInventory.buttons(this.getPageContents(page));
        return listedInventory;
    }

    /**
     * Updates the pages of the inventory
     * <br>
     * <b>Warning:</b> This method is expensive and should only be called when necessary
     */
    public void updatePages() {
        this.pages.clear();

        for (int page = 0; page < this.pagesSize; page++) {
            this.pages.put(page, this.createPage(page));
        }

        this.updateStaticButtons();
    }

    /**
     * Sets the pages size
     *
     * @param pagesSize New pages size
     */
    @Override
    protected void setPagesSize(@Range(from = 0, to = Integer.MAX_VALUE) int pagesSize) {
        this.pagesSize = pagesSize;
    }
}
