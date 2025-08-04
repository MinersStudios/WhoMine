package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.registrable.Registrable;
import org.jetbrains.annotations.NotNull;

public final class RecipeType implements Registrable<ResourceKey> {

    private final ResourceKey resourceKey;
    private final Class<? extends Recipe> recipeClass;

    RecipeType(
            final @NotNull ResourceKey resourceKey,
            final @NotNull Class<? extends Recipe> recipeClass
    ) {
        this.resourceKey = resourceKey;
        this.recipeClass = recipeClass;
    }

    @Override
    public @NotNull ResourceKey getKey() {
        return null;
    }
}
