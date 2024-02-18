package com.minersstudios.mscore.inventory.recipe.builder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents a builder for shapeless recipes
 *
 * @see RecipeBuilder#shapeless()
 * @see RecipeBuilder#shapeless(ShapelessRecipe)
 */
public final class ShapelessRecipeBuilder extends CraftingRecipeBuilderImpl<ShapelessRecipeBuilder, ShapelessRecipe> {
    private RecipeChoice[] ingredients;

    /** The maximum number of ingredients a shapeless recipe can have */
    public static final int MAX_INGREDIENTS = 9;

    ShapelessRecipeBuilder() {}

    ShapelessRecipeBuilder(final @NotNull ShapelessRecipe recipe) {
        super(recipe);

        this.ingredients = recipe.getChoiceList().toArray(new RecipeChoice[0]);
    }

    @Contract(" -> new")
    @ApiStatus.OverrideOnly
    @Override
    protected @NotNull ShapelessRecipe newRecipe() throws IllegalStateException {
        if (
                this.ingredients == null
                || this.ingredients.length == 0
        ) {
            throw new IllegalStateException("Recipe must have at least one ingredient");
        }

        final ShapelessRecipe recipe = new ShapelessRecipe(this.namespacedKey(), this.result());

        for (final var ingredient : this.ingredients) {
            recipe.addIngredient(ingredient);
        }

        return recipe;
    }

    /**
     * Returns the ingredients of the recipe
     *
     * @return The ingredients of the recipe
     */
    public RecipeChoice @UnknownNullability [] ingredients() {
        return this.ingredients;
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
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth,
            final @NotNull Material fifth,
            final @NotNull Material sixth,
            final @NotNull Material seventh,
            final @NotNull Material eighth,
            final @NotNull Material ninth
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
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth,
            final @NotNull Material fifth,
            final @NotNull Material sixth,
            final @NotNull Material seventh,
            final @NotNull Material eighth
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
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth,
            final @NotNull Material fifth,
            final @NotNull Material sixth,
            final @NotNull Material seventh
    ) {
        return this.setIngredients(first, second, third, fourth, fifth, sixth, seventh);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first  The first ingredient
     * @param second The second ingredient
     * @param third  The third ingredient
     * @param fourth The fourth ingredient
     * @param fifth  The fifth ingredient
     * @param sixth  The sixth ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _, _, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth,
            final @NotNull Material fifth,
            final @NotNull Material sixth
    ) {
        return this.setIngredients(first, second, third, fourth, fifth, sixth);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first  The first ingredient
     * @param second The second ingredient
     * @param third  The third ingredient
     * @param fourth The fourth ingredient
     * @param fifth  The fifth ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth,
            final @NotNull Material fifth
    ) {
        return this.setIngredients(first, second, third, fourth, fifth);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first  The first ingredient
     * @param second The second ingredient
     * @param third  The third ingredient
     * @param fourth The fourth ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third,
            final @NotNull Material fourth
    ) {
        return this.setIngredients(first, second, third, fourth);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first  The first ingredient
     * @param second The second ingredient
     * @param third  The third ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second,
            final @NotNull Material third
    ) {
        return this.setIngredients(first, second, third);
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param first  The first ingredient
     * @param second The second ingredient
     * @return This builder, for chaining
     */
    @Contract("_, _ -> this")
    public @NotNull ShapelessRecipeBuilder ingredients(
            final @NotNull Material first,
            final @NotNull Material second
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
    public @NotNull ShapelessRecipeBuilder ingredients(final @NotNull Material first) {
        return this.setIngredients(first);
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
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth,
            final @NotNull ItemStack fifth,
            final @NotNull ItemStack sixth,
            final @NotNull ItemStack seventh,
            final @NotNull ItemStack eighth,
            final @NotNull ItemStack ninth
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
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth,
            final @NotNull ItemStack fifth,
            final @NotNull ItemStack sixth,
            final @NotNull ItemStack seventh,
            final @NotNull ItemStack eighth
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
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth,
            final @NotNull ItemStack fifth,
            final @NotNull ItemStack sixth,
            final @NotNull ItemStack seventh
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
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth,
            final @NotNull ItemStack fifth,
            final @NotNull ItemStack sixth
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
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth,
            final @NotNull ItemStack fifth
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
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third,
            final @NotNull ItemStack fourth
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
            final @NotNull ItemStack first,
            final @NotNull ItemStack second,
            final @NotNull ItemStack third
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
            final @NotNull ItemStack first,
            final @NotNull ItemStack second
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
    public @NotNull ShapelessRecipeBuilder ingredients(final @NotNull ItemStack first) {
        return this.setIngredients(first);
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
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth,
            final @NotNull RecipeChoice fifth,
            final @NotNull RecipeChoice sixth,
            final @NotNull RecipeChoice seventh,
            final @NotNull RecipeChoice eighth,
            final @NotNull RecipeChoice ninth
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
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth,
            final @NotNull RecipeChoice fifth,
            final @NotNull RecipeChoice sixth,
            final @NotNull RecipeChoice seventh,
            final @NotNull RecipeChoice eighth
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
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth,
            final @NotNull RecipeChoice fifth,
            final @NotNull RecipeChoice sixth,
            final @NotNull RecipeChoice seventh
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
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth,
            final @NotNull RecipeChoice fifth,
            final @NotNull RecipeChoice sixth
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
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth,
            final @NotNull RecipeChoice fifth
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
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third,
            final @NotNull RecipeChoice fourth
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
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second,
            final @NotNull RecipeChoice third
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
            final @NotNull RecipeChoice first,
            final @NotNull RecipeChoice second
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
    public @NotNull ShapelessRecipeBuilder ingredients(final @NotNull RecipeChoice first) {
        return this.setIngredients(first);
    }

    @Contract("_ -> this")
    private @NotNull ShapelessRecipeBuilder setIngredients(final Material @NotNull ... ingredients) throws IllegalArgumentException {
        final RecipeChoice[] choices = new RecipeChoice[ingredients.length];

        for (int i = 0; i < ingredients.length; ++i) {
            choices[i] = new RecipeChoice.MaterialChoice(ingredients[i]);
        }

        return this.setIngredients(choices);
    }

    @Contract("_ -> this")
    private @NotNull ShapelessRecipeBuilder setIngredients(final ItemStack @NotNull ... ingredients) throws IllegalArgumentException {
        final RecipeChoice[] choices = new RecipeChoice[ingredients.length];

        for (int i = 0; i < ingredients.length; ++i) {
            choices[i] = new RecipeChoice.ExactChoice(ingredients[i]);
        }

        return this.setIngredients(choices);
    }

    @Contract("_ -> this")
    private @NotNull ShapelessRecipeBuilder setIngredients(final RecipeChoice @NotNull ... ingredients) throws IllegalArgumentException {
        if (ingredients.length > MAX_INGREDIENTS) {
            throw new IllegalArgumentException("Shapeless recipes cannot have more than " + MAX_INGREDIENTS + " ingredients");
        }

        this.ingredients = ingredients;

        return this;
    }
}
