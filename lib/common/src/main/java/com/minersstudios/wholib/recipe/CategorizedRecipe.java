package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.recipe.category.RecipeCategory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface CategorizedRecipe<R extends CategorizedRecipe<R, C>, C extends RecipeCategory> extends Recipe {

    @NotNull C getCategory();

    @Contract("_ -> this")
    @NotNull R setCategory(final @NotNull C category);
}
