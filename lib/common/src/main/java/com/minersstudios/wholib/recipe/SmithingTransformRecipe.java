package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SmithingTransformRecipe extends AbstractSmithingRecipe {

    private SmithingTransformRecipe(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull RecipeChoice<?> template,
            final @NotNull RecipeChoice<?> base,
            final @NotNull RecipeChoice<?> addition,
            final boolean copyDataComponents
    ) {
        super(path, result, template, base, addition, copyDataComponents);
    }

    @Contract("_, _, _, _, _ -> new")
    public static  @NotNull SmithingTransformRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull RecipeChoice<?> template,
            final @NotNull RecipeChoice<?> base,
            final @NotNull RecipeChoice<?> addition
    ) {
        return of(path, result, template, base, addition, DEFAULT_COPY_DATA_COMPONENTS);
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static  @NotNull SmithingTransformRecipe of(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull RecipeChoice<?> template,
            final @NotNull RecipeChoice<?> base,
            final @NotNull RecipeChoice<?> addition,
            final boolean copyDataComponents
    ) {
        return new SmithingTransformRecipe(path, result, template, base, addition, copyDataComponents);
    }
}
