package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents a builder for stonecutting recipe
 *
 * @see RecipeBuilder#stonecutting()
 * @see RecipeBuilder#stonecutting(StonecuttingRecipe)
 */
public final class StonecuttingRecipeBuilder implements GroupedRecipeBuilder<StonecuttingRecipe> {
    private NamespacedKey namespacedKey;
    private ItemStack result;
    private RecipeChoice ingredient;
    private String group;

    StonecuttingRecipeBuilder() {}

    StonecuttingRecipeBuilder(final @NotNull StonecuttingRecipe recipe) {
        this.namespacedKey = recipe.getKey();
        this.result = recipe.getResult();
        this.ingredient = recipe.getInputChoice();
        this.group = recipe.getGroup();
    }

    @Contract(" -> new")
    @Override
    public @NotNull StonecuttingRecipe build() throws IllegalStateException {
        if (this.namespacedKey == null) {
            throw new IllegalStateException("Recipe has no namespaced key");
        }

        if (this.result == null) {
            throw new IllegalStateException("Recipe has no result");
        }

        if (this.ingredient == null) {
            throw new IllegalStateException("Recipe has no ingredient");
        }

        final StonecuttingRecipe recipe =
                new StonecuttingRecipe(
                        this.namespacedKey,
                        this.result,
                        this.ingredient
                );

        recipe.setGroup(this.group == null ? "" : this.group);

        return recipe;
    }

    @Override
    public @UnknownNullability NamespacedKey namespacedKey() {
        return this.namespacedKey;
    }

    @Contract("_ -> this")
    @Override
    public @NotNull StonecuttingRecipeBuilder namespacedKey(final @NotNull NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;

        return this;
    }

    @Override
    public @UnknownNullability ItemStack result() {
        return this.result;
    }

    @Contract("_ -> this")
    @Override
    public @NotNull StonecuttingRecipeBuilder result(final @NotNull ItemStack result) throws IllegalArgumentException {
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Result cannot be empty");
        }

        this.result = result;

        return this;
    }

    @Override
    public @UnknownNullability String group() {
        return this.group;
    }

    @Contract("_ -> this")
    @Override
    public @NotNull StonecuttingRecipeBuilder group(final @NotNull String group) {
        this.group = group;

        return this;
    }

    /**
     * Returns the ingredient of the recipe
     *
     * @return The ingredient of the recipe
     */
    public @UnknownNullability RecipeChoice ingredient() {
        return this.ingredient;
    }

    /**
     * Sets the ingredient of the recipe
     *
     * @param ingredient New ingredient of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    public @NotNull StonecuttingRecipeBuilder ingredient(final @NotNull Material ingredient) {
        return this.ingredient(new RecipeChoice.MaterialChoice(ingredient));
    }

    /**
     * Sets the ingredient of the recipe
     *
     * @param ingredient New ingredient of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    public @NotNull StonecuttingRecipeBuilder ingredient(final @NotNull ItemStack ingredient) {
        return this.ingredient(new RecipeChoice.ExactChoice(ingredient));
    }

    /**
     * Sets the ingredient of the recipe
     *
     * @param ingredient New ingredient of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    public @NotNull StonecuttingRecipeBuilder ingredient(final @NotNull RecipeChoice ingredient) {
        this.ingredient = ingredient;

        return this;
    }
}
