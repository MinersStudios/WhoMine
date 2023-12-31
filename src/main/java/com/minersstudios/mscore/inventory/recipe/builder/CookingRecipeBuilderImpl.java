package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@SuppressWarnings("unchecked")
abstract class CookingRecipeBuilderImpl<B extends CookingRecipeBuilderImpl<B, R>, R extends CookingRecipe<R>> implements RecipeBuilder<R> {
    protected NamespacedKey namespacedKey;
    protected ItemStack result;
    protected RecipeChoice ingredient;
    protected float experience;
    protected int cookingTime;
    protected String group;
    protected CookingBookCategory category;

    protected abstract @NotNull R newRecipe();

    @Override
    public final @NotNull R build() throws IllegalStateException {
        if (this.namespacedKey == null) {
            throw new IllegalStateException("Recipe has no namespaced key");
        }

        if (this.result == null) {
            throw new IllegalStateException("Recipe has no result");
        }

        if (this.ingredient == null) {
            throw new IllegalStateException("Recipe has no ingredient");
        }

        final R recipe = this.newRecipe();

        recipe.setGroup(this.group == null ? "" : this.group);
        recipe.setCategory(this.category == null ? CookingBookCategory.MISC : this.category);

        return recipe;
    }

    @Override
    public final NamespacedKey namespacedKey() {
        return this.namespacedKey;
    }

    @Override
    public final @NotNull B namespacedKey(final @NotNull NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
        return (B) this;
    }

    @Override
    public final ItemStack result() {
        return this.result;
    }

    @Override
    public final @NotNull B result(final @NotNull ItemStack result) throws IllegalArgumentException {
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Result cannot be empty");
        }

        this.result = result;
        return (B) this;
    }

    public final RecipeChoice ingredient() {
        return this.ingredient;
    }

    public final @NotNull B ingredient(final @NotNull Material ingredient) {
        return this.ingredient(new RecipeChoice.MaterialChoice(ingredient));
    }

    public final @NotNull B ingredient(final @NotNull ItemStack ingredient) {
        return this.ingredient(new RecipeChoice.ExactChoice(ingredient));
    }

    public final @NotNull B ingredient(final @NotNull RecipeChoice ingredient) {
        this.ingredient = ingredient;
        return (B) this;
    }

    public final float experience() {
        return this.experience;
    }

    public final @NotNull B experience(final float experience) {
        this.experience = experience;
        return (B) this;
    }

    public final int cookingTime() {
        return this.cookingTime;
    }

    public final @NotNull B cookingTime(final @Range(from = 0, to = Integer.MAX_VALUE) int cookingTime) {
        this.cookingTime = cookingTime;
        return (B) this;
    }

    public final String group() {
        return this.group;
    }

    public final @NotNull B group(final @NotNull String group) {
        this.group = group;
        return (B) this;
    }

    public final CookingBookCategory category() {
        return this.category;
    }

    public final @NotNull B category(final @NotNull CookingBookCategory category) {
        this.category = category;
        return (B) this;
    }
}
