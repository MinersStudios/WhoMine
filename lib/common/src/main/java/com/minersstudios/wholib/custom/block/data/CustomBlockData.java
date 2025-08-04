package com.minersstudios.wholib.custom.block.data;

import com.minersstudios.wholib.property.PropertyStorage;
import com.minersstudios.wholib.recipe.entry.RecipeEntry;
import com.minersstudios.wholib.registrable.Registrable;
import com.minersstudios.wholib.key.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface CustomBlockData extends Registrable<ResourceKey> {

    @NotNull PropertyStorage getPropertyStorage();

    @NotNull DropData getDropData();

    @Unmodifiable @NotNull List<RecipeEntry> getRecipeEntries();
}
