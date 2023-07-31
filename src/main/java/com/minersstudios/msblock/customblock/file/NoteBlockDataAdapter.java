package com.minersstudios.msblock.customblock.file;

import com.google.gson.*;
import org.bukkit.Instrument;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class NoteBlockDataAdapter implements JsonSerializer<NoteBlockData>, JsonDeserializer<NoteBlockData> {
    private static final String INSTRUMENT_KEY = "instrument";
    private static final String NOTE_KEY = "note";
    private static final String POWERED_KEY = "powered";

    @Override
    public @NotNull NoteBlockData deserialize(
            @NotNull JsonElement json,
            @NotNull Type typeOfT,
            @NotNull JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String instrumentName = jsonObject.get(INSTRUMENT_KEY).getAsString();
        Instrument instrument;
        byte noteId = jsonObject.get(NOTE_KEY).getAsByte();
        boolean powered = jsonObject.get(POWERED_KEY).getAsBoolean();

        try {
            instrument = Instrument.valueOf(instrumentName);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Unknown Instrument: " + instrumentName);
        }

        return NoteBlockData.fromParams(instrument, noteId, powered);
    }

    @Override
    public @NotNull JsonElement serialize(
            @NotNull NoteBlockData src,
            @NotNull Type typeOfSrc,
            @NotNull JsonSerializationContext context
    ) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("instrument", src.instrument().toString());
        jsonObject.addProperty("note", src.noteId());
        jsonObject.addProperty("powered", src.powered());

        return jsonObject;
    }
}
