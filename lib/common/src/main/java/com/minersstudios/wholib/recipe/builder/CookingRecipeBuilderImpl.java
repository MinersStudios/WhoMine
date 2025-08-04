package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.CookingRecipe;
import com.minersstudios.wholib.recipe.category.CookingRecipeCategory;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.*;

import java.util.function.Function;

@SuppressWarnings("unchecked")
abstract class CookingRecipeBuilderImpl<B extends CookingRecipeBuilder<R>, R extends CookingRecipe<R>>
        extends FilteredRecipeBuilderImpl<B, R, CookingRecipeCategory>
        implements CookingRecipeBuilder<R> {

    private ResourceKey path;
    private RecipeChoice<?> ingredient;
    private float experience;
    private int cookingTime;

    protected CookingRecipeBuilderImpl(final @NotNull Function<B, R> recipeConstructor) {
        super(recipeConstructor);

        this.category(CookingRecipeCategory.MISC);
    }

    protected CookingRecipeBuilderImpl(
            final @NotNull Function<B, R> recipeConstructor,
            final @NotNull R recipe
    ) {
        super(recipeConstructor, recipe);

        this.path = recipe.getResourceKey();
        this.ingredient = recipe.getIngredient();
        this.experience = recipe.getExperience();
        this.cookingTime = recipe.getCookingTime();
    }

    @Contract(" -> new")
    @Override
    public @NotNull R build() throws IllegalStateException {
        if (this.path == null) {
            throw new IllegalStateException("Recipe has no resource key");
        }

        if (this.ingredient == null) {
            throw new IllegalStateException("Recipe has no ingredient");
        }

        final R recipe = super.build();

        recipe.setIngredient(this.ingredient);
        recipe.setExperience(this.experience);
        recipe.setCookingTime(this.cookingTime);

        return recipe;
    }

    @Override
    public final @UnknownNullability ResourceKey path() {
        return this.path;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B path(final @NotNull ResourceKey path) {
        this.path = path;

        return (B) this;
    }

    @Override
    public final @UnknownNullability RecipeChoice<?> ingredient() {
        return this.ingredient;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B ingredient(final @NotNull RecipeChoice<?> ingredient) {
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
}
