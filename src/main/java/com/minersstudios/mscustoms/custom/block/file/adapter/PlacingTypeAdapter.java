package com.minersstudios.mscustoms.custom.block.file.adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.minersstudios.mscustoms.custom.block.params.NoteBlockData;
import com.minersstudios.mscustoms.custom.block.params.PlacingType;
import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

/**
 * Gson adapter for serializing and deserializing PlacingType objects.
 * This adapter handles PlacingType serialization by converting it into
 * a JsonObject, and deserialization by reading the JsonObject and
 * constructing the corresponding PlacingType.
 * <br>
 * Serialized output you can see in the "MSBlock/blocks/example.json" file.
 */
public class PlacingTypeAdapter implements JsonSerializer<PlacingType>, JsonDeserializer<PlacingType> {
    private static final String TYPE_KEY =            "type";
    private static final String NOTE_BLOCK_DATA_KEY = "noteBlockData";
    private static final String DEFAULT_TYPE =        "DEFAULT";
    private static final String DIRECTIONAL_TYPE =    "DIRECTIONAL";
    private static final String ORIENTABLE_TYPE =     "ORIENTABLE";

    private static final Type BLOCK_FACE_MAP_TYPE = new TypeToken<Map<BlockFace, NoteBlockData>>() {}.getType();
    private static final Type AXIS_NOTE_MAP_TYPE = new TypeToken<Map<Axis, NoteBlockData>>() {}.getType();

    @Override
    public @NotNull PlacingType deserialize(
            final @NotNull JsonElement json,
            final @NotNull Type type,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException, IllegalArgumentException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement typeElement = jsonObject.get(TYPE_KEY);

        if (typeElement == null) {
            throw new JsonParseException("Missing type");
        }

        final String typeString = typeElement.getAsString().toUpperCase(Locale.ENGLISH);

        switch (typeString) {
            case DEFAULT_TYPE -> {
                return PlacingType.defaultType(
                        context.deserialize(
                                jsonObject.getAsJsonObject(NOTE_BLOCK_DATA_KEY),
                                NoteBlockData.class
                        )
                );
            }
            case DIRECTIONAL_TYPE -> {
                return PlacingType.directionalType(
                        context.deserialize(
                                jsonObject.get(NOTE_BLOCK_DATA_KEY),
                                BLOCK_FACE_MAP_TYPE
                        )
                );
            }
            case ORIENTABLE_TYPE -> {
                return PlacingType.orientableType(
                        context.deserialize(
                                jsonObject.get(NOTE_BLOCK_DATA_KEY),
                                AXIS_NOTE_MAP_TYPE
                        )
                );
            }
            default -> throw new IllegalArgumentException("Unknown placing type : " + typeString);
        }
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull PlacingType placingType,
            final @NotNull Type type,
            final @NotNull JsonSerializationContext context
    ) throws UnsupportedOperationException {
        final JsonObject jsonObject = new JsonObject();

        if (placingType instanceof final PlacingType.Default defaultType) {
            jsonObject.addProperty(TYPE_KEY, DEFAULT_TYPE);
            jsonObject.add(
                    NOTE_BLOCK_DATA_KEY,
                    context.serialize(defaultType.getNoteBlockData())
            );
        } else if (placingType instanceof final PlacingType.Directional directionalType) {
            jsonObject.addProperty(TYPE_KEY, DIRECTIONAL_TYPE);
            jsonObject.add(
                    NOTE_BLOCK_DATA_KEY,
                    context.serialize(
                            directionalType.getMap(),
                            BLOCK_FACE_MAP_TYPE
                    )
            );
        } else if (placingType instanceof final PlacingType.Orientable orientableType) {
            jsonObject.addProperty(TYPE_KEY, ORIENTABLE_TYPE);
            jsonObject.add(
                    NOTE_BLOCK_DATA_KEY,
                    context.serialize(
                            orientableType.getMap(),
                            AXIS_NOTE_MAP_TYPE
                    )
            );
        } else {
            throw new UnsupportedOperationException("Unknown placing type : " + placingType.getClass());
        }

        return jsonObject;
    }
}
