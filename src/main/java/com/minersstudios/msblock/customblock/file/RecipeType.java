package com.minersstudios.msblock.customblock.file;

import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum RecipeType {
    SHAPED(ShapedRecipe.class),
    SHAPELESS(ShapelessRecipe.class),
    FURNACE(FurnaceRecipe.class),
    BLAST_FURNACE(BlastingRecipe.class),
    SMOKER(SmokingRecipe.class),
    CAMPFIRE(CampfireRecipe.class),
    STONECUTTER(StonecuttingRecipe.class);

    private final Class<? extends Recipe> clazz;

    RecipeType(@NotNull Class<? extends Recipe> clazz) {
        this.clazz = clazz;
    }

    public @NotNull Class<? extends Recipe> getClazz() {
        return this.clazz;
    }

    public static @NotNull RecipeType valueOf(@NotNull Class<? extends Recipe> clazz) throws IllegalArgumentException {
        for (var recipeType : values()) {
            if (recipeType.getClazz() == clazz) return recipeType;
        }

        throw new IllegalArgumentException("Unknown recipe type: " + clazz);
    }

    public static @NotNull Class<? extends Recipe> clazzOf(@NotNull String type) throws IllegalArgumentException {
        return valueOf(type.toUpperCase()).getClazz();
    }

    public static @NotNull String nameOf(@NotNull Class<? extends Recipe> clazz) throws IllegalArgumentException {
        return valueOf(clazz).name().toLowerCase(Locale.ENGLISH);
    }
}
