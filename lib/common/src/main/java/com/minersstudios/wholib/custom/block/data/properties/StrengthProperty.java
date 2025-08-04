package com.minersstudios.wholib.custom.block.data.properties;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.minersstudios.wholib.key.Key;
import com.minersstudios.wholib.key.ResourceKey;
import com.minersstudios.wholib.property.Property;
import com.minersstudios.wholib.property.PropertyAdapter;
import com.minersstudios.wholib.property.type.AbstractGroupProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.io.IOException;

@Immutable
public class StrengthProperty extends AbstractGroupProperty<StrengthProperty> implements Property<StrengthProperty> {
    private static final ResourceKey RESOURCE_KEY = ResourceKey.empty("strength");
    private static final @Key String HARDNESS_KEY = "hardness";
    private static final @Key String RESISTANCE_KEY = "resistance";
    private static final PropertyAdapter<StrengthProperty> ADAPTER = new Adapter();

    private final float hardness;
    private final float resistance;

    private StrengthProperty(
            final float hardness,
            final float resistance
    ) {
        this.hardness = hardness;
        this.resistance = resistance;
    }

    @Override
    public @NotNull ResourceKey getResourceKey() {
        return RESOURCE_KEY;
    }

    @Override
    public @NotNull PropertyAdapter<StrengthProperty> getAdapter() {
        return ADAPTER;
    }

    public float getHardness() {
        return this.hardness;
    }

    public float getResistance() {
        return this.resistance;
    }

    public static @NotNull ResourceKey resourceKey() {
        return RESOURCE_KEY;
    }

    public static @Key @NotNull String hardnessKey() {
        return HARDNESS_KEY;
    }

    public static @Key @NotNull String resistanceKey() {
        return RESISTANCE_KEY;
    }

    public static @NotNull PropertyAdapter<StrengthProperty> adapter() {
        return ADAPTER;
    }

    @Contract(" -> new")
    public static @NotNull StrengthProperty instabreak() {
        return of(0.0f);
    }

    @Contract("_ -> new")
    public static @NotNull StrengthProperty of(final float strength) {
        return of(strength, strength);
    }

    @Contract("_, _ -> new")
    public static @NotNull StrengthProperty of(
            final float hardness,
            final float resistance
    ) {
        return new StrengthProperty(
                hardness,
                Math.max(0.0f, resistance)
        );
    }

    public static class Adapter extends PropertyAdapter<StrengthProperty> {

        @Override
        public void write(
                final @NotNull JsonWriter out,
                final @NotNull StrengthProperty value
        ) throws IOException {
            out.beginObject();
            out.name(HARDNESS_KEY).value(value.getHardness());
            out.name(RESISTANCE_KEY).value(value.getResistance());
            out.endObject();
        }

        @Override
        public @NotNull StrengthProperty read(final @NotNull JsonReader in) throws IOException {
            float hardness = 0.0f;
            float resistance = 0.0f;

            in.beginObject();

            while (in.hasNext()) {
                final String name = in.nextName();

                switch (name) {
                    case HARDNESS_KEY   -> hardness = (float) in.nextDouble();
                    case RESISTANCE_KEY -> resistance = (float) in.nextDouble();
                    default             -> in.skipValue();
                }
            }

            in.endObject();

            return StrengthProperty.of(hardness, resistance);
        }
    }
}
