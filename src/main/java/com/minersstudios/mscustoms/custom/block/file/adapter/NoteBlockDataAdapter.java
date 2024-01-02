package com.minersstudios.mscustoms.custom.block.file.adapter;

import com.google.gson.*;
import com.minersstudios.mscustoms.custom.block.params.NoteBlockData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Instrument;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Gson adapter for serializing and deserializing NoteBlockData objects.
 * <br>
 * This adapter handles NoteBlockData serialization by converting it into a
 * JsonObject, and deserialization by reading the JsonObject and constructing
 * the corresponding NoteBlockData.
 * <br>
 * Serialized output you can see in the "MSCustoms/blocks/example.json" file.
 */
public class NoteBlockDataAdapter implements JsonSerializer<NoteBlockData>, JsonDeserializer<NoteBlockData> {
    private static final Map<String, Instrument> KEY_TO_INSTRUMENT_MAP;
    private static final String INSTRUMENT_KEY = "instrument";
    private static final String NOTE_KEY =       "note";
    private static final String POWERED_KEY =    "powered";

    static {
        final Instrument[] instruments = Instrument.values();
        KEY_TO_INSTRUMENT_MAP = new Object2ObjectOpenHashMap<>(instruments.length);

        for (final var instrument : instruments) {
            KEY_TO_INSTRUMENT_MAP.put(
                    instrument.name().toUpperCase(),
                    instrument
            );
        }
    }

    @Override
    public @NotNull NoteBlockData deserialize(
            final @NotNull JsonElement json,
            final @NotNull Type type,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException, IllegalArgumentException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final JsonElement instrumentElement = jsonObject.get(INSTRUMENT_KEY);

        if (instrumentElement == null) {
            throw new JsonParseException("Missing instrument");
        }

        final JsonElement noteElement = jsonObject.get(NOTE_KEY);

        if (noteElement == null) {
            throw new JsonParseException("Missing note");
        }

        final JsonElement poweredElement = jsonObject.get(POWERED_KEY);

        if (poweredElement == null) {
            throw new JsonParseException("Missing powered");
        }

        final String instrumentName = instrumentElement.getAsString().toUpperCase();
        final byte noteId = noteElement.getAsByte();
        final boolean powered = poweredElement.getAsBoolean();
        final Instrument instrument = KEY_TO_INSTRUMENT_MAP.get(instrumentName);

        if (instrument == null) {
            throw new JsonParseException("Invalid instrument: " + instrumentName);
        }

        return NoteBlockData.from(instrument, noteId, powered);
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull NoteBlockData data,
            final @NotNull Type type,
            final @NotNull JsonSerializationContext context
    ) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(INSTRUMENT_KEY, data.instrument().name());
        jsonObject.addProperty(NOTE_KEY, data.noteId());
        jsonObject.addProperty(POWERED_KEY, data.powered());

        return jsonObject;
    }
}
