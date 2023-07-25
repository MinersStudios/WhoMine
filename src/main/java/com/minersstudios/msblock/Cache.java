package com.minersstudios.msblock;

import com.minersstudios.msblock.collection.DiggingMap;
import com.minersstudios.msblock.collection.StepMap;
import com.minersstudios.msblock.customblock.CustomBlockData;

import java.util.ArrayList;
import java.util.List;

public final class Cache {
    public final List<CustomBlockData> recipeBlocks = new ArrayList<>();
    public final StepMap stepMap = new StepMap();
    public final DiggingMap diggingMap = new DiggingMap();
}
