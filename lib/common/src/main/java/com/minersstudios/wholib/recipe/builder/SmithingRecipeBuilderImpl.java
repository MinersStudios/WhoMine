package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.SmithingRecipe;
import com.minersstudios.wholib.recipe.choice.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

@SuppressWarnings("unchecked")
abstract class SmithingRecipeBuilderImpl<B extends SmithingRecipeBuilder<R>, R extends SmithingRecipe>
        extends PathedRecipeBuilderImpl<B, R>
        implements SmithingRecipeBuilder<R> {

    private RecipeChoice<?> template;
    private RecipeChoice<?> base;
    private RecipeChoice<?> addition;
    private boolean copyDataComponents;

    protected SmithingRecipeBuilderImpl(final @NotNull Function<B, R> recipeConstructor) {
        super(recipeConstructor);

        this.copyDataComponents = SmithingRecipe.DEFAULT_COPY_DATA_COMPONENTS;
    }

    protected SmithingRecipeBuilderImpl(
            final @NotNull Function<B, R> recipeConstructor,
            final @NotNull R recipe
    ) {
        super(recipeConstructor, recipe);

        this.template = recipe.getTemplate();
        this.base = recipe.getBase();
        this.addition = recipe.getAddition();
        this.copyDataComponents = recipe.willCopyDataComponents();
    }

    @Contract(" -> new")
    @Override
    public final @NotNull R build() throws IllegalStateException {
        if (this.template == null) {
            throw new IllegalStateException("Recipe has no template");
        }

        if (this.base == null) {
            throw new IllegalStateException("Recipe has no base");
        }

        if (this.addition == null) {
            throw new IllegalStateException("Recipe has no addition");
        }

        return super.build();
    }

    @Override
    public final @UnknownNullability RecipeChoice<?> template() {
        return this.template;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B template(final @NotNull RecipeChoice<?> template) {
        this.template = template;

        return (B) this;
    }

    @Override
    public final @UnknownNullability RecipeChoice<?> base() {
        return this.base;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B base(final @NotNull RecipeChoice<?> base) {
        this.base = base;

        return (B) this;
    }

    @Override
    public final @UnknownNullability RecipeChoice<?> addition() {
        return this.addition;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B addition(final @NotNull RecipeChoice<?> addition) {
        this.addition = addition;

        return (B) this;
    }

    @Override
    public final boolean copyDataComponents() {
        return this.copyDataComponents;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B copyDataComponents(final boolean state) {
        this.copyDataComponents = state;

        return (B) this;
    }
}
