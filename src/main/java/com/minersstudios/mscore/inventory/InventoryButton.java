package com.minersstudios.mscore.inventory;

import com.minersstudios.mscore.inventory.action.ButtonClickAction;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Button builder class.
 * Button can use {@link ButtonClickAction} to perform action when clicked
 */
public class InventoryButton implements Cloneable {
    private ItemStack item;
    private ButtonClickAction clickAction;

    private static final ItemStack DEFAULT_ITEM = new ItemStack(Material.AIR);

    /**
     * Creates new button with {@link #DEFAULT_ITEM}
     * and no click action
     */
    public InventoryButton() {
        this.item = DEFAULT_ITEM.clone();
        this.clickAction = null;
    }

    /**
     * Creates new button with specified item and click action
     *
     * @param item        Item to be displayed on button
     * @param clickAction Click action to be performed when button
     *                    is clicked
     */
    public InventoryButton(
            final @Nullable ItemStack item,
            final @Nullable ButtonClickAction clickAction
    ) {
        this.item = item == null
                ? DEFAULT_ITEM.clone()
                : item;
        this.clickAction = clickAction;
    }

    /**
     * Gets item to be displayed on button
     *
     * @return Item to be displayed on button
     */
    public @NotNull ItemStack item() {
        return this.item;
    }

    /**
     * Sets item to be displayed on button
     *
     * @param item New item
     * @return This instance
     */
    public @NotNull InventoryButton item(final @Nullable ItemStack item) {
        this.item = item;
        return this;
    }

    /**
     * Gets click action to be performed when button is clicked
     *
     * @return Click action to be performed when button is clicked
     * @see ButtonClickAction
     */
    public @Nullable ButtonClickAction clickAction() {
        return this.clickAction;
    }

    /**
     * Sets click action to be performed when button is clicked
     *
     * @param clickAction New click action
     * @return This instance
     * @see ButtonClickAction
     */
    public @NotNull InventoryButton clickAction(final @Nullable ButtonClickAction clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    /**
     * Plays click sound to player
     *
     * @param player Player to whom the sound will be played
     */
    public static void playClickSound(final @NotNull Player player) {
        player.playSound(
                player.getLocation(),
                Sound.UI_BUTTON_CLICK,
                SoundCategory.MASTER,
                0.5f,
                1.0f
        );
    }

    /**
     * Performs click action when button is clicked
     *
     * @param event           Event that triggered the action
     * @param customInventory Custom inventory that is involved
     *                        in this event
     */
    public void doClickAction(
            final @NotNull InventoryClickEvent event,
            final @NotNull CustomInventory customInventory
    ) {
        if (this.clickAction != null) {
            this.clickAction.doAction(event, customInventory.clone());
        }
    }

    /**
     * Creates a clone of this button
     *
     * @return Cloned instance of this button
     */
    @Override
    public @NotNull InventoryButton clone() {
        try {
            final InventoryButton clone = (InventoryButton) super.clone();
            clone.item = this.item.clone();
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError("An error occurred while cloning '" + this + "'", e);
        }
    }
}
