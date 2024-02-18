package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

/**
 * Represents a builder for crafting recipes
 *
 * @param <R> Type of the crafting recipe built by this builder
 * @see RecipeBuilder#shaped()
 * @see RecipeBuilder#shaped(ShapedRecipe)
 * @see RecipeBuilder#shapeless()
 * @see RecipeBuilder#shapeless(ShapelessRecipe)
 */
public interface CraftingRecipeBuilder<R extends CraftingRecipe> extends GroupedRecipeBuilder<R>, CategorizedRecipeBuilder<R, CraftingBookCategory> {}
