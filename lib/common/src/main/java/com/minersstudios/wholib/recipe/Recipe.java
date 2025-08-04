package com.minersstudios.wholib.recipe;

import org.jetbrains.annotations.NotNull;

public interface Recipe {

    @NotNull Object getResult();

    <I> @NotNull I getResult(final @NotNull Class<I> type) throws ClassCastException;
}
