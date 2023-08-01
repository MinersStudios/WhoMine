package com.minersstudios.msblock.customblock.file.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Locale;

/**
 * Custom JSON deserializer for enum types. This class allows Gson to
 * deserialize enum values case-insensitively, converting the JSON
 * string to the corresponding enum constant in a case-insensitive
 * manner.
 *
 * @param <T> The enum type to deserialize
 */
public class EnumDeserializer<T extends Enum<T>> implements JsonDeserializer<T> {
    private final Class<T> enumClass;

    /**
     * Constructs the EnumDeserializer for the specified enum class
     *
     * @param enumClass The class of the enum to be deserialized
     */
    public EnumDeserializer(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T deserialize(
            @NotNull JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
    ) throws JsonParseException, IllegalArgumentException {
        return Enum.valueOf(
                this.enumClass,
                json.getAsString().toUpperCase(Locale.ENGLISH)
        );
    }
}
