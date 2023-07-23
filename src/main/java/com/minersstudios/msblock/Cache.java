package com.minersstudios.msblock;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.collections.ConcurrentHashDualMap;
import com.minersstudios.mscore.collections.DualMap;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public final class Cache {
    public final List<CustomBlockData> recipeBlocks = new ArrayList<>();
    public final Map<Player, Double> steps = new HashMap<>();
    public final Set<Player> farAway = new HashSet<>();
    public final DualMap<Block, Player, Integer> blocks = new ConcurrentHashDualMap<>();
}
