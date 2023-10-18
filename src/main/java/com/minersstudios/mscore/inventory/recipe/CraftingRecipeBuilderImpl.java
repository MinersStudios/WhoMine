package com.minersstudios.mscore.inventory.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
abstract class CraftingRecipeBuilderImpl<B extends CraftingRecipeBuilderImpl<B, R>, R extends CraftingRecipe> implements RecipeBuilder<R> {
    protected NamespacedKey namespacedKey;
    protected ItemStack result;
    protected String group;
    protected CraftingBookCategory category;

    protected abstract @NotNull R newRecipe();

    @Override
    public final @NotNull R build() throws IllegalStateException {
        if (this.namespacedKey == null) {
            throw new IllegalStateException("Recipe has no namespaced key");
        }

        if (this.result == null) {
            throw new IllegalStateException("Recipe has no result");
        }

        final R recipe = this.newRecipe();

        recipe.setGroup(this.group == null ? "" : this.group);
        recipe.setCategory(this.category == null ? CraftingBookCategory.MISC : this.category);

        return recipe;
    }

    @Override
    public final NamespacedKey namespacedKey() {
        return this.namespacedKey;
    }

    @Override
    public final @NotNull B namespacedKey(@NotNull NamespacedKey key) {
        this.namespacedKey = key;
        return (B) this;
    }

    @Override
    public final ItemStack result() {
        return this.result;
    }

    @Override
    public final @NotNull B result(@NotNull ItemStack result) throws IllegalArgumentException {
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Result cannot be empty");
        }

        this.result = result;
        return (B) this;
    }

    public final String group() {
        return this.group;
    }

    public final @NotNull B group(final @NotNull String group) {
        this.group = group;
        return (B) this;
    }

    public final CraftingBookCategory category() {
        return this.category;
    }

    public final @NotNull B category(final @NotNull CraftingBookCategory category) {
        this.category = category;
        return (B) this;
    }
}
