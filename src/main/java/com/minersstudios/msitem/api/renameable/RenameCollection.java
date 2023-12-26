package com.minersstudios.msitem.api.renameable;

import com.google.common.collect.ImmutableSet;
import com.minersstudios.mscore.utility.ChatUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * The RenameCollection class represents a collection of renameable items and
 * their associated renames. It allows managing and manipulating the collection
 * of items and renames, as well as creating and applying the renaming process.
 */
public final class RenameCollection {
    private final String key;
    private final List<String> renames;
    private final List<ItemStack> items;
    private String mainName;
    private ItemStack mainItem;

    /**
     * Constructs a RenameCollection with the given key
     *
     * @param key The key associated with the collection
     */
    public RenameCollection(final @NotNull String key) {
        this.key = key;
        this.renames = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    /**
     * Constructs a RenameCollection with the given key, renames, and items
     *
     * @param key     The key associated with the collection
     * @param renames The initial collection of renames
     * @param items   The initial collection of items
     */
    public RenameCollection(
            final @NotNull String key,
            final @NotNull Collection<String> renames,
            final @NotNull Collection<ItemStack> items
    ) {
        this.key = key;
        this.renames = new ArrayList<>();
        this.items = new ArrayList<>();

        this.addAllRenames(renames);
        this.addAllItems(items);
    }

    /**
     * @return An unmodifiable view of the rename collection
     */
    public @NotNull @UnmodifiableView Collection<String> renames() {
        return Collections.unmodifiableCollection(this.renames);
    }

    /**
     * @return An unmodifiable view of the item collection
     */
    public @NotNull @UnmodifiableView Collection<ItemStack> items() {
        return Collections.unmodifiableCollection(this.items);
    }

    /**
     * @return An unmodifiable set of the entries in the collection
     */
    public @NotNull @Unmodifiable Set<RenameEntry> entrySet() {
        final var entrySet = new ImmutableSet.Builder<RenameEntry>();

        for (final var rename : this.renames) {
            for (final var item : this.items) {
                entrySet.add(new RenameEntry(rename, item));
            }
        }

        return entrySet.build();
    }

    /**
     * @return The key associated with the collection
     */
    public @NotNull String getKey() {
        return this.key;
    }

    /**
     * @return The main name of the collection
     * @throws NullPointerException If the main name is null, it will happen if
     *                              the collection is empty
     */
    public String getMainName() throws NullPointerException {
        return this.mainName;
    }

    /**
     * @return The copy of the main item of the collection, main item is the
     *         item that will be used to show the result of the renaming process
     * @throws NullPointerException If the main item is null, it will happen if
     *                              the collection is empty or if the main item
     *                              was set to null
     */
    public @Nullable ItemStack getMainItem() throws NullPointerException {
        return this.mainItem == null
                ? null
                : this.mainItem.clone();
    }

    /**
     * Sets the main item of the collection, that means the item that will be
     * used to show the result of the renaming process
     *
     * @param item New main item of the collection
     * @throws IllegalArgumentException If the item type is air
     */
    public void setMainItem(final @Nullable ItemStack item) throws IllegalArgumentException {
        if (item == null) {
            this.mainItem = null;
            return;
        }

        if (item.getType().isAir()) {
            throw new IllegalArgumentException("Item cannot be air (in " + this.key + ")");
        }

        this.mainItem = new ItemStack(item);
        final ItemMeta meta = item.getItemMeta();

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

    /**
     * Adds a rename to the collection, if the collection is empty, the rename
     * will be set as the main name of the collection and the main item display
     * name will be updated to match the new main name
     *
     * @param rename The rename to add
     * @return True if this collection changed as a result of the call
     */
    public boolean addRename(final @NotNull String rename) {
        if (this.renames.isEmpty()) {
            this.setMainName(rename);
        }

        return this.renames.add(rename.toLowerCase(Locale.ROOT));
    }

    /**
     * Adds a renaming to the collection, if the collection is empty, the first
     * rename will be set as the main name of the collection
     *
     * @param renames The renames to add
     * @return True if this collection changed as a result of the call
     * @see #addRename(String)
     */
    public boolean addAllRenames(final @NotNull Collection<String> renames) {
        boolean isAdded = false;

        for (final var rename : renames) {
            isAdded |= this.addRename(rename);
        }

        return isAdded;
    }

    /**
     * Removes a rename from the collection, the Main name will not be changed
     *
     * @param rename The rename to remove
     * @return True if this list contained the specified element and the rename
     *         is not null or blank
     */
    public boolean removeRename(final @Nullable String rename) {
       return ChatUtils.isNotBlank(rename)
               && this.renames.remove(rename.toLowerCase(Locale.ROOT));
    }

    /**
     * Adds an item to the collection, if the collection is empty and the main
     * item is null, the item will be set as the main item of the collection
     *
     * @param item The item to add
     * @return True if this collection changed as a result of the call
     * @throws IllegalArgumentException If the item type is air
     */
    public boolean addItem(final @NotNull ItemStack item) throws IllegalArgumentException {
        if (item.getType().isAir()) {
            throw new IllegalArgumentException("Item cannot be air (in " + this.key + ")");
        }

        if (
                this.items.isEmpty()
                && this.mainItem == null
        ) {
            this.setMainItem(item);
        }

        return this.items.add(item);
    }

    /**
     * Adds items to the collection, if the collection is empty and the main
     * item is null, the first item will be set as the main item of the
     * collection
     *
     * @param items The items to add
     * @return True if this collection changed as a result of the call
     * @see #addItem(ItemStack)
     */
    public boolean addAllItems(final @NotNull Collection<ItemStack> items) {
        boolean isAdded = false;

        for (final var item : items) {
            isAdded |= this.addItem(item);
        }

        return isAdded;
    }

    /**
     * Removes an item from the collection, the Main item will not be changed
     *
     * @param item The item to remove
     * @return True if this list contained the specified element and the item is
     *         not null
     */
    @Contract("null -> false")
    public boolean removeItem(final @Nullable ItemStack item) {
        return item != null
                && this.items.remove(item);
    }

    /**
     * Adds all the renames and items from the other rename collection to this
     * one
     *
     * @param that The other rename collection to add
     * @return True if this collection changed as a result of the call
     * @see #addAllRenames(Collection)
     * @see #addAllItems(Collection)
     */
    public boolean addAll(final @NotNull RenameCollection that) {
        return this.addAllRenames(that.renames)
                | this.addAllItems(that.items);
    }

    /**
     * @param rename The rename to check
     * @return True if the collection contains the rename and the rename is not
     *         null or blank
     */
    @Contract("null -> false")
    public boolean containsRename(final @Nullable String rename) {
        return ChatUtils.isNotBlank(rename)
                && this.renames.contains(rename.toLowerCase(Locale.ROOT));
    }

    /**
     * @param renames The renames to check
     * @return True if the collection contains all the renames
     */
    public boolean containsAllRenames(final @NotNull Collection<String> renames) {
        return new HashSet<>(this.renames).containsAll(renames);
    }

    /**
     * @param item The item to check
     * @return True if the collection contains the item and the item stack is
     *         not null
     */
    @Contract("null -> false")
    public boolean containsItem(final @Nullable ItemStack item) {
        return this.items.contains(item);
    }

    /**
     * @param items The items to check
     * @return True if the collection contains all the items
     */
    public boolean containsAllItems(final @NotNull Collection<ItemStack> items) {
        return new HashSet<>(this.items).containsAll(items);
    }

    /**
     * @return True if the rename collection is empty
     */
    public boolean isEmptyRenames() {
        return this.renames.isEmpty();
    }

    /**
     * @return True if the item collection is empty
     */
    public boolean isEmptyItems() {
        return this.items.isEmpty();
    }

    /**
     * @return True if the main item and name are not null
     */
    public boolean isInitialized() {
        return this.mainItem != null && this.mainName != null;
    }

    /**
     * @return A hash code of this rename collection
     */
    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result + this.renames.hashCode();
        result = 31 * result + this.items.hashCode();

        return result;
    }

    /**
     * @param obj The object to compare
     * @return True if the object is the same as this
     * @see #containsAllItems(Collection)
     * @see #containsAllRenames(Collection)
     */
    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof final RenameCollection that
                        && this.containsAllItems(that.items)
                        && this.containsAllRenames(that.renames)
                );
    }

    /**
     * @return A string representation of the rename collection
     */
    @Override
    public @NotNull String toString() {
        return "RenameCollection{" +
                "renames=" + this.renames +
                ", items=" + this.items +
                '}';
    }

    /**
     * Removes all the renames from the collection. The Main name will not be
     * changed.
     */
    public void clearRenames() {
        this.renames.clear();
    }

    /**
     * Removes all the items from the collection. The Main item will not be
     * changed.
     */
    public void clearItems() {
        this.items.clear();
    }

    /**
     * Removes all the renames and items from the collection. The Main item and
     * name will not be changed.
     *
     * @see #clearRenames()
     * @see #clearItems()
     */
    public void clear() {
        this.clearRenames();
        this.clearItems();
    }

    /**
     * Creates a copy of the given item with the rename metadata applied to it.
     * The main item will be used as a base for the metadata.
     *
     * @param item   The item to get the rename for
     * @param rename The rename to get the item for
     * @return The item with the rename metadata applied to it, or null if the
     *         item is null, the rename is null or blank, the collection is not
     *         initialized, or the item is air
     */
    @Contract("null, null -> null")
    public @Nullable ItemStack craftRenamed(
            final @Nullable ItemStack item,
            final @Nullable String rename
    ) {
        if (
                item == null
                || !this.isInitialized()
                || item.getType().isAir()
                || ChatUtils.isBlank(rename)
        ) {
            return null;
        }

        final ItemStack newItem = new ItemStack(item);
        final ItemMeta meta = item.getItemMeta();
        final ItemMeta mainMeta = this.mainItem.getItemMeta();

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

    private void setMainName(final @Nullable String name) {
        if (ChatUtils.isBlank(name)) {
            this.mainName = null;
            return;
        }

        final String normalizedName = ChatUtils.normalize(name);

        if (this.mainItem != null) {
            final ItemMeta meta = this.mainItem.getItemMeta();

            meta.displayName(ChatUtils.createDefaultStyledText(normalizedName));
            this.mainItem.setItemMeta(meta);
        }

        this.mainName = normalizedName;
    }
}
