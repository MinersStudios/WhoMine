package com.minersstudios.wholib.recipe.builder;

import com.minersstudios.wholib.recipe.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unchecked")
abstract class RecipeBuilderImpl<B extends RecipeBuilder<R>, R extends Recipe> implements RecipeBuilder<R> {
    static final Map<Class<? extends Recipe>, Function<? extends Recipe, RecipeBuilder<?>>> BUILDER_TYPES;

    static {
        BUILDER_TYPES = new Object2ObjectOpenHashMap<>();

        RecipeBuilder.registerType(ShapedRecipe.class, RecipeBuilder::shaped);
        RecipeBuilder.registerType(ShapelessRecipe.class, RecipeBuilder::shapeless);
        RecipeBuilder.registerType(FurnaceRecipe.class, RecipeBuilder::furnace);
        RecipeBuilder.registerType(SmokingRecipe.class, RecipeBuilder::smoking);
        RecipeBuilder.registerType(BlastingRecipe.class, RecipeBuilder::blasting);
        RecipeBuilder.registerType(CampfireRecipe.class, RecipeBuilder::campfire);
        RecipeBuilder.registerType(StonecuttingRecipe.class, RecipeBuilder::stonecutting);
        RecipeBuilder.registerType(SmithingTransformRecipe.class, RecipeBuilder::smithingTransform);
        RecipeBuilder.registerType(SmithingTrimRecipe.class, RecipeBuilder::smithingTrim);
    }

    private final Function<B, R> recipeConstructor;
    private Object result;

    protected RecipeBuilderImpl(
            final @NotNull Function<B, R> recipeConstructor,
            final @NotNull R recipe
    ) {
        this(recipeConstructor);

        this.result = recipe.getResult();
    }

    protected RecipeBuilderImpl(final @NotNull Function<B, R> recipeConstructor) {
        this.recipeConstructor = recipeConstructor;
    }

    @Contract(" -> new")
    @Override
    public @NotNull R build() throws IllegalStateException {
        if (this.result == null) {
            throw new IllegalStateException("Recipe has no result");
        }

        return this.recipeConstructor.apply((B) this);
    }

    @Override
    public final @UnknownNullability Object result() {
        return this.result;
    }

    @Contract("_ -> this")
    @Override
    public final @NotNull B result(final @NotNull Object result) throws IllegalArgumentException {
        this.result = result;

        return (B) this;
    }
}
