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
            final @NotNull JsonElement json,
            final @NotNull Type typeOfT,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement recipeElement = jsonObject.get(RECIPE_KEY);
        final String type = jsonObject.get(TYPE_KEY).getAsString();

        return context.deserialize(recipeElement, RecipeType.clazzOf(type));
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull Recipe src,
            final @NotNull Type typeOfSrc,
            final @NotNull JsonSerializationContext context
    ) {
        final JsonObject jsonObject = new JsonObject();
        final JsonObject recipeObject = context.serialize(src).getAsJsonObject();

        jsonObject.addProperty(TYPE_KEY, RecipeType.nameOf(src.getClass()));
        recipeObject.remove(OUTPUT_KEY);
        recipeObject.addProperty(AMOUNT_KEY, src.getResult().getAmount());
        jsonObject.add(RECIPE_KEY, recipeObject);

        return jsonObject;
    }

    public static @NotNull Set<RecipeEntry> deserializeEntries(
            final @NotNull ItemStack output,
            final @NotNull JsonArray json
    ) {
        for (final var element : json) {
            final JsonObject recipeObject =
                    element.getAsJsonObject()
                    .get(RECIPE_KEY).getAsJsonObject()
                    .get(RECIPE_KEY).getAsJsonObject();
            final JsonObject outputObj = CustomBlockFile.getGson().toJsonTree(output).getAsJsonObject();
            final JsonElement amountElement = recipeObject.get(AMOUNT_KEY);

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

    public static @NotNull JsonElement serializeEntries(final @NotNull Set<RecipeEntry> entries) {
        return CustomBlockFile.getGson().toJsonTree(entries, RECIPE_ENTRY_SET_TYPE);
    }
}
