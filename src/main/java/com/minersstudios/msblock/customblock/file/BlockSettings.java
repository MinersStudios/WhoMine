package com.minersstudios.msblock.customblock.file;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public record BlockSettings(
        float hardness,
        @NotNull Tool tool,
        @NotNull Placing placing
) {

    public record Tool(
            @NotNull ToolType type,
            boolean force
    ) {}

    public record Placing(
            @NotNull PlacingType type,
            Material... placeableMaterials
    ) {}
}
