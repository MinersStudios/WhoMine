package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.recipe.category.RecipeCategory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFilteredRecipe<R extends FilteredRecipe<R, C>, C extends RecipeCategory>
        extends AbstractRecipe
        implements FilteredRecipe<R, C> {

    private C category;
    private String group;

    protected AbstractFilteredRecipe(
            final @NotNull Object result,
            final @NotNull C category,
            final @NotNull String group
    ) {
        super(result);

        this.category = category;
        this.group = group;
    }

    @Override
    public @NotNull C getCategory() {
        return this.category;
    }

    @SuppressWarnings("unchecked")
    @Contract("_ -> this")
    @Override
    public @NotNull R setCategory(final @NotNull C category) {
        this.category = category;

        return (R) this;
    }

    @Override
    public @NotNull String getGroup() {
        return this.group;
    }

    @SuppressWarnings("unchecked")
    @Contract("_ -> this")
    @Override
    public @NotNull R setGroup(final @NotNull String group) {
        this.group = group;

        return (R) this;
    }
}
