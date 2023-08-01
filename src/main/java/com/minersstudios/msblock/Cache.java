package com.minersstudios.msblock;

import com.google.gson.JsonElement;
import com.minersstudios.msblock.collection.DiggingMap;
import com.minersstudios.msblock.collection.StepMap;
import com.minersstudios.msblock.customblock.CustomBlockData;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cache with all the data that needs to be stored
 */
public final class Cache {
    public final StepMap stepMap = new StepMap();
    public final DiggingMap diggingMap = new DiggingMap();
    public final Map<CustomBlockData, JsonElement> recipesToRegister = new LinkedHashMap<>();
}
