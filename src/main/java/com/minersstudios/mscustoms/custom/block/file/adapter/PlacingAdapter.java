package com.minersstudios.mscustoms.custom.block.file.adapter;

import com.google.gson.*;
import com.minersstudios.mscustoms.custom.block.params.PlacingType;
import com.minersstudios.mscustoms.custom.block.params.settings.Placing;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

/**
 * Gson adapter for serializing and deserializing Placing objects.
 * <br>
 * Serialized output you can see in the "MSCustoms/blocks/example.json" file.
 */
public class PlacingAdapter implements JsonSerializer<Placing>, JsonDeserializer<Placing> {

    @Override
    public @NotNull Placing deserialize(
            final @NotNull JsonElement json,
            final @NotNull Type type,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            throw new JsonParseException(
                    "Expected JsonObject for Placing, got " + json.getClass().getSimpleName()
            );
        }

        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement typeElement = jsonObject.get("type");

        if (typeElement == null) {
            throw new JsonParseException("Missing placing type");
        }

        final JsonElement placeableMaterials = jsonObject.get("placeableMaterials");
        final Set<Material> materialSet;

        if (placeableMaterials == null) {
            materialSet = Collections.emptySet();
        } else {
            materialSet = context.deserialize(placeableMaterials, Set.class);
        }

        final PlacingType placingType = context.deserialize(typeElement, PlacingType.class);

        return Placing.create(
                placingType,
                materialSet
        );
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull Placing placing,
            final @NotNull Type type,
            final @NotNull JsonSerializationContext context
    ) {
        return context.serialize(placing.getType());
    }
}
