package com.minersstudios.msitem;

import com.minersstudios.msitem.api.renameable.RenameableItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache with all the data that needs to be stored
 */
public final class Cache {
    public final Map<Player, EquipmentSlot> dosimeterPlayers = new ConcurrentHashMap<>();
    public final List<RenameableItem> renameableItemsMenu = new ArrayList<>();
}
