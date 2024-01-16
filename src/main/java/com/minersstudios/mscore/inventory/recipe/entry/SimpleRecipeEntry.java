package com.minersstudios.mscore.inventory.recipe.entry;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

@Immutable
final class SimpleRecipeEntry extends RecipeEntryImpl {
    private final Recipe recipe;

    SimpleRecipeEntry(
            final @NotNull Recipe recipe,
            final boolean isRegisteredInMenu
    ) {
        super(isRegisteredInMenu);

        this.recipe = recipe;
    }

    @Override
    public @NotNull RecipeBuilder<?> getBuilder() throws UnsupportedOperationException {
        return RecipeBuilder.unknownRecipeBuilder(this.recipe);
    }

    @Override
    public @NotNull Recipe getRecipe() {
        return this.recipe;
    }

    @Override
    public boolean isBuildable() {
        return false;
    }

    @Override
    public @NotNull String toString() {
        return "SimpleRecipeEntry{" +
                "recipe=" + this.recipe +
                ", isRegisteredInMenu=" + this.isRegisteredInMenu() +
                '}';
    }
}
