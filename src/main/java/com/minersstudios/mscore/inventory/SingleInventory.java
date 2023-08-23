package com.minersstudios.mscore.inventory;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Builder for custom inventories with actions and buttons
 * <br>
 * Actions:
 * <ul>
 *     <li>{@link #openAction()}</li>
 *     <li>{@link #closeAction()}</li>
 *     <li>{@link #clickAction()}</li>
 *     <li>{@link #bottomClickAction()}</li>
 * </ul>
 *
 * @see CustomInventory
 */
public class SingleInventory extends CustomInventoryImpl<SingleInventory> implements CustomInventory {

    /**
     * Single inventory
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     */
    protected SingleInventory(
            final @NotNull Component title,
            final @Range(from = 1, to = 6) int verticalSize
    ) {
        super(title, verticalSize);
    }

    /**
     * Creates new single page inventory with specified title and vertical size
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     * @return New custom inventory
     */
    @Contract("_, _ -> new")
    public static @NotNull SingleInventory single(
            final @NotNull Component title,
            final @Range(from = 1, to = 6) int verticalSize
    ) {
        return new SingleInventory(title, verticalSize);
    }
}
