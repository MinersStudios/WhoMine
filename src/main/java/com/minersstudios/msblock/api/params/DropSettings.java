package com.minersstudios.msblock.api.params;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Class representing the drop settings for a custom block. This class holds
 * information about the item and experience dropped by the custom block when
 * it is broken or destroyed.
 */
@Immutable
public final class DropSettings {
    private final ItemStack item;
    private final int experience;

    /**
     * Constructs the DropSettings with the specified item and experience values
     *
     * @param item       The ItemStack representing the item dropped by the
     *                   custom block when it is broken
     * @param experience The number of experience points dropped by the custom
     *                   block when it is broken
     */
    public DropSettings(
            final @NotNull ItemStack item,
            final int experience
    ) {
        this.item = item;
        this.experience = experience;
    }

    /**
     * @return The ItemStack representing the item dropped by the custom block
     *         when it is broken
     */
    public @NotNull ItemStack getItem() {
        return this.item.clone();
    }

    /**
     * @return The number of experience points dropped by the custom block when
     *         it is broken
     */
    public int getExperience() {
        return this.experience;
    }
}
