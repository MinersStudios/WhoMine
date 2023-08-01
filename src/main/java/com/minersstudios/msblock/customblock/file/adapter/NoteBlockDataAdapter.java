package com.minersstudios.msblock.customblock.file.adapter;

import com.google.gson.*;
import com.minersstudios.msblock.customblock.file.NoteBlockData;
import org.bukkit.Instrument;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Gson adapter for serializing and deserializing NoteBlockData objects.
 * This adapter handles NoteBlockData serialization by converting it into
 * a JsonObject, and deserialization by reading the JsonObject and
 * constructing the corresponding NoteBlockData.
 * <p>
 * Serialized output you can see in the "MSBlock/blocks/example.json" file.
 */
public class NoteBlockDataAdapter implements JsonSerializer<NoteBlockData>, JsonDeserializer<NoteBlockData> {
    private static final String INSTRUMENT_KEY = "instrument";
    private static final String NOTE_KEY = "note";
    private static final String POWERED_KEY = "powered";

    @Override
    public @NotNull NoteBlockData deserialize(
            @NotNull JsonElement json,
            @NotNull Type typeOfT,
            @NotNull JsonDeserializationContext context
    ) throws JsonParseException, IllegalArgumentException {
        JsonObject jsonObject = json.getAsJsonObject();

        Instrument instrument = Instrument.valueOf(jsonObject.get(INSTRUMENT_KEY).getAsString());
        byte noteId = jsonObject.get(NOTE_KEY).getAsByte();
        boolean powered = jsonObject.get(POWERED_KEY).getAsBoolean();

        return NoteBlockData.fromParams(instrument, noteId, powered);
    }

    @Override
    public @NotNull JsonElement serialize(
            @NotNull NoteBlockData src,
            @NotNull Type typeOfSrc,
            @NotNull JsonSerializationContext context
    ) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(INSTRUMENT_KEY, src.instrument().name());
        jsonObject.addProperty(NOTE_KEY, src.noteId());
        jsonObject.addProperty(POWERED_KEY, src.powered());

        return jsonObject;
    }
}
