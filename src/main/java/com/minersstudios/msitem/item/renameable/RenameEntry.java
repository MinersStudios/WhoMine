package com.minersstudios.msitem.item.renameable;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The RenameEntry class represents a pair of a renaming
 * string and an ItemStack, used in the context of renaming
 * items. It encapsulates a renaming operation along with
 * the associated item, providing methods to manage and
 * compare rename entries.
 */
public class RenameEntry {
    private final String rename;
    private final ItemStack item;

    /**
     * Constructs a RenameEntry with the given renaming string
     * and ItemStack
     *
     * @param rename The renaming string for the entry
     * @param item   The ItemStack associated with the entry
     * @throws IllegalArgumentException If the renaming string is null
     *                                  or blank, or if the ItemStack's
     *                                  material is air
     */
    public RenameEntry(
            final @NotNull String rename,
            final @NotNull ItemStack item
    ) throws IllegalArgumentException {
        Preconditions.checkArgument(StringUtils.isNotBlank(rename), "Rename cannot be null or blank");
        Preconditions.checkArgument(!item.getType().isAir(), "Item cannot be air");

        this.rename = rename;
        this.item = item;
    }

    /**
     * Creates a RenameEntry instance if both the renaming string
     * and the ItemStack are valid
     *
     * @param rename The renaming string for the entry
     * @param item   The ItemStack associated with the entry
     * @return A RenameEntry instance if valid, or null if either
     *         parameter is invalid
     */
    public static @Nullable RenameEntry create(
            final @Nullable String rename,
            final @Nullable ItemStack item
    ) {
        return StringUtils.isBlank(rename)
                || item == null
                || item.getType().isAir()
                ? null
                : new RenameEntry(rename, item);
    }

    /**
     * @return The renaming string of the entry
     */
    public @NotNull String getRename() {
        return this.rename;
    }

    /**
     * @return The ItemStack associated with the entry
     */
    public @NotNull ItemStack getItem() {
        return this.item;
    }

    /**
     * Checks if the given renaming string is similar to the
     * renaming string of this entry (case-insensitive). Renaming
     * strings are considered similar if the given renaming string
     * starts with this entry's renaming string.
     *
     * @param rename The renaming string to compare
     * @return True if the given renaming string is similar
     *         to this entry's renaming
     */
    public boolean isSimilarRename(final @Nullable String rename) {
        return StringUtils.startsWithIgnoreCase(rename, this.getRename());
    }

    /**
     * Checks if the given ItemStack is similar to the ItemStack
     * of this entry. Items are considered similar if they have
     * the same material and matching persistent data.
     *
     * @param item The ItemStack to compare
     * @return True if the given ItemStack is similar to this
     *         entry's ItemStack
     */
    public boolean isSimilarItem(final @Nullable ItemStack item) {
        if (item == null) return false;

        final Material type = item.getType();
        final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        final PersistentDataContainer thisContainer = this.item.getItemMeta().getPersistentDataContainer();

        for (final var namespacedKey : container.getKeys()) {
            if (namespacedKey.equals(RenameableItemRegistry.RENAMEABLE_NAMESPACED_KEY)) continue;

            final String typeString = container.get(namespacedKey, PersistentDataType.STRING);
            final String thisTypeString = thisContainer.get(namespacedKey, PersistentDataType.STRING);

            if (!Objects.equals(thisTypeString, typeString)) return false;
        }

        return type == this.item.getType();
    }

    /**
     * Compares this RenameEntry to another object for equality
     *
     * @param obj The object to compare to
     * @return True if the object is a RenameEntry with similar
     *         renaming and ItemStack
     * @see #isSimilarRename(String)
     * @see #isSimilarItem(ItemStack)
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj instanceof final RenameEntry that
                && this.isSimilarRename(that.rename)
                && this.isSimilarItem(that.item);
    }

    /**
     * @return A string representation of the rename entry
     */
    @Override
    public @NotNull String toString() {
        return "RenameEntry{" +
                "rename='" + this.rename + '\'' +
                ", item=" + this.item +
                '}';
    }
}
