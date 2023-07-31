package com.minersstudios.msblock.customblock.file;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

public class PlacingTypeAdapter implements JsonSerializer<PlacingType>, JsonDeserializer<PlacingType> {
    private static final String TYPE_KEY = "type";
    private static final String NOTE_BLOCK_DATA_KEY = "noteBlockData";
    private static final Type NOTE_BLOCK_DATA_TYPE = new TypeToken<NoteBlockData>() {}.getType();
    private static final Type BLOCK_FACE_NOTE_BLOCK_DATA_MAP_TYPE = new TypeToken<Map<BlockFace, NoteBlockData>>() {}.getType();
    private static final Type AXIS_NOTE_BLOCK_DATA_MAP_TYPE = new TypeToken<Map<Axis, NoteBlockData>>() {}.getType();

    @Override
    public @NotNull PlacingType deserialize(
            @NotNull JsonElement json,
            @NotNull Type typeOfT,
            @NotNull JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get(TYPE_KEY).getAsString();

        switch (type) {
            case "default" -> {
                return new PlacingType.Default(
                        context.deserialize(
                                jsonObject.getAsJsonObject(NOTE_BLOCK_DATA_KEY),
                                NOTE_BLOCK_DATA_TYPE
                        )
                );
            }
            case "directional" -> {
                return new PlacingType.Directional(
                        context.deserialize(
                                jsonObject.get(NOTE_BLOCK_DATA_KEY),
                                BLOCK_FACE_NOTE_BLOCK_DATA_MAP_TYPE
                        )
                );
            }
            case "orientable" -> {
                return new PlacingType.Orientable(
                        context.deserialize(
                                jsonObject.get(NOTE_BLOCK_DATA_KEY),
                                AXIS_NOTE_BLOCK_DATA_MAP_TYPE
                        )
                );
            }
            default -> throw new JsonParseException("Unknown PlacingType: " + type);
        }
    }

    @Override
    public @NotNull JsonElement serialize(
            @NotNull PlacingType src,
            @NotNull Type typeOfSrc,
            @NotNull JsonSerializationContext context
    ) {
        JsonObject jsonObject = new JsonObject();

        if (src instanceof PlacingType.Default defaultType) {
            jsonObject.addProperty(TYPE_KEY, "default");
            jsonObject.add(
                    NOTE_BLOCK_DATA_KEY,
                    context.serialize(defaultType.getNoteBlockData())
            );
        } else if (src instanceof PlacingType.Directional directionalType) {
            jsonObject.addProperty(TYPE_KEY, "directional");
            jsonObject.add(
                    NOTE_BLOCK_DATA_KEY,
                    context.serialize(
                            directionalType.getMap(),
                            BLOCK_FACE_NOTE_BLOCK_DATA_MAP_TYPE
                    )
            );
        } else if (src instanceof PlacingType.Orientable orientableType) {
            jsonObject.addProperty(TYPE_KEY, "orientable");
            jsonObject.add(
                    NOTE_BLOCK_DATA_KEY,
                    context.serialize(
                            orientableType.getMap(),
                            AXIS_NOTE_BLOCK_DATA_MAP_TYPE
                    )
            );
        }

        return jsonObject;
    }
}
