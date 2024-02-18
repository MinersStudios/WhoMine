package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jetbrains.annotations.*;

@SuppressWarnings("unchecked")
abstract class CookingRecipeBuilderImpl<B extends CookingRecipeBuilder<R>, R extends CookingRecipe<R>> implements CookingRecipeBuilder<R> {
    private NamespacedKey namespacedKey;
    private ItemStack result;
    private RecipeChoice ingredient;
    private float experience;
    private int cookingTime;
    private String group;
    private CookingBookCategory category;

    CookingRecipeBuilderImpl() {}

    CookingRecipeBuilderImpl(final @NotNull CookingRecipe<R> recipe) {
        this.namespacedKey = recipe.getKey();
        this.result = recipe.getResult();
        this.ingredient = recipe.getInputChoice();
        this.experience = recipe.getExperience();
        this.cookingTime = recipe.getCookingTime();
        this.group = recipe.getGroup();
        this.category = recipe.getCategory();
    }

    @Contract(" -> new")
    @ApiStatus.OverrideOnly
    protected abstract @NotNull R newRecipe();

    @Contract(" -> new")
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

        recipe.setGroup(
                this.group == null
                ? ""
                : this.group
        );
        recipe.setCategory(
                this.category == null
                ? CookingBookCategory.MISC
                : this.category
        );

        return recipe;
    }

    @Override
    public final @UnknownNullability NamespacedKey namespacedKey() {
        return this.namespacedKey;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B namespacedKey(final @NotNull NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;

        return (B) this;
    }

    @Override
    public final @UnknownNullability  ItemStack result() {
        return this.result;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B result(final @NotNull ItemStack result) throws IllegalArgumentException {
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Result cannot be empty");
        }

        this.result = result;

        return (B) this;
    }

    @Override
    public final @UnknownNullability RecipeChoice ingredient() {
        return this.ingredient;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B ingredient(final @NotNull Material ingredient) {
        return this.ingredient(new RecipeChoice.MaterialChoice(ingredient));
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B ingredient(final @NotNull ItemStack ingredient) {
        return this.ingredient(new RecipeChoice.ExactChoice(ingredient));
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B ingredient(final @NotNull RecipeChoice ingredient) {
        this.ingredient = ingredient;

        return (B) this;
    }

    @Override
    public final float experience() {
        return this.experience;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B experience(final float experience) {
        this.experience = experience;

        return (B) this;
    }

    @Override
    public final int cookingTime() {
        return this.cookingTime;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B cookingTime(final @Range(from = 0, to = Integer.MAX_VALUE) int cookingTime) {
        this.cookingTime = cookingTime;

        return (B) this;
    }

    @Override
    public final @UnknownNullability String group() {
        return this.group;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B group(final @NotNull String group) {
        this.group = group;

        return (B) this;
    }

    @Override
    public final @UnknownNullability CookingBookCategory category() {
        return this.category;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B category(final @NotNull CookingBookCategory category) {
        this.category = category;

        return (B) this;
    }
}
