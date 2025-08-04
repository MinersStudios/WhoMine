package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.category.CookingRecipeCategory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SmokingRecipe extends AbstractCookingRecipe<SmokingRecipe> {

    private SmokingRecipe(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CookingRecipeCategory category,
            final @NotNull String group
    ) {
        super(path, result, category, group);
    }

    @Contract("_, _ -> new")
    public static  @NotNull SmokingRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result
    ) {
        return of(path, result, CookingRecipeCategory.MISC);
    }

    @Contract("_, _, _ -> new")
    public static  @NotNull SmokingRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CookingRecipeCategory category
    ) {
        return of(path, result, category, "");
    }

    @Contract("_, _, _, _ -> new")
    public static  @NotNull SmokingRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull CookingRecipeCategory category,
            final @NotNull String group
    ) {
        return new SmokingRecipe(path, result, category, group);
    }
}
