package com.minersstudios.msblock.api.file.adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.minersstudios.msblock.api.file.NoteBlockData;
import com.minersstudios.msblock.api.file.PlacingType;
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
 * <p>
 * Serialized output you can see in the "MSBlock/blocks/example.json" file.
 */
public class PlacingTypeAdapter implements JsonSerializer<PlacingType>, JsonDeserializer<PlacingType> {
    private static final String TYPE_KEY = "type";
    private static final String NOTE_BLOCK_DATA_KEY = "noteBlockData";
    private static final String DEFAULT_TYPE = "DEFAULT";
    private static final String DIRECTIONAL_TYPE = "DIRECTIONAL";
    private static final String ORIENTABLE_TYPE = "ORIENTABLE";
    private static final Type BLOCK_FACE_MAP_TYPE = new TypeToken<Map<BlockFace, NoteBlockData>>() {}.getType();
    private static final Type AXIS_NOTE_MAP_TYPE = new TypeToken<Map<Axis, NoteBlockData>>() {}.getType();

    @Override
    public @NotNull PlacingType deserialize(
            final @NotNull JsonElement json,
            final @NotNull Type typeOfT,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException, IllegalArgumentException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final String type = jsonObject.get(TYPE_KEY).getAsString();

        switch (type.toUpperCase(Locale.ENGLISH)) {
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
            default -> throw new IllegalArgumentException("Unknown PlacingType: " + type);
        }
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull PlacingType src,
            final @NotNull Type typeOfSrc,
            final @NotNull JsonSerializationContext context
    ) {
        final JsonObject jsonObject = new JsonObject();

        if (src instanceof final PlacingType.Default defaultType) {
            jsonObject.addProperty(TYPE_KEY, DEFAULT_TYPE);
            jsonObject.add(
                    NOTE_BLOCK_DATA_KEY,
                    context.serialize(defaultType.getNoteBlockData())
            );
        } else if (src instanceof final PlacingType.Directional directionalType) {
            jsonObject.addProperty(TYPE_KEY, DIRECTIONAL_TYPE);
            jsonObject.add(
                    NOTE_BLOCK_DATA_KEY,
                    context.serialize(
                            directionalType.getMap(),
                            BLOCK_FACE_MAP_TYPE
                    )
            );
        } else if (src instanceof final PlacingType.Orientable orientableType) {
            jsonObject.addProperty(TYPE_KEY, ORIENTABLE_TYPE);
            jsonObject.add(
                    NOTE_BLOCK_DATA_KEY,
                    context.serialize(
                            orientableType.getMap(),
                            AXIS_NOTE_MAP_TYPE
                    )
            );
        }

        return jsonObject;
    }
}
