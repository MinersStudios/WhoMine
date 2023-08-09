package com.minersstudios.msblock;

import com.minersstudios.msblock.collection.DiggingMap;
import com.minersstudios.msblock.collection.StepMap;

/**
 * Cache with all the data that needs to be stored
 */
public final class Cache {
    public final StepMap stepMap = new StepMap();
    public final DiggingMap diggingMap = new DiggingMap();
}
