package com.minersstudios.msblock.customblock.file;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public class RecipeEntry {
    private final @NotNull Recipe recipe;
    private final boolean showInCraftsMenu;

    public RecipeEntry(
            @NotNull Recipe recipe,
            boolean showInCraftsMenu
    ) {
        this.recipe = recipe;
        this.showInCraftsMenu = showInCraftsMenu;
    }

    public @NotNull Recipe getRecipe() {
        return this.recipe;
    }

    public boolean isShowInCraftsMenu() {
        return this.showInCraftsMenu;
    }
}
