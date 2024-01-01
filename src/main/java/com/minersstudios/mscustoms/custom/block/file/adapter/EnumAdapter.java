package com.minersstudios.mscustoms.custom.block.file.adapter;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

/**
 * Gson adapter for serializing and deserializing enum constants. This adapter
 * handles enum serialization by converting it into a JsonPrimitive, and
 * deserialization by reading the JsonPrimitive and constructing the
 * corresponding enum constant.
 * <br>
 * Serialized output you can see in the "MSBlock/blocks/example.json" file.
 */
public class EnumAdapter<E extends Enum<E>> implements JsonSerializer<E>, JsonDeserializer<E> {
    private final Map<String, E> enumMap;

    public EnumAdapter(final E @NotNull [] enumConstants) {
        this.enumMap = new Object2ObjectOpenHashMap<>(enumConstants.length);

        for (final var enumConstant : enumConstants) {
            this.enumMap.put(
                    enumConstant.name().toUpperCase(Locale.ENGLISH),
                    enumConstant
            );
        }
    }

    @Override
    public @NotNull E deserialize(
            final @NotNull JsonElement json,
            final @Nullable Type type,
            final @Nullable JsonDeserializationContext context
    ) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            throw new JsonParseException(
                    "Expected JsonPrimitive for " + type + ", got " + json.getClass().getSimpleName()
            );
        }

        final var enumConstant = this.enumMap.get(
                json.getAsString().toUpperCase(Locale.ENGLISH)
        );

        if (enumConstant == null) {
            throw new JsonParseException("Invalid enum constant: " + json.getAsString());
        }

        return enumConstant;
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull E enumValue,
            final @NotNull Type type,
            final @NotNull JsonSerializationContext context
    ) {
        return new JsonPrimitive(enumValue.name());
    }
}
