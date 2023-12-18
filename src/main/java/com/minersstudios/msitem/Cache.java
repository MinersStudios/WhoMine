package com.minersstudios.msitem;

import com.minersstudios.mscore.plugin.cache.PluginCache;
import com.minersstudios.msitem.api.renameable.RenameableItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache with all the data that needs to be stored
 */
public final class Cache extends PluginCache<MSItem> {
    private Map<Player, EquipmentSlot> dosimeterPlayers;
    private List<RenameableItem> renameableMenuItems;

    /**
     * Cache constructor
     *
     * @param plugin The plugin that owns this cache
     */
    public Cache(final @NotNull MSItem plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        this.dosimeterPlayers = new ConcurrentHashMap<>();
        this.renameableMenuItems = new ArrayList<>();
    }

    @Override
    public void onUnload() {
        this.dosimeterPlayers = null;
        this.renameableMenuItems = null;
    }

    public @UnknownNullability Map<Player, EquipmentSlot> getDosimeterPlayers() {
        return this.dosimeterPlayers;
    }

    public @UnknownNullability List<RenameableItem> getRenameableMenuItems() {
        return this.renameableMenuItems;
    }
}
