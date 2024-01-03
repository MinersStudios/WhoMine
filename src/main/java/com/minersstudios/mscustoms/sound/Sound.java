package com.minersstudios.mscustoms.sound;

import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.Config;
import net.minecraft.sounds.SoundEvent;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.concurrent.Immutable;

/**
 * Sound interface.
 * <br>
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
 * Sound sound = Sound.builder()
 *         .key("block.wood.place")
 *         .category(SoundCategory.BLOCKS)
 *         .volume(1.0f)
 *         .pitch(1.0f)
 *         .build();
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
 *
 * @see Builder
 */
@Immutable
public interface Sound {
    String KEY_KEY =      "key";
    String CATEGORY_KEY = "category";
    String VOLUME_KEY =   "volume";
    String PITCH_KEY =    "pitch";
    String EMPTY_VALUE =  "empty";

    /**
     * Creates a new Sound instance with 1.0f volume and pitch and
     * {@link SoundCategory#MASTER} category
     *
     * @param soundEvent The sound event, which will be converted to a key
     * @return A new Sound instance
     * @see #create(String)
     */
    @Contract("_ -> new")
    static @NotNull Sound create(final @NotNull SoundEvent soundEvent) {
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
    static @NotNull Sound create(final @NotNull String key) throws IllegalArgumentException {
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
    static @NotNull Sound create(
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
    static @NotNull Sound create(
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
    static @NotNull Sound create(
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
    static @NotNull Sound create(
            final @NotNull String key,
            final @NotNull SoundCategory soundCategory,
            final float volume,
            final float pitch
    ) throws IllegalArgumentException {
        return new Builder()
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
    @Contract(" -> new")
    static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * @return An empty Sound
     */
    static @NotNull Sound empty() {
        return EmptySound.EMPTY;
    }

    /**
     * @return The key of this Sound
     */
    @NotNull String getKey();

    /**
     * @return The sound category of this Sound
     */
    @NotNull SoundCategory getCategory();

    /**
     * @return The volume of this Sound
     */
    float getVolume();

    /**
     * @return The pitch of this Sound
     */
    float getPitch();

    /**
     * @return The hash code value for this sound. If this sound is empty, the
     *         hash code will be 0.
     */
    @Override
    int hashCode();

    /**
     * @return True if this Sound is empty
     */
    boolean isEmpty();

    /**
     * @param obj The reference object with which to compare
     * @return True if this Sound is the same as the obj argument
     */
    @Contract("null -> false")
    @Override
    boolean equals(final @Nullable Object obj);

    /**
     * @return A string representation of this Sound
     */
    @Override
    @NotNull String toString();

    /**
     * @return A builder with the values of this Sound
     */
    @Contract(" -> new")
    @NotNull Builder toBuilder();

    /**
     * Plays this Sound to the specified player.
     * <br>
     * If the place sound is {@link SoundGroup#WOOD}, the wood place sound from
     * the {@link Config} will be played instead.
     *
     * @param player The player to play this sound to
     * @param entity The entity source of this sound
     */
    void play(
            final @NotNull Player player,
            final @NotNull Entity entity
    );

    /**
     * Plays this Sound to the specified player.
     * <br>
     * If the place sound is {@link SoundGroup#WOOD}, the wood place sound from
     * the {@link Config} will be played instead.
     *
     * @param player   The player to play this sound to
     * @param position The position to play this sound at
     */
    void play(
            final @NotNull Player player,
            final @NotNull MSPosition position
    );

    /**
     * Plays this Sound to the specified player.
     * <br>
     * If the place sound is {@link SoundGroup#WOOD}, the wood place sound from
     * the {@link Config} will be played instead.
     *
     * @param player   The player to play this sound to
     * @param location The location to play this sound at
     */
    void play(
            final @NotNull Player player,
            final @NotNull Location location
    );

    /**
     * Plays this Sound at the specified entity in the world.
     * <br>
     * If the place sound is {@link SoundGroup#WOOD}, the wood place sound from
     * the {@link Config} will be played instead.
     *
     * @param entity The entity source of this sound
     */
    void play(final @NotNull Entity entity);

    /**
     * Plays this Sound at the specified position in the world.
     * <br>
     * If the place sound is {@link SoundGroup#WOOD}, the wood place sound from
     * the {@link Config} will be played instead.
     *
     * @param position The position to play this sound at
     * @throws IllegalStateException If the world of the position is null
     */
    void play(final @NotNull MSPosition position) throws IllegalStateException;

    /**
     * Plays this Sound at the specified location in the world.
     * <br>
     * If the place sound is {@link SoundGroup#WOOD}, the wood place sound from
     * the {@link Config} will be played instead.
     *
     * @param location The location to play this sound at
     * @throws IllegalStateException If the world of the location is null
     */
    void play(final @NotNull Location location) throws IllegalStateException;

    /**
     * A builder for {@link Sound}.
     * <br>
     * This builder can be used with the following chain methods :
     * <ul>
     *     <li>{@link #key(SoundEvent)}</li>
     *     <li>{@link #key(String)}</li>
     *     <li>{@link #category(SoundCategory)}</li>
     *     <li>{@link #volume(float)}</li>
     *     <li>{@link #pitch(float)}</li>
     * </ul>
     *
     * Default values :
     * <ul>
     *     <li>{@code key} : {@code null}</li>
     *     <li>{@code category} : {@link SoundCategory#MASTER}</li>
     *     <li>{@code volume} : {@code 0.0f}</li>
     *     <li>{@code pitch} : {@code 0.0f}</li>
     * </ul>
     */
    class Builder {
        private String key;
        private SoundCategory category;
        private float volume;
        private float pitch;

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
         * @throws IllegalArgumentException If the key is blank or null
         */
        public @NotNull Sound build() throws IllegalArgumentException {
            if (ChatUtils.isBlank(this.key)) {
                throw new IllegalArgumentException("Key cannot be blank or null!");
            }

            if (this.category == null) {
                this.category = SoundCategory.MASTER;
            }

            return new SoundImpl(this);
        }
    }
}
