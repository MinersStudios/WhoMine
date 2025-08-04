package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.category.CookingRecipeCategory;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCookingRecipe<R extends CookingRecipe<R>>
        extends AbstractFilteredRecipe<R, CookingRecipeCategory>
        implements CookingRecipe<R> {

    private final ResourceKey path;
    private RecipeChoice<?> ingredient;
    private float experience;
    private int cookingTime;

    protected AbstractCookingRecipe(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CookingRecipeCategory category,
            final @NotNull String group
    ) {
        super(result, category, group);

        this.path = path;
    }

    @Override
    public @NotNull ResourceKey getResourceKey() {
        return this.path;
    }

    @Override
    public @NotNull RecipeChoice<?> getIngredient() {
        return this.ingredient;
    }

    @SuppressWarnings("unchecked")
    @Contract("_ -> this")
    @Override
    public @NotNull R setIngredient(final @NotNull RecipeChoice<?> ingredient) {
        this.ingredient = ingredient;

        return (R) this;
    }

    @Override
    public float getExperience() {
        return this.experience;
    }

    @SuppressWarnings("unchecked")
    @Contract("_ -> this")
    @Override
    public @NotNull R setExperience(final float experience) {
        this.experience = experience;

        return (R) this;
    }

    @Override
    public int getCookingTime() {
        return this.cookingTime;
    }

    @SuppressWarnings("unchecked")
    @Contract("_ -> this")
    @Override
    public @NotNull R setCookingTime(final int cookingTime) {
        this.cookingTime = cookingTime;

        return (R) this;
    }
}
