package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.recipe.category.CookingRecipeCategory;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface CookingRecipe<R extends CookingRecipe<R>>
        extends PathedRecipe, FilteredRecipe<R, CookingRecipeCategory> {

    @NotNull RecipeChoice<?> getIngredient();

    @Contract("_ -> this")
    @NotNull R setIngredient(final @NotNull RecipeChoice<?> ingredient);

    float getExperience();

    @Contract("_ -> this")
    @NotNull R setExperience(final float experience);

    int getCookingTime();

    @Contract("_ -> this")
    @NotNull R setCookingTime(final int cookingTime);
}
