package com.minersstudios.msblock.customblock.file.adapter;

import com.google.gson.*;
import com.minersstudios.msblock.customblock.file.RecipeType;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class RecipeAdapter implements JsonSerializer<Recipe>, JsonDeserializer<Recipe> {
    private static final String TYPE_KEY = "type";
    private static final String RECIPE_KEY = "recipe";

    @Override
    public @NotNull Recipe deserialize(
            @NotNull JsonElement json,
            @NotNull Type typeOfT,
            @NotNull JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement recipeElement = jsonObject.get(RECIPE_KEY);
        String type = jsonObject.get(TYPE_KEY).getAsString();

        return context.deserialize(recipeElement, RecipeType.clazzOf(type));
    }

    @Override
    public @NotNull JsonElement serialize(
            @NotNull Recipe src,
            @NotNull Type typeOfSrc,
            @NotNull JsonSerializationContext context
    ) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(TYPE_KEY, RecipeType.nameOf(src.getClass()));
        jsonObject.add(RECIPE_KEY, context.serialize(src));

        return jsonObject;
    }
}
