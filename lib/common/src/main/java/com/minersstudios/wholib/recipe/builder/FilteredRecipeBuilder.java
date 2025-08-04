package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.FilteredRecipe;
import com.minersstudios.wholib.recipe.category.RecipeCategory;

public interface FilteredRecipeBuilder<R extends FilteredRecipe<R, C>, C extends RecipeCategory>
        extends GroupedRecipeBuilder<R>, CategorizedRecipeBuilder<R, C> {}
