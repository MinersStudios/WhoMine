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
            final @NotNull JsonElement json,
            final @NotNull Type type,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException, IllegalArgumentException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final String typeString = jsonObject.get(TYPE_KEY).getAsString().toUpperCase(Locale.ENGLISH);

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
                final List<String> namespacedKeys = context.deserialize(
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
            final @NotNull RecipeChoice src,
            final @NotNull Type type,
            final @NotNull JsonSerializationContext context
    ) throws IllegalArgumentException {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(
                TYPE_KEY,
                src instanceof RecipeChoice.MaterialChoice
                ? MATERIAL_CHOICE
                : src instanceof RecipeChoice.ExactChoice
                ? EXACT_CHOICE
                : CUSTOM_CHOICE
        );

        if (src instanceof final RecipeChoice.MaterialChoice materialChoice) {
            jsonObject.add(CHOICES_KEY, context.serialize(materialChoice.getChoices()));
        } else if (src instanceof final RecipeChoice.ExactChoice exactChoice) {
            jsonObject.add(CHOICES_KEY, context.serialize(exactChoice.getChoices()));
        } else if (src instanceof final CustomChoice customChoice) {
            jsonObject.add(CHOICES_KEY, context.serialize(customChoice.getNamespacedKeys()));
        } else {
            throw new IllegalArgumentException("Unknown RecipeChoice type: " + src.getClass().getName());
        }

        return jsonObject;
    }
}
