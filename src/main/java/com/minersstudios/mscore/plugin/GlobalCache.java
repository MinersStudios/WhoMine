package com.minersstudios.mscore.plugin;

import com.minersstudios.mscore.packet.PacketListenersMap;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cache for all custom data.
 * Use {@link MSPlugin#getGlobalCache()} to get cache instance.
 */
public final class GlobalCache {
    public final PacketListenersMap packetListenerMap = new PacketListenersMap();
    public final Set<String> onlyPlayerCommandSet = new HashSet<>();
    public final List<Recipe> customDecorRecipes = new ArrayList<>();
    public final List<Recipe> customItemRecipes = new ArrayList<>();
    public final List<Recipe> customBlockRecipes = new ArrayList<>();
}
