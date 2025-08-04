package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.CraftingRecipe;
import com.minersstudios.wholib.recipe.category.CraftingRecipeCategory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

@SuppressWarnings("unchecked")
abstract class CraftingRecipeBuilderImpl<B extends CraftingRecipeBuilder<R>, R extends CraftingRecipe<R>>
        extends FilteredRecipeBuilderImpl<B, R, CraftingRecipeCategory>
        implements CraftingRecipeBuilder<R> {

    private ResourceKey path;

    protected CraftingRecipeBuilderImpl(final @NotNull Function<B, R> recipeConstructor) {
        super(recipeConstructor);

        this.category(CraftingRecipeCategory.MISC);
    }

    protected CraftingRecipeBuilderImpl(
            final @NotNull Function<B, R> recipeConstructor,
            final @NotNull R recipe
    ) {
        super(recipeConstructor, recipe);

        this.path = recipe.getResourceKey();
    }

    @Contract(" -> new")
    @Override
    public @NotNull R build() throws IllegalStateException {
        if (this.path == null) {
            throw new IllegalStateException("Recipe has no resource key");
        }

        return super.build();
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
}
