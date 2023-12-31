package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

public final class StonecuttingRecipeBuilder implements RecipeBuilder<StonecuttingRecipe> {
    private NamespacedKey namespacedKey;
    private ItemStack result;
    private RecipeChoice ingredient;
    private String group;

    StonecuttingRecipeBuilder() {}

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
    public NamespacedKey namespacedKey() {
        return this.namespacedKey;
    }

    @Override
    public @NotNull StonecuttingRecipeBuilder namespacedKey(final @NotNull NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
        return this;
    }

    @Override
    public ItemStack result() {
        return this.result;
    }

    @Override
    public @NotNull StonecuttingRecipeBuilder result(final @NotNull ItemStack result) throws IllegalArgumentException {
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Result cannot be empty");
        }

        this.result = result;
        return this;
    }

    public String group() {
        return this.group;
    }

    public @NotNull StonecuttingRecipeBuilder group(final @NotNull String group) {
        this.group = group;
        return this;
    }

    public RecipeChoice ingredient() {
        return this.ingredient;
    }

    public @NotNull StonecuttingRecipeBuilder ingredient(final @NotNull Material ingredient) {
        return this.ingredient(new RecipeChoice.MaterialChoice(ingredient));
    }

    public @NotNull StonecuttingRecipeBuilder ingredient(final @NotNull ItemStack ingredient) {
        return this.ingredient(new RecipeChoice.ExactChoice(ingredient));
    }

    public @NotNull StonecuttingRecipeBuilder ingredient(final @NotNull RecipeChoice ingredient) {
        this.ingredient = ingredient;
        return this;
    }
}
