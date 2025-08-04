package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.category.CraftingRecipeCategory;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

public class ShapelessRecipe extends AbstractCraftingRecipe<ShapelessRecipe> {
    /** The maximum number of ingredients a shapeless recipe can have */
    public static final int MAX_INGREDIENTS = 9;

    private RecipeChoice<?>[] ingredients;

    private ShapelessRecipe(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CraftingRecipeCategory category,
            final @NotNull String group
    ) {
        super(path, result, category, group);
    }

    public @Unmodifiable RecipeChoice<?> @UnknownNullability [] getIngredients() {
        return this.ingredients;
    }

    @Contract("_ -> this")
    public @NotNull ShapelessRecipe setIngredients(final RecipeChoice<?> @NotNull ... ingredients) throws IllegalArgumentException {
        if (ingredients.length > MAX_INGREDIENTS) {
            throw new IllegalArgumentException("Shapeless recipe can have at most " + MAX_INGREDIENTS + " ingredients");
        }

        return this.setIngredientsArray(ingredients);
    }

    @Contract("_ -> this")
    public @NotNull ShapelessRecipe setIngredients(final @NotNull RecipeChoice<?> choice) {
        return this.setIngredientsArray(choice);
    }

    public @NotNull ShapelessRecipe setIngredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second
    ) {
        return this.setIngredientsArray(first, second);
    }

    public @NotNull ShapelessRecipe setIngredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third
    ) {
        return this.setIngredientsArray(first, second, third);
    }

    public @NotNull ShapelessRecipe setIngredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth
    ) {
        return this.setIngredientsArray(first, second, third, fourth);
    }

    public @NotNull ShapelessRecipe setIngredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth,
            final @NotNull RecipeChoice<?> fifth
    ) {
        return this.setIngredientsArray(first, second, third, fourth, fifth);
    }

    public @NotNull ShapelessRecipe setIngredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth,
            final @NotNull RecipeChoice<?> fifth,
            final @NotNull RecipeChoice<?> sixth
    ) {
        return this.setIngredientsArray(first, second, third, fourth, fifth, sixth);
    }

    public @NotNull ShapelessRecipe setIngredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth,
            final @NotNull RecipeChoice<?> fifth,
            final @NotNull RecipeChoice<?> sixth,
            final @NotNull RecipeChoice<?> seventh
    ) {
        return this.setIngredientsArray(first, second, third, fourth, fifth, sixth, seventh);
    }

    public @NotNull ShapelessRecipe setIngredients(
            final @NotNull RecipeChoice<?> first,
            final @NotNull RecipeChoice<?> second,
            final @NotNull RecipeChoice<?> third,
            final @NotNull RecipeChoice<?> fourth,
            final @NotNull RecipeChoice<?> fifth,
            final @NotNull RecipeChoice<?> sixth,
            final @NotNull RecipeChoice<?> seventh,
            final @NotNull RecipeChoice<?> eighth
    ) {
        return this.setIngredientsArray(first, second, third, fourth, fifth, sixth, seventh, eighth);
    }

    public @NotNull ShapelessRecipe setIngredients(
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
        return this.setIngredientsArray(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
    }

    @Contract("_, _ -> new")
    public static  @NotNull ShapelessRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result
    ) {
        return of(path, result, CraftingRecipeCategory.MISC);
    }

    @Contract("_, _, _ -> new")
    public static  @NotNull ShapelessRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CraftingRecipeCategory category
    ) {
        return of(path, result, category, "");
    }

    @Contract("_, _, _, _ -> new")
    public static  @NotNull ShapelessRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CraftingRecipeCategory category,
            final @NotNull String group
    ) {
        return new ShapelessRecipe(path, result, category, group);
    }

    @Contract("_ -> this")
    private @NotNull ShapelessRecipe setIngredientsArray(final RecipeChoice<?> @NotNull ... ingredients) {
        this.ingredients = ingredients;

        return this;
    }
}
