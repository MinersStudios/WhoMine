package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.StonecuttingRecipe;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

/**
 * Represents a builder for stonecutting recipe
 *
 * @see RecipeBuilder#stonecutting()
 * @see RecipeBuilder#stonecutting(StonecuttingRecipe)
 */
public final class StonecuttingRecipeBuilder
        extends PathedRecipeBuilderImpl<StonecuttingRecipeBuilder, StonecuttingRecipe>
        implements GroupedRecipeBuilder<StonecuttingRecipe> {
    private static final Function<StonecuttingRecipeBuilder, StonecuttingRecipe> RECIPE_CONSTRUCTOR =
            builder -> StonecuttingRecipe.of(builder.path(), builder.result(), builder.ingredient());

    private RecipeChoice<?> ingredient;
    private String group;

    StonecuttingRecipeBuilder() {
        super(RECIPE_CONSTRUCTOR);

        this.group = "";
    }

    StonecuttingRecipeBuilder(final @NotNull StonecuttingRecipe recipe) {
        super(RECIPE_CONSTRUCTOR, recipe);

        this.ingredient = recipe.getIngredient();
        this.group = recipe.getGroup();
    }

    @Contract(" -> new")
    @Override
    public @NotNull StonecuttingRecipe build() throws IllegalStateException {
        if (this.ingredient == null) {
            throw new IllegalStateException("Recipe has no ingredient");
        }

        final StonecuttingRecipe recipe = super.build();

        recipe.setIngredient(this.ingredient);
        recipe.setGroup(this.group);

        return recipe;
    }

    @Override
    public @NotNull String group() {
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
    public @UnknownNullability RecipeChoice<?> ingredient() {
        return this.ingredient;
    }

    /**
     * Sets the ingredient of the recipe
     *
     * @param ingredient New ingredient of the recipe
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    public @NotNull StonecuttingRecipeBuilder ingredient(final @NotNull RecipeChoice<?> ingredient) {
        this.ingredient = ingredient;

        return this;
    }
}
