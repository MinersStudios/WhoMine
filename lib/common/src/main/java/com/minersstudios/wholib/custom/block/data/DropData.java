package com.minersstudios.wholib.custom.block.data;

import com.minersstudios.wholib.item.Item;
import org.jetbrains.annotations.NotNull;

public interface DropData {

    @NotNull Item getItem();

    int getExperience();
}
