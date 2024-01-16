package com.minersstudios.mscustoms.custom.block.file.adapter;

import com.google.gson.*;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class RecipeEntryAdapter implements JsonSerializer<RecipeEntry>, JsonDeserializer<RecipeEntry> {
    private static final String RECIPE_KEY =              "recipe";
    private static final String SHOW_IN_CRAFTS_MENU_KEY = "showInCraftsMenu";

    @Override
    public @NotNull RecipeEntry deserialize(
            final @NotNull JsonElement json,
            final @NotNull Type type,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement recipeElement = jsonObject.get(RECIPE_KEY);

        if (recipeElement == null) {
            throw new JsonParseException("Missing recipe");
        }

        final JsonElement showInCraftsMenuElement = jsonObject.get(SHOW_IN_CRAFTS_MENU_KEY);

        if (showInCraftsMenuElement == null) {
            throw new JsonParseException("Missing showInCraftsMenu");
        }

        return RecipeEntry.of(
                (Recipe) context.deserialize(
                        recipeElement,
                        Recipe.class
                ),
                showInCraftsMenuElement.getAsBoolean()
        );
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull RecipeEntry entry,
            final @NotNull Type type,
            final @NotNull JsonSerializationContext context
    ) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.add(RECIPE_KEY, context.serialize(entry.getRecipe()));
        jsonObject.addProperty(SHOW_IN_CRAFTS_MENU_KEY, entry.isRegisteredInMenu());

        return jsonObject;
    }
}
