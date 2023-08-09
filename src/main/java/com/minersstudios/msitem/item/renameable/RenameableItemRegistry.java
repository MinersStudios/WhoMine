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

public final class RenameableItemRegistry {
    public static final NamespacedKey RENAMEABLE_NAMESPACED_KEY = new NamespacedKey(CustomItemType.NAMESPACE, "renameable");

    private static final Map<String, RenameableItem> KEY_MAP = new ConcurrentHashMap<>();
    private static final Map<RenameEntry, String> RENAME_ENTRY_MAP = new ConcurrentHashMap<>();

    @Contract(value = " -> fail")
    private RenameableItemRegistry() {
        throw new AssertionError("Utility class");
    }

    public static @NotNull @UnmodifiableView Set<String> keySet() {
        return Collections.unmodifiableSet(KEY_MAP.keySet());
    }

    public static @NotNull @UnmodifiableView Set<RenameEntry> renameEntrySet() {
        return Collections.unmodifiableSet(RENAME_ENTRY_MAP.keySet());
    }

    public static @NotNull @UnmodifiableView Collection<RenameableItem> renameableItems() {
        return Collections.unmodifiableCollection(KEY_MAP.values());
    }

    public static @NotNull Optional<RenameableItem> fromKey(@Nullable String key) {
        return StringUtils.isBlank(key)
                ? Optional.empty()
                : Optional.ofNullable(KEY_MAP.get(key.toLowerCase(Locale.ENGLISH)));
    }

    public static @NotNull Optional<RenameableItem> fromRenameEntry(@Nullable RenameEntry renameEntry) {
        return renameEntry == null
                ? Optional.empty()
                : fromKey(
                        RENAME_ENTRY_MAP.entrySet().stream().parallel()
                        .filter(entry -> entry.getKey().equals(renameEntry))
                        .map(Map.Entry::getValue)
                        .findAny()
                        .orElse(null)
                );
    }

    public static @NotNull Optional<RenameableItem> fromRename(
            @Nullable String rename,
            @Nullable ItemStack itemStack
    ) {
        return fromRenameEntry(RenameEntry.create(rename, itemStack));
    }

    public static @NotNull Optional<RenameableItem> fromItemStack(@Nullable ItemStack itemStack) {
        if (itemStack == null) return Optional.empty();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return Optional.empty();
        return fromKey(
                itemMeta.getPersistentDataContainer().get(RENAMEABLE_NAMESPACED_KEY, PersistentDataType.STRING)
        );
    }

    @Contract("null -> false")
    public static boolean containsKey(@Nullable String key) {
        return !StringUtils.isBlank(key)
                && KEY_MAP.containsKey(key.toLowerCase(Locale.ENGLISH));
    }

    @Contract("null -> false")
    public static boolean containsRenameEntry(@Nullable RenameEntry renameEntry) {
        return renameEntry != null
                && RENAME_ENTRY_MAP.containsKey(renameEntry);
    }

    @Contract("null, null -> false")
    public static boolean containsRename(
            @Nullable String rename,
            @Nullable ItemStack itemStack
    ) {
        return containsRenameEntry(RenameEntry.create(rename, itemStack));
    }

    public static boolean containsAnyFromSet(@Nullable RenameCollection renameCollection) {
        return renameCollection != null
                && renameCollection.entrySet().stream().parallel()
                .anyMatch(rename -> containsRename(rename.getKey(), rename.getValue()));
    }

    @Contract("null -> false")
    public static boolean containsRenameableItem(@Nullable RenameableItem renameableItem) {
        return renameableItem != null
                && KEY_MAP.containsValue(renameableItem);
    }

    @Contract("null -> false")
    public static boolean isRenameableItem(@Nullable ItemStack itemStack) {
        return fromItemStack(itemStack).isPresent();
    }

    public static boolean isEmpty() {
        return KEY_MAP.isEmpty();
    }

    public static int keysSize() {
        return KEY_MAP.size();
    }

    public static int renameEntriesSize() {
        return RENAME_ENTRY_MAP.size();
    }

    public static void register(@NotNull RenameableItem renameableItem) throws IllegalArgumentException {
        String key = renameableItem.getKey();
        RenameCollection renameCollection = renameableItem.getRenames();

        Preconditions.checkArgument(!containsKey(key), "Key " + key + " is already registered");
        Preconditions.checkArgument(!containsAnyFromSet(renameCollection), "Renames for key " + key + " are already registered");

        KEY_MAP.put(key, renameableItem);
        renameCollection.entrySet().forEach(
                rename -> RENAME_ENTRY_MAP.put(RenameEntry.create(rename.getKey(), rename.getValue()), key)
        );
    }

    public static void unregister(@NotNull RenameableItem renameableItem) throws IllegalArgumentException {
        String key = renameableItem.getKey();
        RenameCollection renameCollection = renameableItem.getRenames();

        Preconditions.checkArgument(containsKey(key), "Key " + key + " is not registered");
        Preconditions.checkArgument(containsAnyFromSet(renameCollection), "Renames for key " + key + " are not registered");

        KEY_MAP.remove(key);
        renameCollection.entrySet().forEach(
                rename -> RENAME_ENTRY_MAP.remove(RenameEntry.create(rename.getKey(), rename.getValue()))
        );
    }

    public static void unregisterAll() {
        KEY_MAP.clear();
        RENAME_ENTRY_MAP.clear();
    }
}
