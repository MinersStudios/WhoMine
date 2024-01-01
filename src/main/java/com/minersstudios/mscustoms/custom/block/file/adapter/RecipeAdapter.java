package com.minersstudios.mscustoms.custom.block.file.adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.minersstudios.mscustoms.custom.block.file.CustomBlockFile;
import com.minersstudios.mscore.inventory.recipe.RecipeEntry;
import com.minersstudios.mscustoms.custom.block.params.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * Gson adapter for serializing and deserializing Recipe objects.
 * This adapter handles Recipe serialization by converting it into
 * a JsonObject, and deserialization by reading the JsonObject and
 * constructing the corresponding Recipe object.
 * <br>
 * Serialized output you can see in the "MSBlock/blocks/example.json" file.
 *
 * @see RecipeType
 */
public class RecipeAdapter implements JsonSerializer<Recipe>, JsonDeserializer<Recipe> {
    private static final String TYPE_KEY =   "type";
    private static final String RECIPE_KEY = "recipe";
    private static final String OUTPUT_KEY = "output";
    private static final String AMOUNT_KEY = "amount";

    private static final Type RECIPE_ENTRY_LIST_TYPE = new TypeToken<List<RecipeEntry>>() {}.getType();

    @Override
    public @NotNull Recipe deserialize(
            final @NotNull JsonElement json,
            final @NotNull Type type,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException, IllegalArgumentException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement typeElement = jsonObject.get(TYPE_KEY);

        if (typeElement == null) {
            throw new JsonParseException("Missing type");
        }

        final JsonElement recipeElement = jsonObject.get(RECIPE_KEY);

        if (recipeElement == null) {
            throw new JsonParseException("Missing recipe");
        }

        return context.deserialize(
                recipeElement,
                RecipeType.clazzOf(typeElement.getAsString())
        );
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull Recipe recipe,
            final @NotNull Type type,
            final @NotNull JsonSerializationContext context
    ) throws IllegalArgumentException {
        final JsonObject jsonObject = new JsonObject();
        final JsonObject recipeObject = context.serialize(recipe).getAsJsonObject();

        jsonObject.addProperty(TYPE_KEY, RecipeType.nameOf(recipe.getClass()));
        recipeObject.remove(OUTPUT_KEY);
        recipeObject.addProperty(AMOUNT_KEY, recipe.getResult().getAmount());
        jsonObject.add(RECIPE_KEY, recipeObject);

        return jsonObject;
    }

    public static @NotNull List<RecipeEntry> deserializeEntries(
            final @NotNull ItemStack output,
            final @NotNull JsonArray json
    ) {
        for (final var element : json) {
            final JsonObject recipeObject =
                    element.getAsJsonObject()
                    .get(RECIPE_KEY).getAsJsonObject()
                    .get(RECIPE_KEY).getAsJsonObject();
            final JsonObject outputObj = CustomBlockFile.gson().toJsonTree(output).getAsJsonObject();
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

        return CustomBlockFile.gson().fromJson(json, RECIPE_ENTRY_LIST_TYPE);
    }

    public static @NotNull JsonElement serializeEntries(final @NotNull Set<RecipeEntry> entries) {
        return CustomBlockFile.gson().toJsonTree(entries, RECIPE_ENTRY_LIST_TYPE);
    }
}
