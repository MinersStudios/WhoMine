package com.minersstudios.msitem.item.renameable;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RenameEntry {
    private final String rename;
    private final ItemStack item;

    public RenameEntry(
            @NotNull String rename,
            @NotNull ItemStack item
    ) throws IllegalArgumentException {
        Preconditions.checkArgument(!StringUtils.isBlank(rename), "Rename cannot be null or blank");
        Preconditions.checkArgument(!item.getType().isAir(), "Item cannot be air");

        this.rename = rename;
        this.item = item;
    }

    public static @Nullable RenameEntry create(
            @Nullable String rename,
            @Nullable ItemStack item
    ) {
        return StringUtils.isBlank(rename)
                || item == null
                || item.getType().isAir()
                ? null
                : new RenameEntry(rename, item);
    }

    public @NotNull String getRename() {
        return this.rename;
    }

    public @NotNull ItemStack getItem() {
        return this.item;
    }

    public boolean isSimilarRename(@Nullable String rename) {
        return StringUtils.startsWithIgnoreCase(rename, this.getRename());
    }

    public boolean isSimilarItem(@Nullable ItemStack item) {
        if (item == null) return false;

        Material type = item.getType();
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        PersistentDataContainer thisContainer = this.item.getItemMeta().getPersistentDataContainer();

        for (var namespacedKey : container.getKeys()) {
            if (namespacedKey.equals(RenameableItemRegistry.RENAMEABLE_NAMESPACED_KEY)) continue;

            String typeString = container.get(namespacedKey, PersistentDataType.STRING);
            String thisTypeString = thisContainer.get(namespacedKey, PersistentDataType.STRING);

            if (!Objects.equals(thisTypeString, typeString)) return false;
        }

        return type == this.item.getType();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof RenameEntry that
                && this.isSimilarRename(that.rename)
                && this.isSimilarItem(that.item);
    }

    @Override
    public @NotNull String toString() {
        return "RenameEntry{" +
                "rename='" + this.rename + '\'' +
                ", item=" + this.item +
                '}';
    }
}
