package com.minersstudios.mscore.plugin;

import com.minersstudios.mscore.packet.PacketListenersMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.inventory.Recipe;

import java.util.List;
import java.util.Set;

/**
 * Cache for all custom data.
 * Use {@link MSPlugin#globalCache()} to get cache instance.
 */
public final class GlobalCache {
    public final PacketListenersMap packetListenerMap = new PacketListenersMap();
    public final Set<String> onlyPlayerCommandSet = new ObjectOpenHashSet<>();
    public final List<Recipe> customDecorRecipes = new ObjectArrayList<>();
    public final List<Recipe> customItemRecipes = new ObjectArrayList<>();
    public final List<Recipe> customBlockRecipes = new ObjectArrayList<>();
}
