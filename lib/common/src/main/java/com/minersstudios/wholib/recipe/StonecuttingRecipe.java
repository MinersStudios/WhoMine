package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class StonecuttingRecipe
        extends AbstractPathedRecipe
        implements GroupedRecipe<StonecuttingRecipe> {

    private RecipeChoice<?> ingredient;
    private String group;

    private StonecuttingRecipe(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull RecipeChoice<?> ingredient,
            final @NotNull String group
    ) {
        super(path, result);

        this.ingredient = ingredient;
        this.group = group;
    }

    public @NotNull RecipeChoice<?> getIngredient() {
        return this.ingredient;
    }

    @Contract("_ -> this")
    public @NotNull StonecuttingRecipe setIngredient(final @NotNull RecipeChoice<?> ingredient) {
        this.ingredient = ingredient;

        return this;
    }

    @Override
    public @NotNull String getGroup() {
        return this.group;
    }

    @Contract("_ -> this")
    @Override
    public @NotNull StonecuttingRecipe setGroup(final @NotNull String group) {
        this.group = group;

        return this;
    }

    @Contract("_, _, _ -> new")
    public static  @NotNull StonecuttingRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull RecipeChoice<?> ingredient
    ) {
        return of(path, result, ingredient);
    }

    @Contract("_, _, _, _ -> new")
    public static  @NotNull StonecuttingRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull RecipeChoice<?> ingredient,
            final @NotNull String group
    ) {
        return new StonecuttingRecipe(path, result, ingredient, group);
    }
}
