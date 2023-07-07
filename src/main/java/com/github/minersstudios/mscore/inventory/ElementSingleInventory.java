package com.github.minersstudios.mscore.inventory;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for single inventory with elements.
 * Element slots are slots where elements are located.
 * Elements are buttons that located in the element slots.
 *
 * @see CustomInventory
 * @see SingleInventory
 */
public class ElementSingleInventory extends CustomInventoryImpl<ElementSingleInventory> implements CustomInventory {
    protected final @NotNull List<InventoryButton> elements;
    protected final int[] elementSlots;

    /**
     * Inventory with elements and pages
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     * @param elementSlots Slots of the elements in the inventory
     */
    protected ElementSingleInventory(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize,
            int @Range(from = 0, to = Integer.MAX_VALUE) [] elementSlots
    ) {
        super(title, verticalSize);
        this.elementSlots = elementSlots;
        this.elements = new ArrayList<>();
    }

    /**
     * Creates a new inventory with elements
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     * @param elementSlots Slots of the elements in the inventory
     * @return New element inventory
     */
    @Contract("_, _, _ -> new")
    public static @NotNull ElementSingleInventory elementSingle(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize,
            int @Range(from = 0, to = Integer.MAX_VALUE) [] elementSlots
    ) {
        return new ElementSingleInventory(title, verticalSize, elementSlots);
    }

    /**
     * @return Elements of the inventory
     */
    @Contract(" -> new")
    public @NotNull List<InventoryButton> elements() {
        return List.copyOf(this.elements);
    }

    /**
     * Set the elements of the inventory
     *
     * @param elements New elements of the inventory
     * @return This inventory
     */
    public @NotNull ElementSingleInventory elements(@NotNull List<InventoryButton> elements) {
        this.elements.clear();

        for (int i = 0; i < this.elementSlots.length; i++) {
            if (i >= elements.size()) break;

            InventoryButton button = elements.get(i);

            this.elements.add(button);
            this.buttonAt(this.elementSlots[i], button);
        }

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
}
