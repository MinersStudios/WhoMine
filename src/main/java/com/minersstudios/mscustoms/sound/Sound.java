package com.minersstudios.mscustoms.sound;

import com.minersstudios.mscore.location.MSPosition;
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
public interface Sound {

    /**
     * Returns the key of this Sound
     *
     * @return The key of this Sound
     */
    @NotNull String getKey();

    /**
     * Returns the sound category of this sound
     *
     * @return The sound category of this sound
     */
    @NotNull SoundCategory getCategory();

    /**
     * Returns the volume of this sound
     *
     * @return The volume of this sound
     */
    float getVolume();

    /**
     * Returns the pitch of this sound
     *
     * @return The pitch of this sound
     */
    float getPitch();

    /**
     * Returns the hash code value for this sound.
     * <br>
     * If this sound is empty, the hash code will be 0.
     *
     * @return The hash code value for this sound
     */
    @Override
    int hashCode();

    /**
     * Returns whether this Sound is empty
     *
     * @return True if this Sound is empty
     */
    boolean isEmpty();

    /**
     * Returns whether this Sound is the same as the specified object
     *
     * @param obj The reference object with which to compare
     * @return True if this Sound is the same as the obj argument
     */
    @Contract("null -> false")
    @Override
    boolean equals(final @Nullable Object obj);

    /**
     * Returns a string representation of this Sound
     *
     * @return A string representation of this Sound
     */
    @Override
    @NotNull String toString();

    /**
     * Creates a new {@link Builder} with the values of this Sound
     *
     * @return A new builder with the values of this Sound
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
     * Plays this sound at the specified entity in the world.
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
     * @throws IllegalStateException If the specified key is blank or null
     * @see #create(String, SoundCategory)
     * @see #create(String, SoundCategory, float, float)
     */
    @Contract("_ -> new")
    static @NotNull Sound create(final @NotNull String key) throws IllegalStateException {
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
     * @throws IllegalStateException If the specified key is blank or null
     * @see #create(String, SoundCategory, float, float)
     */
    @Contract("_, _ -> new")
    static @NotNull Sound create(
            final @NotNull String key,
            final @NotNull SoundCategory soundCategory
    ) throws IllegalStateException {
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
     * Creates a new {@link Sound} instance
     *
     * @param key           The key of the sound
     * @param soundCategory The sound category of the sound
     * @param volume        The volume of the sound
     * @param pitch         The pitch of the sound
     * @return A new Sound instance
     * @throws IllegalStateException If the specified key is blank or null
     */
    @Contract("_, _, _, _ -> new")
    static @NotNull Sound create(
            final @NotNull String key,
            final @NotNull SoundCategory soundCategory,
            final float volume,
            final float pitch
    ) throws IllegalStateException {
        return builder()
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
        return new SoundImpl.BuilderImpl();
    }

    /**
     * Returns an empty sound constant
     *
     * @return An empty sound constant
     */
    static @NotNull Sound empty() {
        return EmptySound.SINGLETON;
    }

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
    interface Builder {

        /**
         * Returns the key of this sound
         *
         * @return The key of this sound, or null if not set
         */
        @UnknownNullability String key();

        /**
         * Sets the key of this sound
         *
         * @param soundEvent The sound event, which will be converted to a key
         * @return This builder, for chaining
         * @see #key(String)
         */
        @Contract("_ -> this")
        @NotNull Builder key(final @NotNull SoundEvent soundEvent);

        /**
         * Sets the key of this sound
         *
         * @param key The key to set
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder key(final @NotNull String key);

        /**
         * Returns the sound category of this sound
         *
         * @return The sound category of this sound, or null if not set
         */
        @UnknownNullability SoundCategory category();

        /**
         * Sets the sound category of this sound
         *
         * @param category The sound category to set
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder category(final @NotNull SoundCategory category);

        /**
         * Returns the volume of this sound
         *
         * @return The volume of this sound
         */
        float volume();

        /**
         * Sets the volume of this sound
         *
         * @param volume The volume to set
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder volume(final float volume);

        /**
         * Returns the pitch of this sound
         *
         * @return The pitch of this sound
         */
        float pitch();

        /**
         * Sets the pitch of this sound
         *
         * @param pitch The pitch to set
         * @return This builder, for chaining
         */
        @Contract("_ -> this")
        @NotNull Builder pitch(final float pitch);

        /**
         * Builds a {@code Sound} with the values of this builder
         *
         * @return The built {@code Sound}
         * @throws IllegalStateException If the key is blank or null
         */
        @Contract(" -> new")
        @NotNull Sound build() throws IllegalStateException;

        /**
         * Returns a string representation of this builder
         *
         * @return A string representation of this builder
         */
        @Override
        @NotNull String toString();
    }
}
