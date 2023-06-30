package com.github.minersstudios.mscore.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a map of custom inventories.
 * This class is used to store custom inventories and associate them with a key.
 */
@SuppressWarnings("unused")
public class CustomInventoryMap {
    private final @NotNull Map<String, CustomInventory> map = new ConcurrentHashMap<>();

    /**
     * @return Custom inventory map with associated keys
     */
    public @NotNull Map<String, CustomInventory> getMap() {
        return this.map;
    }

    /**
     * @param key Custom inventory key
     * @return Custom inventory associated with key, or null if there is no custom inventory for the key
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
     * @return The previous custom inventory associated with key, or null if there was no custom inventory for key
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
}
