package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.key.ResourceKey;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPathedRecipe
        extends AbstractRecipe
        implements PathedRecipe {

    private final ResourceKey path;

    protected AbstractPathedRecipe(
            final @NotNull ResourceKey path,
            final @NotNull Object result
    ) {
        super(result);

        this.path = path;
    }

    @Override
    public @NotNull ResourceKey getResourceKey() {
        return this.path;
    }
}
