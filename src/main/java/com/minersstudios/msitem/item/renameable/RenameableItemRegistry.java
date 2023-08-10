package com.minersstudios.msitem.item.renameable;

import com.google.common.base.Preconditions;
import com.minersstudios.msitem.item.CustomItemType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The RenameableItemRegistry class is responsible for managing and storing
 * renameable item data. It provides various methods to register, unregister,
 * and retrieve renameable items based on different criteria, such as keys,
 * rename entries, or item stacks.
 * <p>
 * The RenameableItemRegistry uses two concurrent maps, one to store items
 * associated with their lowercase keys and another to store rename entries
 * associated with their keys. The keys are used as identifiers for the items,
 * and rename entries are used to look up items based on rename values and item
 * stacks.
 * <p>
 * Make sure to use the provided methods and their respective Optional return
 * types to handle cases where the desired renameable item data might not be
 * present.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Register a renameable item
 * RenameableItemRegistry.register(renameableItem);
 *
 * // Retrieve renameable item using key
 * Optional<RenameableItem> renameableItem = RenameableItemRegistry.fromKey("my_item");
 * if (renameableItem.isPresent()) {
 *     // Renameable item found, do something with it
 *     RenameableItem item = renameableItem.get();
 * }
 *
 * // Check if an item stack is a renameable item
 * ItemStack itemStack = ...;
 * if (RenameableItemRegistry.isRenameableItem(itemStack)) {
 *     // Handle the case when the item stack is a renameable item
 * }
 * }</pre>
 */
public final class RenameableItemRegistry {
    public static final NamespacedKey RENAMEABLE_NAMESPACED_KEY = new NamespacedKey(CustomItemType.NAMESPACE, "renameable");

    private static final Map<String, RenameableItem> KEY_MAP = new ConcurrentHashMap<>();
    private static final Map<RenameEntry, String> RENAME_ENTRY_MAP = new ConcurrentHashMap<>();

    @Contract(value = " -> fail")
    private RenameableItemRegistry() {
        throw new AssertionError("Utility class");
    }

    /**
     * @return An unmodifiable view of the keys of all registered
     *         renameable items
     * @see #KEY_MAP
     */
    public static @NotNull @UnmodifiableView Set<String> keySet() {
        return Collections.unmodifiableSet(KEY_MAP.keySet());
    }

    /**
     * @return An unmodifiable view of the rename entries of all
     *         registered renameable items
     * @see #RENAME_ENTRY_MAP
     */
    public static @NotNull @UnmodifiableView Set<RenameEntry> renameEntrySet() {
        return Collections.unmodifiableSet(RENAME_ENTRY_MAP.keySet());
    }

    /**
     * @return An unmodifiable view of all registered renameable
     *         items
     * @see #KEY_MAP
     */
    public static @NotNull @UnmodifiableView Collection<RenameableItem> renameableItems() {
        return Collections.unmodifiableCollection(KEY_MAP.values());
    }

    /**
     * Gets the {@link RenameableItem} from the given key. It will
     * get the item from the {@link #KEY_MAP}.
     *
     * @param key The key to get the {@link RenameableItem} from
     * @return An {@link Optional} containing the {@link RenameableItem}
     *         or an {@link Optional#empty()} if the given key is not
     *         associated with any renameable item
     * @see #KEY_MAP
     */
    public static @NotNull Optional<RenameableItem> fromKey(@Nullable String key) {
        return StringUtils.isBlank(key)
                ? Optional.empty()
                : Optional.ofNullable(KEY_MAP.get(key.toLowerCase(Locale.ENGLISH)));
    }

    /**
     * Gets the {@link RenameableItem} from the given rename entry. It
     * will get the item from the {@link #RENAME_ENTRY_MAP}
     *
     * @param renameEntry The rename entry to get the {@link RenameableItem}
     *                    from
     * @return An {@link Optional} containing the {@link RenameableItem}
     *         or an {@link Optional#empty()} if the given rename entry is
     *         not associated with any renameable item
     * @see #RENAME_ENTRY_MAP
     */
    public static @NotNull Optional<RenameableItem> fromRenameEntry(@Nullable RenameEntry renameEntry) {
        if (renameEntry == null) return Optional.empty();

        for (var entry : RENAME_ENTRY_MAP.entrySet()) {
            if (entry.getKey().equals(renameEntry)) return fromKey(entry.getValue());
        }

        return Optional.empty();
    }

    /**
     * Gets the {@link RenameableItem} from the given rename value and
     * item stack. It will create a {@link RenameEntry} from the given
     * rename value and item stack and get the item from the
     * {@link #RENAME_ENTRY_MAP}.
     *
     * @param rename    The rename value to get the {@link RenameableItem}
     * @param itemStack The item stack to get the {@link RenameableItem}
     * @return An {@link Optional} containing the {@link RenameableItem}
     *         or an {@link Optional#empty()} if the given rename value and
     *         item stack are not associated with any renameable item
     * @see #fromRenameEntry(RenameEntry)
     */
    public static @NotNull Optional<RenameableItem> fromRename(
            @Nullable String rename,
            @Nullable ItemStack itemStack
    ) {
        return fromRenameEntry(RenameEntry.create(rename, itemStack));
    }

    /**
     * Gets the {@link RenameableItem} from the given item stack. It will
     * check the item stack's persistent data container for the
     * {@link #RENAMEABLE_NAMESPACED_KEY}  key, and if it has it, it will
     * get the custom block data from the key by calling
     * {@link #fromKey(String)} method.
     *
     * @param itemStack The item stack to get the {@link RenameableItem} from
     * @return An {@link Optional} containing the {@link RenameableItem},
     *         or an {@link Optional#empty()} if the key from the item stack's
     *         persistent data container is not associated with any renameable
     *         item
     * @see #RENAMEABLE_NAMESPACED_KEY
     * @see #fromKey(String)
     */
    public static @NotNull Optional<RenameableItem> fromItemStack(@Nullable ItemStack itemStack) {
        if (itemStack == null) return Optional.empty();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return Optional.empty();
        return fromKey(
                itemMeta.getPersistentDataContainer().get(RENAMEABLE_NAMESPACED_KEY, PersistentDataType.STRING)
        );
    }

    /**
     * @param key The key to check
     * @return True if the {@link #KEY_MAP} contains the given key
     *         and key is not blank or null (case-insensitive)
     */
    @Contract("null -> false")
    public static boolean containsKey(@Nullable String key) {
        return StringUtils.isNotBlank(key)
                && KEY_MAP.containsKey(key.toLowerCase(Locale.ENGLISH));
    }

    /**
     * @param renameEntry The rename entry to check
     * @return True if the {@link #RENAME_ENTRY_MAP} contains the given
     *         rename entry and rename entry is not null
     */
    @Contract("null -> false")
    public static boolean containsRenameEntry(@Nullable RenameEntry renameEntry) {
        return renameEntry != null
                && RENAME_ENTRY_MAP.containsKey(renameEntry);
    }

    /**
     * @param rename    The rename value to check
     * @param itemStack The item stack to check
     * @return True if the {@link #RENAME_ENTRY_MAP} contains the given
     *         rename value and item stack and rename value and item stack
     *         are not null
     * @see #containsRenameEntry(RenameEntry)
     */
    @Contract("null, null -> false")
    public static boolean containsRename(
            @Nullable String rename,
            @Nullable ItemStack itemStack
    ) {
        return containsRenameEntry(RenameEntry.create(rename, itemStack));
    }

    /**
     * @param renameCollection The rename collection to check
     * @return True if the {@link #RENAME_ENTRY_MAP} contains any of the
     *         given rename collection's entries and rename collection is
     *         not null
     * @see #containsRename(String, ItemStack)
     */
    @Contract("null -> false")
    public static boolean containsAnyFromSet(@Nullable RenameCollection renameCollection) {
        if (renameCollection == null) return false;

        for (var rename : renameCollection.entrySet()) {
            if (containsRenameEntry(rename)) return true;
        }

        return false;
    }

    /**
     * @param renameableItem The renameable item to check
     * @return True if the {@link #KEY_MAP} contains the given renameable
     *         item and renameable item is not null
     */
    @Contract("null -> false")
    public static boolean containsRenameableItem(@Nullable RenameableItem renameableItem) {
        return renameableItem != null
                && KEY_MAP.containsValue(renameableItem);
    }

    /**
     * Checks if the item stack is a renameable item by verifying
     * if it has a valid key associated with it
     *
     * @param itemStack The item stack to check
     * @return True if the item stack is a renameable item
     * @see #fromItemStack(ItemStack)
     */
    @Contract("null -> false")
    public static boolean isRenameableItem(@Nullable ItemStack itemStack) {
        return fromItemStack(itemStack).isPresent();
    }

    /**
     * @return True if the {@link #KEY_MAP} is empty
     */
    public static boolean isEmpty() {
        return KEY_MAP.isEmpty();
    }

    /**
     * @return The size of the {@link #KEY_MAP}
     */
    public static int keysSize() {
        return KEY_MAP.size();
    }

    /**
     * @return The size of the {@link #RENAME_ENTRY_MAP}
     */
    public static int renameEntriesSize() {
        return RENAME_ENTRY_MAP.size();
    }

    /**
     * Registers the given renameable item to the {@link #KEY_MAP} and
     * {@link #RENAME_ENTRY_MAP}
     *
     * @param renameableItem The renameable item to register
     * @throws IllegalArgumentException If the key or any of the rename
     *                                  entries are already registered
     */
    public static void register(@NotNull RenameableItem renameableItem) throws IllegalArgumentException {
        String key = renameableItem.getKey();
        RenameCollection renameCollection = renameableItem.getRenames();

        Preconditions.checkArgument(!containsKey(key), "Key " + key + " is already registered");
        Preconditions.checkArgument(!containsAnyFromSet(renameCollection), "Renames for key " + key + " are already registered");

        KEY_MAP.put(key, renameableItem);
        renameCollection.entrySet().forEach(
                rename -> RENAME_ENTRY_MAP.put(rename, key)
        );
    }

    /**
     * Unregisters the given renameable item from the {@link #KEY_MAP} and
     * {@link #RENAME_ENTRY_MAP}
     *
     * @param renameableItem The renameable item to unregister
     * @throws IllegalArgumentException If the key or any of the rename
     *                                  entries are not registered
     */
    public static void unregister(@NotNull RenameableItem renameableItem) throws IllegalArgumentException {
        String key = renameableItem.getKey();
        RenameCollection renameCollection = renameableItem.getRenames();

        Preconditions.checkArgument(containsKey(key), "Key " + key + " is not registered");
        Preconditions.checkArgument(containsAnyFromSet(renameCollection), "Renames for key " + key + " are not registered");

        KEY_MAP.remove(key);
        renameCollection.entrySet().forEach(RENAME_ENTRY_MAP::remove);
    }

    /**
     * Unregisters all renameable items from the {@link #KEY_MAP} and
     * {@link #RENAME_ENTRY_MAP}
     */
    public static void unregisterAll() {
        KEY_MAP.clear();
        RENAME_ENTRY_MAP.clear();
    }
}
