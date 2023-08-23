package com.minersstudios.msblock.customblock.file.adapter;

import com.google.gson.*;
import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Gson adapter for serializing and deserializing NamespacedKey objects.
 * This adapter handles NamespacedKey serialization by converting it into
 * a JsonObject, and deserialization by reading the JsonObject and
 * constructing the corresponding NamespacedKey with the default namespace
 * and the key from the JsonObject.
 * <p>
 * Serialized output you can see in the "MSBlock/blocks/example.json" file.
 */
public class NamespacedKeyAdapter implements JsonSerializer<NamespacedKey>, JsonDeserializer<NamespacedKey> {

    @Override
    public @NotNull NamespacedKey deserialize(
            final @NotNull JsonElement json,
            final @NotNull Type typeOfT,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException {
        return new NamespacedKey(
                CustomBlockRegistry.NAMESPACE,
                json.getAsString()
        );
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull NamespacedKey src,
            final @NotNull Type typeOfSrc,
            final @NotNull JsonSerializationContext context
    ) {
        return new JsonPrimitive(src.getKey());
    }
}
