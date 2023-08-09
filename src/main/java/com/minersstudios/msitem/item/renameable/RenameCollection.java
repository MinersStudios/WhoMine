package com.minersstudios.msitem.item.renameable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.minersstudios.mscore.util.ChatUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RenameCollection {
    private final String key;
    private String mainName;
    private ItemStack mainItem;
    private final Queue<String> renames;
    private final Queue<ItemStack> items;

    public RenameCollection(@NotNull String key) {
        this.key = key;
        this.renames = new ConcurrentLinkedQueue<>();
        this.items = new ConcurrentLinkedQueue<>();
    }

    public RenameCollection(
            @NotNull String key,
            @NotNull Collection<String> renames,
            @NotNull Collection<ItemStack> items
    ) {
        this.key = key;
        this.renames = new ConcurrentLinkedQueue<>();
        this.items = new ConcurrentLinkedQueue<>();

        this.addAllRenames(renames);
        this.addAllItems(items);
    }

    public @NotNull @UnmodifiableView Collection<String> renames() {
        return Collections.unmodifiableCollection(this.renames);
    }

    public @NotNull @UnmodifiableView Collection<ItemStack> items() {
        return Collections.unmodifiableCollection(this.items);
    }

    public @NotNull @Unmodifiable Set<Map.Entry<String, ItemStack>> entrySet() {
        var entrySet = new ImmutableSet.Builder<Map.Entry<String, ItemStack>>();

        for (var rename : this.renames) {
            for (var item : this.items) {
                entrySet.add(new AbstractMap.SimpleImmutableEntry<>(rename, item));
            }
        }

        return entrySet.build();
    }

    public @NotNull String getKey() {
        return this.key;
    }

    public String getMainName() {
        return this.mainName;
    }

    public ItemStack getMainItem() {
        return this.mainItem == null
                ? null
                : this.mainItem.clone();
    }

    public void setMainItem(@Nullable ItemStack item) throws IllegalArgumentException {
        if (item == null) {
            this.mainItem = null;
            return;
        }

        Preconditions.checkArgument(!item.getType().isAir(), "Item cannot be air (in " + this.key + ")");

        this.mainItem = new ItemStack(item);
        ItemMeta meta = item.getItemMeta();

        if (this.mainName != null) {
            meta.displayName(ChatUtils.createDefaultStyledText(this.mainName));
        }

        meta.getPersistentDataContainer().set(
                RenameableItemRegistry.RENAMEABLE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.key
        );
        this.mainItem.setItemMeta(meta);
    }

    public boolean addRename(@NotNull String rename) {
        if (this.renames.isEmpty()) {
            this.setMainName(rename);
        }

        return this.renames.add(rename.toLowerCase(Locale.ROOT));
    }

    public boolean addAllRenames(@NotNull Collection<String> renames) {
        boolean added = false;

        for (var rename : renames) {
            added |= this.addRename(rename);
        }

        return added;
    }

    public boolean removeRename(@Nullable String rename) {
       return !StringUtils.isBlank(rename)
               && this.renames.remove(rename.toLowerCase(Locale.ROOT));
    }

    public boolean addItem(@NotNull ItemStack item) throws IllegalArgumentException {
        Preconditions.checkArgument(!item.getType().isAir(), "Item cannot be air (in " + this.key + ")");

        if (
                this.items.isEmpty()
                && this.mainItem == null
        ) {
            this.setMainItem(item);
        }

        return this.items.add(item);
    }

    public boolean addAllItems(@NotNull Collection<ItemStack> items) {
        boolean added = false;

        for (var item : items) {
            added |= this.addItem(item);
        }

        return added;
    }

    @Contract("null -> false")
    public boolean removeItem(@Nullable ItemStack item) {
        return item != null
                && this.items.remove(item);
    }

    public boolean addAll(@NotNull RenameCollection that) {
        return this.addAllRenames(that.renames)
                | this.addAllItems(that.items);
    }

    @Contract("null -> false")
    public boolean containsRename(@Nullable String rename) {
        return !StringUtils.isBlank(rename)
                && this.renames.contains(rename.toLowerCase(Locale.ROOT));
    }

    public boolean containsAllRenames(@NotNull Collection<String> renames) {
        return this.renames.containsAll(renames);
    }

    @Contract("null -> false")
    public boolean containsItem(@Nullable ItemStack item) {
        return this.items.contains(item);
    }

    public boolean containsAllItems(@NotNull Collection<ItemStack> items) {
        return this.items.containsAll(items);
    }

    public boolean isEmptyRenames() {
        return this.renames.isEmpty();
    }

    public boolean isEmptyItems() {
        return this.items.isEmpty();
    }

    public boolean isInitialized() {
        return this.mainItem != null && this.mainName != null;
    }

    @Override
    public @NotNull String toString() {
        return "RenameCollection{" +
                "renames=" + this.renames +
                ", items=" + this.items +
                '}';
    }

    @Contract("null -> false")
    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj
                || (obj instanceof RenameCollection that
                && this.renames.containsAll(that.renames)
                && this.items.containsAll(that.items));
    }

    public void clearRenames() {
        this.renames.clear();
    }

    public void clearItems() {
        this.items.clear();
    }

    public void clear() {
        this.clearRenames();
        this.clearItems();
    }

    @Contract("null, null -> null")
    public @Nullable ItemStack craftRenamed(
            @Nullable ItemStack item,
            @Nullable String rename
    ) {
        if (
                item == null
                || !this.isInitialized()
                || item.getType().isAir()
                || StringUtils.isBlank(rename)
        ) return null;

        ItemStack newItem = new ItemStack(item);
        ItemMeta meta = item.getItemMeta();
        ItemMeta mainMeta = this.mainItem.getItemMeta();

        meta.lore(mainMeta.lore());
        meta.setCustomModelData(mainMeta.getCustomModelData());
        meta.displayName(ChatUtils.createDefaultStyledText(rename));
        meta.getPersistentDataContainer().set(
                RenameableItemRegistry.RENAMEABLE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.key
        );
        newItem.setItemMeta(meta);

        return newItem;
    }

    private void setMainName(@Nullable String name) {
        if (StringUtils.isBlank(name)) {
            this.mainName = null;
            return;
        }

        String normalizedName = ChatUtils.normalize(name);

        if (this.mainItem != null) {
            ItemMeta meta = this.mainItem.getItemMeta();

            meta.displayName(ChatUtils.createDefaultStyledText(normalizedName));
            this.mainItem.setItemMeta(meta);
        }

        this.mainName = normalizedName;
    }
}
