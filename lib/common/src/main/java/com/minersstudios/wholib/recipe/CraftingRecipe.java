package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.recipe.category.CraftingRecipeCategory;

public interface CraftingRecipe<R extends CraftingRecipe<R>>
        extends PathedRecipe, FilteredRecipe<R, CraftingRecipeCategory> {}
