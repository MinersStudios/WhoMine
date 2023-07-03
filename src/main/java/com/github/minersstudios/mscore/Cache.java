package com.github.minersstudios.mscore;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.collections.DualMap;
import com.github.minersstudios.mscore.inventory.CustomInventoryMap;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msitem.items.CustomItem;
import com.github.minersstudios.msitem.items.RenameableItem;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for all custom data
 */
public final class Cache {
    public final @NotNull DualMap<String, Integer, CustomDecorData> customDecorMap = new DualMap<>();
    public final @NotNull List<Recipe> customDecorRecipes = new ArrayList<>();
    public final @NotNull DualMap<String, Integer, CustomBlockData> customBlockMap = new DualMap<>();
    public final @NotNull Map<Integer, CustomBlockData> cachedNoteBlockData = new HashMap<>();
    public final @NotNull List<Recipe> customBlockRecipes = new ArrayList<>();
    public final @NotNull DualMap<String, Integer, CustomItem> customItemMap = new DualMap<>();
    public final @NotNull DualMap<String, Integer, RenameableItem> renameableItemMap = new DualMap<>();
    public final @NotNull List<RenameableItem> renameableItemsMenu = new ArrayList<>();
    public final @NotNull List<Recipe> customItemRecipes = new ArrayList<>();
    public final @NotNull CustomInventoryMap customInventoryMap = new CustomInventoryMap();
    public final Map<InetAddress, String> timezoneCache = new ConcurrentHashMap<>();
    public final Map<String, UUID> playerUUIDs = new HashMap<>();
}
