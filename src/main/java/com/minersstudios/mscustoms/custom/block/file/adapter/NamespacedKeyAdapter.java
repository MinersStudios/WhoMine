package com.minersstudios.mscustoms.custom.block.file.adapter;

import com.google.gson.*;
import com.minersstudios.mscore.annotation.ResourceKey;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * Gson adapter for serializing and deserializing NamespacedKey objects.
 * <br>
 * This adapter handles NamespacedKey serialization by converting it into a
 * JsonObject, and deserialization by reading the JsonObject and constructing
 * the corresponding NamespacedKey with the default namespace and the key from
 * the JsonObject.
 * <br>
 * Serialized output you can see in the "MSCustoms/blocks/example.json" file.
 */
public class NamespacedKeyAdapter implements JsonSerializer<NamespacedKey>, JsonDeserializer<NamespacedKey> {
    private final String namespace;

    public NamespacedKeyAdapter(final @ResourceKey @NotNull String namespace) {
        this.namespace = namespace;
    }

    @Override
    public @NotNull NamespacedKey deserialize(
            final @NotNull JsonElement json,
            final @NotNull Type type,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            throw new JsonParseException(
                    "Expected JsonPrimitive for NamespacedKey, got " + json.getClass().getSimpleName()
            );
        }

        return new NamespacedKey(
                this.namespace,
                json.getAsString()
        );
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull NamespacedKey namespacedKey,
            final @NotNull Type type,
            final @NotNull JsonSerializationContext context
    ) {
        return new JsonPrimitive(namespacedKey.getKey());
    }
}
