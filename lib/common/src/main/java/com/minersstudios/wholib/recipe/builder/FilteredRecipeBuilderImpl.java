package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.FilteredRecipe;
import com.minersstudios.wholib.recipe.category.RecipeCategory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

@SuppressWarnings("unchecked")
abstract class FilteredRecipeBuilderImpl<B extends FilteredRecipeBuilder<R, C>, R extends FilteredRecipe<R, C>, C extends RecipeCategory>
        extends RecipeBuilderImpl<B, R>
        implements FilteredRecipeBuilder<R, C> {

    private String group;
    private C category;

    protected FilteredRecipeBuilderImpl(final @NotNull Function<B, R> recipeConstructor) {
        super(recipeConstructor);

        this.group = "";
    }

    protected FilteredRecipeBuilderImpl(
            final @NotNull Function<B, R> recipeConstructor,
            final @NotNull R recipe
    ) {
        super(recipeConstructor, recipe);

        this.group = recipe.getGroup();
        this.category = recipe.getCategory();
    }

    @Contract(" -> new")
    @Override
    public @NotNull R build() throws IllegalStateException {
        if (this.category == null) {
            throw new IllegalStateException("Recipe has no category");
        }

        return super.build()
                    .setGroup(this.group)
                    .setCategory(this.category);
    }

    @Override
    public final @NotNull String group() {
        return this.group;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B group(final @NotNull String group) {
        this.group = group;

        return (B) this;
    }

    @Override
    public final @UnknownNullability C category() {
        return this.category;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B category(final @NotNull C category) {
        this.category = category;

        return (B) this;
    }
}
