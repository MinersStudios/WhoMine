package com.minersstudios.msblock.customblock.file.adapter;

import com.google.gson.*;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Locale;

public class RecipeChoiceAdapter implements JsonSerializer<RecipeChoice>, JsonDeserializer<RecipeChoice> {
    private static final String CHOICES_KEY = "choices";
    private static final String TYPE_KEY = "type";
    private static final String MATERIAL_CHOICE = "MATERIAL_CHOICE";
    private static final String EXACT_CHOICE = "EXACT_CHOICE";

    @Override
    public @NotNull RecipeChoice deserialize(
            @NotNull JsonElement json,
            @NotNull Type type,
            @NotNull JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String typeString = jsonObject.get(TYPE_KEY).getAsString().toUpperCase(Locale.ENGLISH);

        return context.deserialize(
                jsonObject,
                switch (typeString) {
                    case MATERIAL_CHOICE -> RecipeChoice.MaterialChoice.class;
                    case EXACT_CHOICE -> RecipeChoice.ExactChoice.class;
                    default -> throw new JsonParseException("Unknown RecipeChoice type: " + typeString);
                }
        );
    }

    @Override
    public @NotNull JsonElement serialize(
            @NotNull RecipeChoice src,
            @NotNull Type type,
            @NotNull JsonSerializationContext context
    ) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(
                TYPE_KEY,
                src instanceof RecipeChoice.MaterialChoice
                ? MATERIAL_CHOICE
                : EXACT_CHOICE
        );

        if (src instanceof RecipeChoice.MaterialChoice materialChoice) {
            jsonObject.add(CHOICES_KEY, context.serialize(materialChoice.getChoices()));
        } else if (src instanceof RecipeChoice.ExactChoice exactChoice) {
            jsonObject.add(CHOICES_KEY, context.serialize(exactChoice.getChoices()));
        } else {
            throw new IllegalArgumentException("Unknown RecipeChoice type: " + src.getClass().getName());
        }

        return jsonObject;
    }
}
