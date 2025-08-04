package com.minersstudios.wholib.recipe;

import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public interface SmithingRecipe extends PathedRecipe {
    boolean DEFAULT_COPY_DATA_COMPONENTS = true;

    @NotNull RecipeChoice<?> getTemplate();

    @NotNull RecipeChoice<?> getBase();

    @NotNull RecipeChoice<?> getAddition();

    boolean willCopyDataComponents();
}
