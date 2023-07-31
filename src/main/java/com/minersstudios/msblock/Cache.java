package com.minersstudios.msblock;

import com.minersstudios.msblock.collection.DiggingMap;
import com.minersstudios.msblock.collection.StepMap;
import com.minersstudios.msblock.customblock.CustomBlockData;

import java.util.LinkedList;
import java.util.Queue;

public final class Cache {
    public final StepMap stepMap = new StepMap();
    public final DiggingMap diggingMap = new DiggingMap();
    public final Queue<CustomBlockData> recipesToRegister = new LinkedList<>();
}
