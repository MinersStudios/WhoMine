package com.minersstudios.mscore.inventory.recipe.entry;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

final class BuilderRecipeEntry extends RecipeEntryImpl {
    private final RecipeBuilder<?> builder;

    BuilderRecipeEntry(
            final @NotNull RecipeBuilder<?> builder,
            final boolean isRegisteredInMenu
    ) {
        super(isRegisteredInMenu);

        this.builder = builder;
    }

    @Override
    public @NotNull RecipeBuilder<?> getBuilder() {
        return this.builder;
    }

    @Override
    public @NotNull Recipe getRecipe() {
        return this.builder.build();
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    @Override
    public @NotNull String toString() {
        return "BuilderRecipeEntry{" +
                "recipe=" + this.builder +
                ", isRegisteredInMenu=" + this.isRegisteredInMenu() +
                '}';
    }
}
