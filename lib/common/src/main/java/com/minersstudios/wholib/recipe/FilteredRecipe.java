package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.recipe.category.RecipeCategory;

public interface FilteredRecipe<R extends FilteredRecipe<R, C>, C extends RecipeCategory>
        extends CategorizedRecipe<R, C>, GroupedRecipe<R> {}
