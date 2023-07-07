package com.github.minersstudios.mscore.inventory;

import com.github.minersstudios.mscore.inventory.actions.InventoryAction;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link Inventory} that allows to create
 * custom inventories with custom buttons and actions
 * <br>
 * Actions:
 * <ul>
 *     <li>{@link #openAction()}</li>
 *     <li>{@link #closeAction()}</li>
 *     <li>{@link #clickAction()}</li>
 *     <li>{@link #bottomClickAction()}</li>
 * </ul>
 */
public interface CustomInventory extends Inventory, Cloneable {
    /**
     * @return Title of this inventory as string
     */
    @NotNull String getTitle();

    /**
     * @return Title of this inventory as component
     */
    @NotNull Component title();

    /**
     * Last slot in the inventory (5th row, 9th column)
     * <br>
     * 0 is first slot
     */
    @NotNull Map<Integer, InventoryButton> buttons();

    /**
     * Sets buttons in this inventory
     *
     * @param buttons Buttons to set
     * @return This instance
     * @throws IllegalArgumentException If any of the buttons is out of inventory size
     */
    @NotNull CustomInventory buttons(@NotNull Map<Integer, InventoryButton> buttons) throws IllegalArgumentException;

    /**
     * @return True if this inventory has any buttons
     */
    boolean hasButtons();

    /**
     * Gets button at specified slot
     *
     * @param slot Slot to get button from
     * @return Button at specified slot or null if there is no button
     */
    @Nullable InventoryButton buttonAt(@Range(from = 0, to = CustomInventoryImpl.LAST_SLOT) int slot);

    /**
     * Sets button at specified slot
     *
     * @param slot   Slot to set button at
     * @param button Button to set
     * @return This inventory
     * @throws IllegalArgumentException If slot is out of inventory size
     */
    @NotNull CustomInventory buttonAt(
            @Range(from = 0, to = CustomInventoryImpl.LAST_SLOT) int slot,
            @Nullable InventoryButton button
    ) throws IllegalArgumentException;

    /**
     * @return Inventory arguments
     */
    @NotNull List<Object> args();

    /**
     * Sets inventory arguments
     *
     * @param args New inventory arguments
     * @return This inventory
     */
    @NotNull CustomInventory args(@NotNull List<Object> args);

    /**
     * Gets inventory open action
     *
     * @return Inventory action that is performed when this inventory is opened
     * @see #openAction(InventoryAction)
     */
    @Nullable InventoryAction<InventoryOpenEvent> openAction();

    /**
     * Sets inventory action that is performed when this inventory is opened
     *
     * @param openAction New open action
     * @return This inventory
     * @see #openAction()
     */
    @NotNull CustomInventory openAction(@Nullable InventoryAction<InventoryOpenEvent> openAction);

    /**
     * Gets inventory close action
     *
     * @return Inventory action that is performed when this inventory is closed
     * @see #closeAction(InventoryAction)
     */
    @Nullable InventoryAction<InventoryCloseEvent> closeAction();

    /**
     * Sets inventory action that is performed when this inventory is closed
     *
     * @param closeAction New close action
     * @return This inventory
     * @see #closeAction()
     */
    @NotNull CustomInventory closeAction(@Nullable InventoryAction<InventoryCloseEvent> closeAction);

    /**
     * Gets inventory click action
     *
     * @return Inventory action that is performed when this inventory is clicked
     * @see #clickAction(InventoryAction)
     */
    @Nullable InventoryAction<InventoryClickEvent> clickAction();

    /**
     * Sets inventory action that is performed when this inventory is clicked
     *
     * @param clickAction New click action
     * @return This inventory
     * @see #clickAction()
     */
    @NotNull CustomInventory clickAction(@Nullable InventoryAction<InventoryClickEvent> clickAction);

    /**
     * Gets bottom inventory click action
     *
     * @return Inventory action that is performed when player is clicked bottom inventory
     * @see #bottomClickAction(InventoryAction)
     */
    @Nullable InventoryAction<InventoryClickEvent> bottomClickAction();

    /**
     * Sets inventory action that is performed when player is clicked bottom inventory
     *
     * @param bottomClickAction New bottom inventory click action
     * @return This inventory
     * @see #bottomClickAction()
     */
    @NotNull CustomInventory bottomClickAction(@Nullable InventoryAction<InventoryClickEvent> bottomClickAction);

    /**
     * Performs the opening action when the inventory is opened, if it is set
     *
     * @param event Event that triggered the action
     * @see #openAction(InventoryAction)
     */
    void doOpenAction(@NotNull InventoryOpenEvent event);

    /**
     * Performs the closing action when the inventory is closed, if it is set
     *
     * @param event Event that triggered the action
     * @see #closeAction(InventoryAction)
     */
    void doCloseAction(@NotNull InventoryCloseEvent event);

    /**
     * Performs the clicking action when the inventory is clicked, if it is set
     * <br>
     * If click action is not set, it will cancel the click event
     *
     * @param event Event that triggered the action
     * @see #clickAction(InventoryAction)
     */
    void doClickAction(@NotNull InventoryClickEvent event);

    /**
     * Performs the clicking action when player is clicked bottom inventory, if it is set
     *
     * @param event Event that triggered the action
     * @see #bottomClickAction(InventoryAction)
     */
    void doBottomClickAction(@NotNull InventoryClickEvent event);

    /**
     * Checks if slot is in inventory bounds
     *
     * @param slot Slot to validate
     * @throws IllegalArgumentException If slot is out of inventory size
     */
    void validateSlot(int slot) throws IllegalArgumentException;

    /**
     * Creates a clone of this inventory with all the contents copied into it
     *
     * @return Clone of this inventory
     */
    @NotNull CustomInventory clone();
}
