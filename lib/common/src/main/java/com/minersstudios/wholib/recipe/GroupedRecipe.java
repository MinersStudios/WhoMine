package com.minersstudios.wholib.recipe;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface GroupedRecipe<R extends GroupedRecipe<R>> extends Recipe {

    @NotNull String getGroup();

    @Contract("_ -> this")
    @NotNull R setGroup(final @NotNull String group);
}
