package com.minersstudios.wholib.recipe;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractRecipe implements Recipe {

    private final Object result;

    protected AbstractRecipe(final @NotNull Object result) {
        this.result = result;
    }

    @Override
    public @NotNull Object getResult() {
        return this.result;
    }

    @Override
    public <I> @NotNull I getResult(final @NotNull Class<I> type) throws ClassCastException {
        return type.cast(this.result);
    }
}
