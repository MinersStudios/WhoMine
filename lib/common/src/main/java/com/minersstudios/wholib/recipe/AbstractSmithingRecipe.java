package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSmithingRecipe
        extends AbstractPathedRecipe
        implements SmithingRecipe {

    private final RecipeChoice<?> template;
    private final RecipeChoice<?> base;
    private final RecipeChoice<?> addition;
    private final boolean copyDataComponents;

    protected AbstractSmithingRecipe(
            final @NotNull ResourceKey path,
            final @NotNull Object result,
            final @NotNull RecipeChoice<?> template,
            final @NotNull RecipeChoice<?> base,
            final @NotNull RecipeChoice<?> addition,
            final boolean copyDataComponents
    ) {
        super(path, result);

        this.template = template;
        this.base = base;
        this.addition = addition;
        this.copyDataComponents = copyDataComponents;
    }

    @Override
    public @NotNull RecipeChoice<?> getTemplate() {
        return this.template;
    }

    @Override
    public @NotNull RecipeChoice<?> getBase() {
        return this.base;
    }

    @Override
    public @NotNull RecipeChoice<?> getAddition() {
        return this.addition;
    }

    @Override
    public boolean willCopyDataComponents() {
        return this.copyDataComponents;
    }
}
