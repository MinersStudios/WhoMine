package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

@SuppressWarnings("unchecked")
abstract class SmithingRecipeBuilderImpl<B extends SmithingRecipeBuilder<R>, R extends SmithingRecipe> implements SmithingRecipeBuilder<R> {
    private NamespacedKey namespacedKey;
    private ItemStack result;
    private RecipeChoice template;
    private RecipeChoice base;
    private RecipeChoice addition;
    private boolean copyNbt;

    SmithingRecipeBuilderImpl() {}

    SmithingRecipeBuilderImpl(final @NotNull SmithingRecipe recipe) {
        this.namespacedKey = recipe.getKey();
        this.result = recipe.getResult();
        this.base = recipe.getBase();
        this.addition = recipe.getAddition();
        this.copyNbt = recipe.willCopyNbt();

        if (recipe instanceof final SmithingTransformRecipe transformRecipe) {
            this.template = transformRecipe.getTemplate();
        } else if (recipe instanceof final SmithingTrimRecipe trimRecipe) {
            this.template = trimRecipe.getTemplate();
        }
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

        if (this.template == null) {
            throw new IllegalStateException("Recipe has no template");
        }

        if (this.base == null) {
            throw new IllegalStateException("Recipe has no base");
        }

        if (this.addition == null) {
            throw new IllegalStateException("Recipe has no addition");
        }

        return this.newRecipe();
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
    public final @UnknownNullability ItemStack result() {
        return this.result;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B result(final @NotNull ItemStack result) {
        this.result = result;

        return (B) this;
    }

    @Override
    public final @UnknownNullability RecipeChoice template() {
        return this.template;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B template(final @NotNull Material template) {
        return this.template(new RecipeChoice.MaterialChoice(template));
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B template(final @NotNull ItemStack template) {
        return this.template(new RecipeChoice.ExactChoice(template));
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B template(final @NotNull RecipeChoice template) {
        this.template = template;

        return (B) this;
    }

    @Override
    public final @UnknownNullability RecipeChoice base() {
        return this.base;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B base(final @NotNull Material base) {
        return this.base(new RecipeChoice.MaterialChoice(base));
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B base(final @NotNull ItemStack base) {
        return this.base(new RecipeChoice.ExactChoice(base));
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B base(final @NotNull RecipeChoice base) {
        this.base = base;

        return (B) this;
    }

    @Override
    public final @UnknownNullability RecipeChoice addition() {
        return this.addition;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B addition(final @NotNull Material addition) {
        return this.addition(new RecipeChoice.MaterialChoice(addition));
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B addition(final @NotNull ItemStack addition) {
        return this.addition(new RecipeChoice.ExactChoice(addition));
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B addition(final @NotNull RecipeChoice addition) {
        this.addition = addition;

        return (B) this;
    }

    @Override
    public final boolean copyNbt() {
        return this.copyNbt;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B copyNbt(final boolean copyNbt) {
        this.copyNbt = copyNbt;

        return (B) this;
    }
}
