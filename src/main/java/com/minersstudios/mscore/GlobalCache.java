package com.minersstudios.mscore;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.collections.DualMap;
import com.minersstudios.mscore.collections.HashDualMap;
import com.minersstudios.mscore.packet.PacketListenersMap;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msitem.items.CustomItem;
import com.minersstudios.msitem.items.RenameableItem;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cache for all custom data
 */
public final class GlobalCache {
    public final PacketListenersMap packetListenersMap = new PacketListenersMap();
    public final DualMap<String, Integer, CustomDecorData> customDecorMap = new HashDualMap<>();
    public final List<Recipe> customDecorRecipes = new ArrayList<>();
    public final DualMap<String, Integer, CustomBlockData> customBlockMap = new HashDualMap<>();
    public final Map<Integer, CustomBlockData> cachedNoteBlockData = new HashMap<>();
    public final List<Recipe> customBlockRecipes = new ArrayList<>();
    public final DualMap<String, Integer, CustomItem> customItemMap = new HashDualMap<>();
    public final DualMap<String, Integer, RenameableItem> renameableItemMap = new HashDualMap<>();
    public final List<RenameableItem> renameableItemsMenu = new ArrayList<>();
    public final List<Recipe> customItemRecipes = new ArrayList<>();
}
