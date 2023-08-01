package com.minersstudios.msblock.customblock.file.adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.minersstudios.msblock.customblock.file.CustomChoice;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

/**
 * Gson adapter for serializing and deserializing RecipeChoice objects.
 * This adapter handles RecipeChoice serialization by converting it into
 * a JsonObject, and deserialization by reading the JsonObject and
 * constructing the corresponding RecipeChoice object.
 * <p>
 * Serialized output you can see in the "MSBlock/blocks/example.json" file.
 */
public class RecipeChoiceAdapter implements JsonSerializer<RecipeChoice>, JsonDeserializer<RecipeChoice> {
    private static final String CHOICES_KEY = "choices";
    private static final String TYPE_KEY = "type";
    private static final String MATERIAL_CHOICE = "MATERIAL_CHOICE";
    private static final String EXACT_CHOICE = "EXACT_CHOICE";
    private static final String CUSTOM_CHOICE = "CUSTOM_CHOICE";
    private static final Type NAMESPACED_LIST_TYPE = new TypeToken<List<String>>() {}.getType();

    @Override
    public @NotNull RecipeChoice deserialize(
            @NotNull JsonElement json,
            @NotNull Type type,
            @NotNull JsonDeserializationContext context
    ) throws JsonParseException, IllegalArgumentException {
        JsonObject jsonObject = json.getAsJsonObject();
        String typeString = jsonObject.get(TYPE_KEY).getAsString().toUpperCase(Locale.ENGLISH);

        switch (typeString) {
            case MATERIAL_CHOICE -> {
                return context.deserialize(
                        jsonObject,
                        RecipeChoice.MaterialChoice.class
                );
            }
            case EXACT_CHOICE -> {
                return context.deserialize(
                        jsonObject,
                        RecipeChoice.ExactChoice.class
                );
            }
            case CUSTOM_CHOICE -> {
                List<String> namespacedKeys = context.deserialize(
                        jsonObject.get(CHOICES_KEY),
                        NAMESPACED_LIST_TYPE
                );
                return new CustomChoice(namespacedKeys).toExactChoice();
            }
            default -> throw new IllegalArgumentException("Unknown RecipeChoice type: " + typeString);
        }
    }

    @Override
    public @NotNull JsonElement serialize(
            @NotNull RecipeChoice src,
            @NotNull Type type,
            @NotNull JsonSerializationContext context
    ) throws IllegalArgumentException {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(
                TYPE_KEY,
                src instanceof RecipeChoice.MaterialChoice
                ? MATERIAL_CHOICE
                : src instanceof RecipeChoice.ExactChoice
                ? EXACT_CHOICE
                : CUSTOM_CHOICE
        );

        if (src instanceof RecipeChoice.MaterialChoice materialChoice) {
            jsonObject.add(CHOICES_KEY, context.serialize(materialChoice.getChoices()));
        } else if (src instanceof RecipeChoice.ExactChoice exactChoice) {
            jsonObject.add(CHOICES_KEY, context.serialize(exactChoice.getChoices()));
        } else if (src instanceof CustomChoice customChoice) {
            jsonObject.add(CHOICES_KEY, context.serialize(customChoice.getNamespacedKeys()));
        } else {
            throw new IllegalArgumentException("Unknown RecipeChoice type: " + src.getClass().getName());
        }

        return jsonObject;
    }
}
