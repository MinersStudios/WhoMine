package com.minersstudios.msblock.customblock.file.adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.minersstudios.msblock.customblock.file.CustomBlockFile;
import com.minersstudios.msblock.customblock.file.RecipeEntry;
import com.minersstudios.msblock.customblock.file.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * Gson adapter for serializing and deserializing Recipe objects.
 * This adapter handles Recipe serialization by converting it into
 * a JsonObject, and deserialization by reading the JsonObject and
 * constructing the corresponding Recipe object.
 * <p>
 * Serialized output you can see in the "MSBlock/blocks/example.json" file.
 *
 * @see RecipeType
 */
public class RecipeAdapter implements JsonSerializer<Recipe>, JsonDeserializer<Recipe> {
    private static final String TYPE_KEY = "type";
    private static final String RECIPE_KEY = "recipe";
    private static final String OUTPUT_KEY = "output";
    private static final String AMOUNT_KEY = "amount";
    private static final Type RECIPE_ENTRY_SET_TYPE = new TypeToken<Set<RecipeEntry>>() {}.getType();

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
        JsonObject recipeObject = context.serialize(src).getAsJsonObject();

        jsonObject.addProperty(TYPE_KEY, RecipeType.nameOf(src.getClass()));
        recipeObject.remove(OUTPUT_KEY);
        recipeObject.addProperty(AMOUNT_KEY, src.getResult().getAmount());
        jsonObject.add(RECIPE_KEY, recipeObject);

        return jsonObject;
    }

    public static @NotNull Set<RecipeEntry> deserializeEntries(
            @NotNull ItemStack output,
            @NotNull JsonArray json
    ) {
        for (var element : json) {
            JsonObject outputObj = CustomBlockFile.getGson().toJsonTree(output).getAsJsonObject();
            JsonObject recipeEntryObject = element.getAsJsonObject();
            JsonObject recipeTypeObject = recipeEntryObject.get(RECIPE_KEY).getAsJsonObject();
            JsonObject recipeObject = recipeTypeObject.get(RECIPE_KEY).getAsJsonObject();
            JsonElement amountElement = recipeObject.get(AMOUNT_KEY);

            if (amountElement != null) {
                outputObj.add(AMOUNT_KEY, amountElement);
            }

            if (!recipeObject.has(OUTPUT_KEY)) {
                recipeObject.add(
                        OUTPUT_KEY,
                        outputObj
                );
            }
        }

        return CustomBlockFile.getGson().fromJson(json, RECIPE_ENTRY_SET_TYPE);
    }

    public static @NotNull JsonElement serializeEntries(@NotNull Set<RecipeEntry> entries) {
        return CustomBlockFile.getGson().toJsonTree(entries, RECIPE_ENTRY_SET_TYPE);
    }
}
