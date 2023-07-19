package com.minersstudios.mscore;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.collections.DualMap;
import com.minersstudios.mscore.collections.HashDualMap;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msitem.items.CustomItem;
import com.minersstudios.msitem.items.RenameableItem;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cache for all custom data
 */
public final class Cache {
    public final @NotNull DualMap<String, Integer, CustomDecorData> customDecorMap = new HashDualMap<>();
    public final @NotNull List<Recipe> customDecorRecipes = new ArrayList<>();
    public final @NotNull DualMap<String, Integer, CustomBlockData> customBlockMap = new HashDualMap<>();
    public final @NotNull Map<Integer, CustomBlockData> cachedNoteBlockData = new HashMap<>();
    public final @NotNull List<Recipe> customBlockRecipes = new ArrayList<>();
    public final @NotNull DualMap<String, Integer, CustomItem> customItemMap = new HashDualMap<>();
    public final @NotNull DualMap<String, Integer, RenameableItem> renameableItemMap = new HashDualMap<>();
    public final @NotNull List<RenameableItem> renameableItemsMenu = new ArrayList<>();
    public final @NotNull List<Recipe> customItemRecipes = new ArrayList<>();
}
