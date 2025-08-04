package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.category.CraftingRecipeCategory;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCraftingRecipe<R extends CraftingRecipe<R>>
        extends AbstractFilteredRecipe<R, CraftingRecipeCategory>
        implements CraftingRecipe<R> {

    private final ResourceKey path;

    protected AbstractCraftingRecipe(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CraftingRecipeCategory category,
            final @NotNull String group
    ) {
        super(result, category, group);

        this.path = path;
    }

    @Override
    public @NotNull ResourceKey getResourceKey() {
        return this.path;
    }
}
