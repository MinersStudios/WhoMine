package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.ShapelessRecipe;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

/**
 * Represents a builder for shapeless recipes
 *
 * @see RecipeBuilder#shapeless()
 * @see RecipeBuilder#shapeless(ShapelessRecipe)
 */
public final class ShapelessRecipeBuilder extends CraftingRecipeBuilderImpl<ShapelessRecipeBuilder, ShapelessRecipe> {
    private static final Function<ShapelessRecipeBuilder, ShapelessRecipe> RECIPE_CONSTRUCTOR =
            builder -> ShapelessRecipe.of(builder.path(), builder.result());

    private RecipeChoice<?>[] ingredients;

    ShapelessRecipeBuilder() {
        super(RECIPE_CONSTRUCTOR);
    }

    ShapelessRecipeBuilder(final @NotNull ShapelessRecipe recipe) {
        super(RECIPE_CONSTRUCTOR, recipe);

        this.ingredients = recipe.getIngredients();
    }

    @Contract(" -> new")
    @Override
    public @NotNull ShapelessRecipe build() throws IllegalStateException {
        if (this.ingredients == null) {
            throw new IllegalStateException("Recipe has no ingredients");
        }

        return super.build()
                    .setIngredients(this.ingredients);
    }

    /**
     * Returns the ingredients of the recipe
     *
     * @return The ingredients of the recipe
     */
    public RecipeChoice<?> @UnknownNullability [] ingredients() {
        return this.ingredients;
    }

    @Contract("_ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(final @NotNull RecipeChoice<?> @NotNull ... ingredients) {
        if (this.ingredients.length > ShapelessRecipe.MAX_INGREDIENTS) {
            throw new IllegalArgumentException("Shapeless recipe can have at most " + ShapelessRecipe.MAX_INGREDIENTS + " ingredients");
        }

        return this.setIngredients(ingredients);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first   The first ingredient
     * @param second  The second ingredient
     * @param third   The third ingredient
     * @param fourth  The fourth ingredient
     * @param fifth   The fifth ingredient
     * @param sixth   The sixth ingredient
     * @param seventh The seventh ingredient
     * @param eighth  The eighth ingredient
     * @param ninth   The ninth ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _, _, _, _, _, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth,
            final @NotNull RecipeChoice<?> fifth,
            final @NotNull RecipeChoice<?> sixth,
            final @NotNull RecipeChoice<?> seventh,
            final @NotNull RecipeChoice<?> eighth,
            final @NotNull RecipeChoice<?> ninth
    ) {
        return this.setIngredients(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first   The first ingredient
     * @param second  The second ingredient
     * @param third   The third ingredient
     * @param fourth  The fourth ingredient
     * @param fifth   The fifth ingredient
     * @param sixth   The sixth ingredient
     * @param seventh The seventh ingredient
     * @param eighth  The eighth ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _, _, _, _, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth,
            final @NotNull RecipeChoice<?> fifth,
            final @NotNull RecipeChoice<?> sixth,
            final @NotNull RecipeChoice<?> seventh,
            final @NotNull RecipeChoice<?> eighth
    ) {
        return this.setIngredients(first, second, third, fourth, fifth, sixth, seventh, eighth);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first   The first ingredient
     * @param second  The second ingredient
     * @param third   The third ingredient
     * @param fourth  The fourth ingredient
     * @param fifth   The fifth ingredient
     * @param sixth   The sixth ingredient
     * @param seventh The seventh ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _, _, _, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth,
            final @NotNull RecipeChoice<?> fifth,
            final @NotNull RecipeChoice<?> sixth,
            final @NotNull RecipeChoice<?> seventh
    ) {
        return this.setIngredients(first, second, third, fourth, fifth, sixth, seventh);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first   The first ingredient
     * @param second  The second ingredient
     * @param third   The third ingredient
     * @param fourth  The fourth ingredient
     * @param fifth   The fifth ingredient
     * @param sixth   The sixth ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _, _, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth,
            final @NotNull RecipeChoice<?> fifth,
            final @NotNull RecipeChoice<?> sixth
    ) {
        return this.setIngredients(first, second, third, fourth, fifth, sixth);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first   The first ingredient
     * @param second  The second ingredient
     * @param third   The third ingredient
     * @param fourth  The fourth ingredient
     * @param fifth   The fifth ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth,
            final @NotNull RecipeChoice<?> fifth
    ) {
        return this.setIngredients(first, second, third, fourth, fifth);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first   The first ingredient
     * @param second  The second ingredient
     * @param third   The third ingredient
     * @param fourth  The fourth ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth
    ) {
        return this.setIngredients(first, second, third, fourth);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first   The first ingredient
     * @param second  The second ingredient
     * @param third   The third ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third
    ) {
        return this.setIngredients(first, second, third);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first   The first ingredient
     * @param second  The second ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second
    ) {
        return this.setIngredients(first, second);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first The first ingredient
     * @return This builder, for chaining
     */
    @Contract("_ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(final @NotNull RecipeChoice<?> first) {
        return this.setIngredients(first);
    }

    @Contract("_ -> this")
    private @NotNull ShapelessRecipeBuilder setIngredients(final RecipeChoice<?> @NotNull ... ingredients) {
        this.ingredients = ingredients;

        return this;
    }
}
