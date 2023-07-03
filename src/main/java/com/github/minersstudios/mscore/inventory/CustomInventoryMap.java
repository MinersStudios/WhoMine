package com.github.minersstudios.mscore.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a map of custom inventories.
 * This class is used to store custom inventories and associate them with a key.
 * <br>
 * Use {@link #get(String)} to get custom inventory by key.
 * Use {@link #put(String, CustomInventory)} to put custom inventory with key.
 *
 * @see CustomInventory
 * @see ListedInventory
 * @see ElementListedInventory
 */
public class CustomInventoryMap {
    private final @NotNull Map<String, CustomInventory> map = new ConcurrentHashMap<>();

    /**
     * @param key Custom inventory key
     * @return Custom inventory associated with key,
     *         or null if there is no custom inventory for the key
     */
    public @Nullable CustomInventory get(@NotNull String key) {
        CustomInventory customInventory = this.map.get(key);
        return customInventory instanceof ListedInventory listedInventory
                ? listedInventory.getPage(0)
                : customInventory;
    }

    /**
     * @param key             Custom inventory key
     * @param customInventory The custom inventory
     * @return The previous custom inventory associated with key,
     *         or null if there was no custom inventory for key
     */
    public @Nullable CustomInventory put(
            @NotNull String key,
            @NotNull CustomInventory customInventory
    ) {
        return this.map.put(key.toLowerCase(Locale.ROOT), customInventory);
    }

    /**
     * @param key Custom inventory key
     * @return The previous custom inventory associated with key, or null if there was no custom inventory for key
     */
    public @Nullable CustomInventory remove(@NotNull String key) {
        return this.map.remove(key.toLowerCase(Locale.ROOT));
    }

    /**
     * @return The number of custom inventories in this map
     */
    public int size() {
        return this.map.size();
    }

    /**
     * @return True if this map contains no custom inventories
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * @param key Custom inventory key
     * @return True if this map contains the specified key
     */
    public boolean containsKey(@Nullable String key) {
        return this.map.containsKey(key);
    }

    /**
     * @param customInventory The custom inventory
     * @return True if this map contains the specified custom inventory
     */
    public boolean containsInventory(@NotNull CustomInventory customInventory) {
        return this.map.containsValue(customInventory);
    }

    /**
     * Removes all custom inventories from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * @return An unmodifiable view of the keys contained in this map
     */
    public @NotNull @UnmodifiableView Set<String> keySet() {
        return Set.copyOf(this.map.keySet());
    }

    /**
     * @return An unmodifiable view of the custom inventories contained in this map
     */
    public @NotNull @UnmodifiableView Collection<CustomInventory> customInventories() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    /**
     * @return An unmodifiable view of the mappings contained in this map
     */
    public @NotNull @UnmodifiableView Set<Map.Entry<String, CustomInventory>> entrySet() {
        return Set.copyOf(this.map.entrySet());
    }
}
