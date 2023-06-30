package com.github.minersstudios.mscore.inventory;

import com.github.minersstudios.mscore.inventory.actions.InventoryAction;
import net.kyori.adventure.text.Component;
import net.minecraft.world.Container;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class CustomInventory extends CraftInventoryCustom implements Inventory, Cloneable {
    protected final int size;
    protected @NotNull Map<Integer, InventoryButton> buttons;
    protected @Nullable InventoryAction<InventoryOpenEvent> openAction;
    protected @Nullable InventoryAction<InventoryCloseEvent> closeAction;
    protected @Nullable InventoryAction<InventoryClickEvent> clickAction;
    protected @Nullable InventoryAction<InventoryClickEvent> bottomClickAction;

    protected static final ItemStack EMPTY_ITEM = new ItemStack(Material.AIR);

    /**
     * Last slot in the inventory (5th row, 9th column)
     * <br>
     * 0 is first slot
     */
    protected static final int LAST_SLOT = 53;

    /**
     * Custom inventory
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     */
    protected CustomInventory(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize
    ) {
        super(null, verticalSize * 9, title);
        this.size = verticalSize * 9;
        this.buttons = new HashMap<>(this.size);
    }

    /**
     * Creates new custom inventory with specified title and vertical size
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     * @return New custom inventory
     */
    @Contract("_, _ -> new")
    public static @NotNull CustomInventory create(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize
    ) {
        return new CustomInventory(title, verticalSize);
    }

    /**
     * Integers - slot
     * <br>
     * InventoryButton - button placed in that slot
     *
     * @return Button map of this inventory
     */
    public @NotNull Map<Integer, InventoryButton> buttons() {
        return this.buttons;
    }

    /**
     * Sets buttons in this inventory
     *
     * @param buttons Buttons to set
     * @return This instance
     * @throws IllegalArgumentException If any of the buttons is out of inventory size
     */
    public @NotNull CustomInventory buttons(@NotNull Map<Integer, InventoryButton> buttons) throws IllegalArgumentException {
        buttons.forEach(this::buttonAt);
        return this;
    }

    /**
     * @return True if this inventory has any buttons
     */
    public boolean hasButtons() {
        return !this.buttons.isEmpty();
    }

    /**
     * Gets button at specified slot
     *
     * @param slot Slot to get button from
     * @return Button at specified slot or null if there is no button
     */
    public @Nullable InventoryButton buttonAt(@Range(from = 0, to = LAST_SLOT) int slot) {
        return this.buttons.getOrDefault(slot, null);
    }

    /**
     * Sets button at specified slot
     *
     * @param slot   Slot to set button at
     * @param button Button to set
     * @return This inventory
     * @throws IllegalArgumentException If slot is out of inventory size
     */
    public @NotNull CustomInventory buttonAt(
            @Range(from = 0, to = LAST_SLOT) int slot,
            @Nullable InventoryButton button
    ) throws IllegalArgumentException {
        this.validateSlot(slot);
        this.buttons.put(slot, button);
        this.setItem(slot, button == null ? EMPTY_ITEM : button.item());
        return this;
    }

    /**
     * Gets inventory open action
     *
     * @return Inventory action that is performed when this inventory is opened
     * @see #openAction(InventoryAction)
     */
    public @Nullable InventoryAction<InventoryOpenEvent> openAction() {
        return this.openAction;
    }

    /**
     * Sets inventory action that is performed when this inventory is opened
     *
     * @param openAction New open action
     * @return This inventory
     * @see #openAction()
     */
    public @NotNull CustomInventory openAction(@Nullable InventoryAction<InventoryOpenEvent> openAction) {
        this.openAction = openAction;
        return this;
    }

    /**
     * Gets inventory close action
     *
     * @return Inventory action that is performed when this inventory is closed
     * @see #closeAction(InventoryAction)
     */
    public @Nullable InventoryAction<InventoryCloseEvent> closeAction() {
        return this.closeAction;
    }

    /**
     * Sets inventory action that is performed when this inventory is closed
     *
     * @param closeAction New close action
     * @return This inventory
     * @see #closeAction()
     */
    public @NotNull CustomInventory closeAction(@Nullable InventoryAction<InventoryCloseEvent> closeAction) {
        this.closeAction = closeAction;
        return this;
    }

    /**
     * Gets inventory click action
     *
     * @return Inventory action that is performed when this inventory is clicked
     * @see #clickAction(InventoryAction)
     */
    public @Nullable InventoryAction<InventoryClickEvent> clickAction() {
        return this.clickAction;
    }

    /**
     * Sets inventory action that is performed when this inventory is clicked
     *
     * @param clickAction New click action
     * @return This inventory
     * @see #clickAction()
     */
    public @NotNull CustomInventory clickAction(@Nullable InventoryAction<InventoryClickEvent> clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    /**
     * Gets bottom inventory click action
     *
     * @return Inventory action that is performed when player is clicked bottom inventory
     * @see #bottomClickAction(InventoryAction)
     */
    public @Nullable InventoryAction<InventoryClickEvent> bottomClickAction() {
        return this.bottomClickAction;
    }

    /**
     * Sets inventory action that is performed when player is clicked bottom inventory
     *
     * @param bottomClickAction New bottom inventory click action
     * @return This inventory
     * @see #bottomClickAction()
     */
    public @NotNull CustomInventory bottomClickAction(@Nullable InventoryAction<InventoryClickEvent> bottomClickAction) {
        this.bottomClickAction = bottomClickAction;
        return this;
    }

    /**
     * Performs the opening action when the inventory is opened, if it is set
     *
     * @param event Event that triggered the action
     * @see #openAction(InventoryAction)
     */
    public void doOpenAction(@NotNull InventoryOpenEvent event) {
        if (this.openAction != null) {
            this.openAction.doAction(event, this.clone());
        }
    }

    /**
     * Performs the closing action when the inventory is closed, if it is set
     *
     * @param event Event that triggered the action
     * @see #closeAction(InventoryAction)
     */
    public void doCloseAction(@NotNull InventoryCloseEvent event) {
        if (this.closeAction != null) {
            this.closeAction.doAction(event, this.clone());
        }
    }

    /**
     * Performs the clicking action when the inventory is clicked, if it is set
     * <br>
     * If click action is not set, it will cancel the click event
     *
     * @param event Event that triggered the action
     * @see #clickAction(InventoryAction)
     */
    public void doClickAction(@NotNull InventoryClickEvent event) {
        if (this.clickAction != null) {
            this.clickAction.doAction(event, this.clone());
        }
    }

    /**
     * Performs the clicking action when player is clicked bottom inventory, if it is set
     *
     * @param event Event that triggered the action
     * @see #bottomClickAction(InventoryAction)
     */
    public void doBottomClickAction(@NotNull InventoryClickEvent event) {
        if (this.bottomClickAction != null) {
            this.bottomClickAction.doAction(event, this.clone());
        }
    }

    /**
     * Checks if slot is in inventory bounds
     *
     * @param slot Slot to validate
     * @throws IllegalArgumentException If slot is out of inventory size
     */
    public void validateSlot(int slot) throws IllegalArgumentException {
        if (slot < 0 || slot >= this.getSize()) {
            throw new IllegalArgumentException("Slot must be between 0 and " + (this.getSize() - 1));
        }
    }

    /**
     * Creates a clone of this inventory with all the contents copied into it
     *
     * @return Clone of this inventory
     */
    @Override
    public @NotNull CustomInventory clone() {
        try {
            CustomInventory clone = (CustomInventory) super.clone();
            Container newContainer = new CraftInventoryCustom(null, this.getSize(), this.title()).getInventory();
            Field inventoryField = CraftInventory.class.getDeclaredField("inventory");

            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            unsafe.putObject(clone, unsafe.objectFieldOffset(inventoryField), newContainer);

            clone.buttons = new HashMap<>(this.buttons);
            clone.setContents(this.getContents());

            return clone;
        } catch (CloneNotSupportedException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
