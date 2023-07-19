package com.minersstudios.mscore.inventory;

import com.minersstudios.mscore.inventory.actions.ButtonClickAction;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Button builder class.
 * Button can use {@link ButtonClickAction} to perform action when clicked
 */
public class InventoryButton {
    private @Nullable ItemStack item;
    private @Nullable ButtonClickAction clickAction;

    /**
     * Empty button constructor
     */
    private InventoryButton() {}

    /**
     * Creates new instance of {@link InventoryButton} with null values
     *
     * @return New instance of {@link InventoryButton}
     */
    @Contract(value = " -> new")
    public static @NotNull InventoryButton create() {
        return new InventoryButton();
    }

    /**
     * Gets item to be displayed on button
     *
     * @return Item to be displayed on button
     */
    public @Nullable ItemStack item() {
        return this.item;
    }

    /**
     * Sets item to be displayed on button
     *
     * @param item New item
     * @return This instance
     */
    public @NotNull InventoryButton item(@Nullable ItemStack item) {
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
    public @NotNull InventoryButton clickAction(@Nullable ButtonClickAction clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    /**
     * Plays click sound to player
     *
     * @param player Player to whom the sound will be played
     */
    public static void playClickSound(@NotNull Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.5f, 1.0f);
    }

    /**
     * Performs click action when button is clicked
     *
     * @param event           Event that triggered the action
     * @param customInventory Custom inventory that is involved in this event
     */
    public void doClickAction(
            @NotNull InventoryClickEvent event,
            @NotNull CustomInventory customInventory
    ) {
        if (this.clickAction != null) {
            this.clickAction.doAction(event, customInventory.clone());
        }
    }
}
