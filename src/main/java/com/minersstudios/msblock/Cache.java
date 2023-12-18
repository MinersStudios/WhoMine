package com.minersstudios.msblock;

import com.minersstudios.msblock.collection.DiggingMap;
import com.minersstudios.msblock.collection.StepMap;
import com.minersstudios.mscore.plugin.cache.PluginCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Cache with all the data that needs to be stored
 */
public final class Cache extends PluginCache<MSBlock> {
    private StepMap stepMap;
    private DiggingMap diggingMap;

    /**
     * Cache constructor
     *
     * @param plugin The plugin that owns this cache
     */
    public Cache(final @NotNull MSBlock plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        this.stepMap = new StepMap();
        this.diggingMap = new DiggingMap();
    }

    @Override
    public void onUnload() {
        this.stepMap = null;
        this.diggingMap = null;
    }

    public @UnknownNullability StepMap getStepMap() {
        return this.stepMap;
    }

    public @UnknownNullability DiggingMap getDiggingMap() {
        return this.diggingMap;
    }
}
