package com.minersstudios.msblock.customblock.file;

import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public record RecipeEntry(
        @NotNull ShapedRecipe recipe,
        boolean showInCraftsMenu
) {}
