package com.minersstudios.mscustoms;

import com.google.gson.JsonElement;
import com.minersstudios.mscore.plugin.cache.PluginCache;
import com.minersstudios.mscustoms.collection.DiggingMap;
import com.minersstudios.mscustoms.collection.StepMap;
import com.minersstudios.mscustoms.custom.block.CustomBlockData;
import com.minersstudios.mscustoms.custom.item.renameable.RenameableItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache with all the data that needs to be stored
 */
public final class Cache extends PluginCache<MSCustoms> {
    private StepMap stepMap;
    private DiggingMap diggingMap;
    private Map<Player, EquipmentSlot> dosimeterPlayers;
    private List<RenameableItem> renameableMenuItems;
    private List<Map.Entry<CustomBlockData, JsonElement>> blockDataRecipes;

    /**
     * Cache constructor
     *
     * @param plugin The plugin that owns this cache
     */
    Cache(final @NotNull MSCustoms plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        this.stepMap = new StepMap();
        this.diggingMap = new DiggingMap();
        this.dosimeterPlayers = new ConcurrentHashMap<>();
        this.renameableMenuItems = new ObjectArrayList<>();
        this.blockDataRecipes = new ObjectArrayList<>();
    }

    @Override
    public void onUnload() {
        this.stepMap = null;
        this.diggingMap = null;
        this.dosimeterPlayers = null;
        this.renameableMenuItems = null;
        this.blockDataRecipes = null;
    }

    public @UnknownNullability StepMap getStepMap() {
        return this.stepMap;
    }

    public @UnknownNullability DiggingMap getDiggingMap() {
        return this.diggingMap;
    }

    public @UnknownNullability Map<Player, EquipmentSlot> getDosimeterPlayers() {
        return this.dosimeterPlayers;
    }

    public @UnknownNullability List<RenameableItem> getRenameableMenuItems() {
        return this.renameableMenuItems;
    }

    @ApiStatus.Internal
    public @UnknownNullability List<Map.Entry<CustomBlockData, JsonElement>> getBlockDataRecipes() {
        return this.blockDataRecipes;
    }
}
