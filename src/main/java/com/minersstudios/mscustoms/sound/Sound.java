package com.minersstudios.mscustoms.sound;

import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.MSCustoms;
import net.minecraft.sounds.SoundEvent;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Represents a sound with :
 * <ul>
 *     <li>A key {@link String}</li>
 *     <li>A {@link SoundCategory}</li>
 *     <li>A volume float</li>
 *     <li>A pitch float</li>
 * </ul>
 *
 * Sound can be created with builder, for example :
 * <pre>
 *     Sound sound = Sound.builder()
 *             .key("block.wood.place")
 *             .category(SoundCategory.BLOCKS)
 *             .volume(1.0f)
 *             .pitch(1.0f)
 *             .build();
 * </pre>
 * Or with static methods :
 * <ul>
 *     <li>{@link #create(SoundEvent)}</li>
 *     <li>{@link #create(String)}</li>
 *     <li>{@link #create(SoundEvent, SoundCategory)}</li>
 *     <li>{@link #create(String, SoundCategory)}</li>
 *     <li>{@link #create(SoundEvent, SoundCategory, float, float)}</li>
 *     <li>{@link #create(String, SoundCategory, float, float)}</li>
 * </ul>
 */
@Immutable
public final class Sound {
    private final String key;
    private final SoundCategory category;
    private final float volume;
    private final float pitch;

    //<editor-fold desc="Wood Sound Keys" defaultstate="collapsed">
    private static final String WOOD_PLACE_SOUND_KEY = "block.wood.place";
    private static final String WOOD_BREAK_SOUND_KEY = "block.wood.break";
    private static final String WOOD_HIT_SOUND_KEY =   "block.wood.hit";
    private static final String WOOD_STEP_SOUND_KEY =  "block.wood.step";
    //</editor-fold>

    /**
     * Sound constructor
     *
     * @param builder The builder to create this Sound
     */
    private Sound(final @NotNull Builder builder) {
        this.key = builder.key;
        this.category = builder.category;
        this.volume = builder.volume;
        this.pitch = builder.pitch;
    }

    /**
     * Creates a new Sound instance with 1.0f volume and pitch and
     * {@link SoundCategory#MASTER} category
     *
     * @param soundEvent The sound event, which will be converted to a key
     * @return A new Sound instance
     * @see #create(String)
     */
    @Contract("_ -> new")
    public static @NotNull Sound create(final @NotNull SoundEvent soundEvent) {
        return create(soundEvent.getLocation().getPath());
    }

    /**
     * Creates a new Sound instance with 1.0f volume and pitch and
     * {@link SoundCategory#MASTER} category
     *
     * @param key The key of the sound
     * @return A new Sound instance
     * @throws IllegalArgumentException If the key is blank
     * @see #create(String, SoundCategory)
     * @see #create(String, SoundCategory, float, float)
     */
    @Contract("_ -> new")
    public static @NotNull Sound create(final @NotNull String key) throws IllegalArgumentException {
        return create(
                key,
                SoundCategory.MASTER
        );
    }

    /**
     * Creates a new Sound instance with 1.0f volume and pitch
     *
     * @param soundEvent The sound event, which will be converted to a key
     * @param soundCategory The sound category of the sound
     * @return A new Sound instance
     * @see #create(String, SoundCategory)
     */
    @Contract("_, _ -> new")
    public static @NotNull Sound create(
            final @NotNull SoundEvent soundEvent,
            final @NotNull SoundCategory soundCategory
    ) {
        return create(
                soundEvent.getLocation().getPath(),
                soundCategory
        );
    }

    /**
     * Creates a new Sound instance with 1.0f volume and pitch
     *
     * @param key           The key of the sound
     * @param soundCategory The sound category of the sound
     * @return A new Sound instance
     * @throws IllegalArgumentException If the key is blank
     * @see #create(String, SoundCategory, float, float)
     */
    @Contract("_, _ -> new")
    public static @NotNull Sound create(
            final @NotNull String key,
            final @NotNull SoundCategory soundCategory
    ) throws IllegalArgumentException {
        return create(
                key,
                soundCategory,
                1.0f, 1.0f
        );
    }

    /**
     * Creates a new Sound instance
     *
     * @param soundEvent    The sound event, which will be converted to a key
     * @param soundCategory The sound category of the sound
     * @param volume        The volume of the sound
     * @param pitch         The pitch of the sound
     * @return A new Sound instance
     * @see #create(String, SoundCategory, float, float)
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull Sound create(
            final @NotNull SoundEvent soundEvent,
            final @NotNull SoundCategory soundCategory,
            final float volume,
            final float pitch
    ) {
        return create(
                soundEvent.getLocation().getPath(),
                soundCategory,
                volume,
                pitch
        );
    }

    /**
     * Creates a new Sound instance
     *
     * @param key           The key of the sound
     * @param soundCategory The sound category of the sound
     * @param volume        The volume of the sound
     * @param pitch         The pitch of the sound
     * @return A new Sound instance
     * @throws IllegalArgumentException If the key is blank
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull Sound create(
            final @NotNull String key,
            final @NotNull SoundCategory soundCategory,
            final float volume,
            final float pitch
    ) throws IllegalArgumentException {
        return new Sound.Builder()
                .key(key)
                .category(soundCategory)
                .volume(volume)
                .pitch(pitch)
                .build();
    }

    /**
     * Creates a new builder for {@link Sound}
     *
     * @return A new builder
     */
    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * @return The key of this Sound
     */
    public @NotNull String getKey() {
        return this.key;
    }

    /**
     * @return The sound category of this Sound
     */
    public @NotNull SoundCategory getCategory() {
        return this.category;
    }

    /**
     * @return The volume of this Sound
     */
    public float getVolume() {
        return this.volume;
    }

    /**
     * @return The pitch of this Sound
     */
    public float getPitch() {
        return this.pitch;
    }

    /**
     * @return A hash code value for this Sound
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.category, this.volume, this.pitch);
    }

    /**
     * @param obj The reference object with which to compare
     * @return True if this Sound is the same as the obj argument
     */
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof final Sound that
                        && Float.compare(that.volume, this.volume) == 0
                        && Float.compare(that.pitch, this.pitch) == 0
                        && this.key.equals(that.key)
                        && this.category == that.category
                );
    }

    /**
     * @return A string representation of this Sound
     */
    @Override
    public @NotNull String toString() {
        return "Sound{" +
                "key=" + this.key +
                ", soundCategory=" + this.category +
                ", volume=" + this.volume +
                ", pitch=" + this.pitch +
                '}';
    }

    /**
     * @return A builder with the values of this Sound
     */
    public @NotNull Builder toBuilder() {
        final Builder builder = new Builder();

        builder.key = this.key;
        builder.category = this.category;
        builder.volume = this.volume;
        builder.pitch = this.pitch;

        return builder;
    }

    /**
     * Plays this Sound at the specified position
     *
     * @param position The position to play this Sound at
     * @throws IllegalStateException If the world of the position is null
     */
    public void play(final @NotNull MSPosition position) {
        this.play(position.toLocation());
    }

    /**
     * Plays this Sound at the specified location
     *
     * @param location The location to play this Sound at
     * @throws IllegalStateException If the world of the location is null
     */
    public void play(final @NotNull Location location) {
        final World world = location.getWorld();

        if (world == null) {
            throw new IllegalStateException("The world of the location cannot be null");
        }

        world.playSound(
                location,
                switch (this.key) {
                    case WOOD_PLACE_SOUND_KEY -> MSCustoms.config().getWoodSoundPlace();
                    case WOOD_BREAK_SOUND_KEY -> MSCustoms.config().getWoodSoundBreak();
                    case WOOD_HIT_SOUND_KEY ->   MSCustoms.config().getWoodSoundHit();
                    case WOOD_STEP_SOUND_KEY ->  MSCustoms.config().getWoodSoundStep();
                    default -> this.key;
                },
                this.category,
                this.volume,
                this.pitch
        );
    }

    /**
     * A builder for {@link Sound}
     */
    public static class Builder {
        private String key;
        private SoundCategory category;
        private float volume;
        private float pitch;

        private Builder() {
            this.volume = 1.0f;
            this.pitch = 1.0f;
        }

        /**
         * @return The key of this Sound
         */
        public @UnknownNullability String key() {
            return this.key;
        }

        /**
         * Sets the key of this Sound
         *
         * @param soundEvent The sound event, which will be converted to a key
         * @return This builder, for chaining
         * @see #key(String)
         */
        public @NotNull Builder key(final @NotNull SoundEvent soundEvent) {
            return this.key(soundEvent.getLocation().getPath());
        }

        /**
         * Sets the key of this Sound
         *
         * @param key The key to set
         * @return This builder, for chaining
         */
        public @NotNull Builder key(final @NotNull String key) {
            this.key = key;

            return this;
        }

        /**
         * @return The sound category of this Sound
         */
        public @UnknownNullability SoundCategory category() {
            return this.category;
        }

        /**
         * Sets the sound category of this Sound
         *
         * @param category The sound category to set
         * @return This builder, for chaining
         */
        public @NotNull Builder category(final @NotNull SoundCategory category) {
            this.category = category;

            return this;
        }

        /**
         * @return The volume of this Sound
         */
        public float volume() {
            return this.volume;
        }

        /**
         * Sets the volume of this Sound
         *
         * @param volume The volume to set
         * @return This builder, for chaining
         */
        public @NotNull Builder volume(final float volume) {
            this.volume = volume;

            return this;
        }

        /**
         * @return The pitch of this Sound
         */
        public float pitch() {
            return this.pitch;
        }

        /**
         * Sets the pitch of this Sound
         *
         * @param pitch The pitch to set
         * @return This builder, for chaining
         */
        public @NotNull Builder pitch(final float pitch) {
            this.pitch = pitch;

            return this;
        }

        /**
         * Builds a Sound with the values of this builder
         *
         * @return The built Sound
         * @throws IllegalStateException If the key is blank or null
         */
        public @NotNull Sound build() throws IllegalStateException {
            if (ChatUtils.isBlank(this.key)) {
                throw new IllegalArgumentException("Key cannot be blank or null!");
            }

            return new Sound(this);
        }
    }
}
