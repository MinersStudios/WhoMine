package com.minersstudios.mscustoms.sound;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

/**
 * Gson adapter for serializing and deserializing Sound objects.
 * <br>
 * Serialized output example :
 * <pre>
 * "sound": {
 *     "key": "block.wood.break",
 *     "category": "BLOCKS",
 *     "volume": 1.0,
 *     "pitch": 1.0
 * }
 * </pre>
 *
 * Or for empty sound :
 * <pre>
 * "sound": "empty"
 * </pre>
 *
 * If sound is empty, then serialized output will be primitive string
 * {@link #EMPTY_VALUE}.
 * <br>
 * If json is primitive string {@link #EMPTY_VALUE}, then deserialized
 * sound will be empty.
 */
public final class SoundAdapter implements JsonSerializer<Sound>, JsonDeserializer<Sound> {
    private static final Map<String, SoundCategory> KEY_TO_CATEGORY_MAP;

    //<editor-fold desc="Properties" defaultstate="collapsed">
    public static final String KEY_KEY =      "key";
    public static final String CATEGORY_KEY = "category";
    public static final String VOLUME_KEY =   "volume";
    public static final String PITCH_KEY =    "pitch";
    public static final String EMPTY_VALUE =  "empty";
    //</editor-fold>

    static {
        final SoundCategory[] categories = SoundCategory.values();
        KEY_TO_CATEGORY_MAP = new Object2ObjectOpenHashMap<>(categories.length);

        for (final var category : categories) {
            KEY_TO_CATEGORY_MAP.put(
                    category.name().toUpperCase(),
                    category
            );
        }
    }

    @Override
    public @NotNull Sound deserialize(
            final @NotNull JsonElement json,
            final @NotNull Type type,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException, IllegalStateException {
        if (json.isJsonPrimitive()) {
            final String value = json.getAsString();

            if (EMPTY_VALUE.equals(value)) {
                return Sound.empty();
            }

            throw new JsonParseException("Unknown primitive string : " + value);
        }

        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement keyElement = jsonObject.get("key");

        if (keyElement == null) {
            throw new JsonParseException("Missing key");
        }

        final JsonElement categoryElement = jsonObject.get("category");

        if (categoryElement == null) {
            throw new JsonParseException("Missing category");
        }

        final JsonElement volumeElement = jsonObject.get("volume");

        if (volumeElement == null) {
            throw new JsonParseException("Missing volume");
        }

        final JsonElement pitchElement = jsonObject.get("pitch");

        if (pitchElement == null) {
            throw new JsonParseException("Missing pitch");
        }

        final SoundCategory category = KEY_TO_CATEGORY_MAP.get(categoryElement.getAsString().toUpperCase(Locale.ENGLISH));

        if (category == null) {
            throw new JsonParseException("Unknown SoundCategory : " + categoryElement.getAsString());
        }

        return Sound.create(
                keyElement.getAsString(),
                category,
                volumeElement.getAsFloat(),
                pitchElement.getAsFloat()
        );
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull Sound sound,
            final @Nullable Type type,
            final @Nullable JsonSerializationContext context
    ) {
        if (sound.isEmpty()) {
            return new JsonPrimitive(EMPTY_VALUE);
        }

        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(KEY_KEY, sound.getKey());
        jsonObject.addProperty(CATEGORY_KEY, sound.getCategory().name());
        jsonObject.addProperty(VOLUME_KEY, sound.getVolume());
        jsonObject.addProperty(PITCH_KEY, sound.getPitch());

        return jsonObject;
    }
}
