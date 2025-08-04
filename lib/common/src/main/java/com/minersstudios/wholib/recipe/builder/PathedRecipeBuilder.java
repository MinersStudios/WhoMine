package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface PathedRecipeBuilder<R extends Recipe> extends RecipeBuilder<R> {

    @UnknownNullability
    ResourceKey path();

    @Contract("_ -> this")
    @NotNull PathedRecipeBuilder<R> path(final @NotNull ResourceKey resourceKey);
}
