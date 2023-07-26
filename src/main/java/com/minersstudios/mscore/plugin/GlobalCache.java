package com.minersstudios.mscore.plugin;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.collections.DualMap;
import com.minersstudios.mscore.collections.HashDualMap;
import com.minersstudios.mscore.packet.collection.PacketListenersMap;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msitem.items.CustomItem;
import com.minersstudios.msitem.items.RenameableItem;
import org.bukkit.inventory.Recipe;

import java.util.*;

/**
 * Cache for all custom data
 */
public final class GlobalCache {
    public final PacketListenersMap packetListenerMap = new PacketListenersMap();
    public final Set<String> onlyPlayerCommandSet = new HashSet<>();
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