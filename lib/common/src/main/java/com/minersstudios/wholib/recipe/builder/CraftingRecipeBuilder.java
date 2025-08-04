package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.CraftingRecipe;
import com.minersstudios.wholib.recipe.ShapedRecipe;
import com.minersstudios.wholib.recipe.ShapelessRecipe;
import com.minersstudios.wholib.recipe.category.CraftingRecipeCategory;

/**
 * Represents a builder for crafting recipes
 *
 * @param <R> Type of the crafting recipe built by this builder
 * @see RecipeBuilder#shaped()
 * @see RecipeBuilder#shaped(ShapedRecipe)
 * @see RecipeBuilder#shapeless()
 * @see RecipeBuilder#shapeless(ShapelessRecipe)
 */
public interface CraftingRecipeBuilder<R extends CraftingRecipe<R>>
        extends PathedRecipeBuilder<R>, FilteredRecipeBuilder<R, CraftingRecipeCategory> {}
