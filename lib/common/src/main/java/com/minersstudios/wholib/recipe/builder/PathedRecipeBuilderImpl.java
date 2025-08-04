package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.PathedRecipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

@SuppressWarnings("unchecked")
abstract class PathedRecipeBuilderImpl<B extends PathedRecipeBuilder<R>, R extends PathedRecipe>
        extends RecipeBuilderImpl<B, R>
        implements PathedRecipeBuilder<R> {

    private ResourceKey path;

    protected PathedRecipeBuilderImpl(final @NotNull Function<B, R> recipeConstructor) {
        super(recipeConstructor);
    }

    protected PathedRecipeBuilderImpl(
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

    public final @UnknownNullability ResourceKey path() {
        return this.path;
    }

    @Contract("_ -> this")
    public final @NotNull B path(final @NotNull ResourceKey path) {
        this.path = path;

        return (B) this;
    }
}
