package com.minersstudios.msblock.customblock.file;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Class representing the drop settings for a custom block. This
 * class holds information about the item and experience dropped
 * by the custom block when it is broken or destroyed.
 */
public class DropSettings {
    public ItemStack item;
    private int experience;

    /**
     * Constructs the DropSettings with the specified item
     * and experience values
     *
     * @param item       The ItemStack representing the item
     *                   dropped by the custom block when it
     *                   is broken
     * @param experience The amount of experience points
     *                   dropped by the custom block when it
     *                   is broken
     */
    public DropSettings(
            final @NotNull ItemStack item,
            final int experience
    ) {
        this.item = item;
        this.experience = experience;
    }

    /**
     * @return The ItemStack representing the item dropped
     *         by the custom block when it is broken
     */
    public @NotNull ItemStack getItem() {
        return this.item.clone();
    }

    /**
     * Sets the ItemStack representing the item dropped by
     * the custom block when it is broken
     *
     * @param item A new item to drop
     */
    public void setItem(final @NotNull ItemStack item) {
        this.item = item;
    }

    /**
     * @return The amount of experience points dropped by
     *         the custom block when it is broken
     */
    public int getExperience() {
        return this.experience;
    }

    /**
     * Sets the amount of experience points dropped by the
     * custom block when it is broken
     *
     * @param experience The new amount of experience points
     */
    public void setExperience(final int experience) {
        this.experience = experience;
    }
}
