package com.minersstudios.msblock.customblock.file;

import com.minersstudios.msblock.Config;
import com.minersstudios.msblock.MSBlock;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a group of sounds associated with a custom block
 */
public class SoundGroup implements Cloneable {
    private Sound placeSound;
    private Sound breakSound;
    private Sound hitSound;
    private Sound stepSound;

    private static final SoundGroup WOOD = new SoundGroup(
            SoundGroup.Sound.create(
                    "block.wood.place",
                    SoundCategory.BLOCKS,
                    1.0f,
                    1.0f
            ),
            SoundGroup.Sound.create(
                    "block.wood.break",
                    SoundCategory.BLOCKS,
                    1.0f,
                    1.0f
            ),
            SoundGroup.Sound.create(
                    "block.wood.hit",
                    SoundCategory.BLOCKS,
                    0.5f,
                    0.5f
            ),
            SoundGroup.Sound.create(
                    "block.wood.step",
                    SoundCategory.PLAYERS,
                    0.3f,
                    0.9f
            )
    );

    private static final SoundGroup STONE = new SoundGroup(
            SoundGroup.Sound.create(
                    "block.stone.place",
                    SoundCategory.BLOCKS,
                    0.5f,
                    1.0f
            ),
            SoundGroup.Sound.create(
                    "block.stone.break",
                    SoundCategory.BLOCKS,
                    1.0f,
                    1.0f
            ),
            SoundGroup.Sound.create(
                    "block.stone.hit",
                    SoundCategory.BLOCKS,
                    0.5f,
                    0.5f
            ),
            SoundGroup.Sound.create(
                    "block.stone.step",
                    SoundCategory.PLAYERS,
                    0.3f,
                    0.9f
            )
    );

    /**
     * Constructs a SoundGroup with the specified sounds
     *
     * @param placeSound The place sound
     * @param breakSound The break sound
     * @param hitSound   The hit sound
     * @param stepSound  The step sound
     */
    public SoundGroup(
            @Nullable Sound placeSound,
            @Nullable Sound breakSound,
            @Nullable Sound hitSound,
            @Nullable Sound stepSound
    ) {
        this.placeSound = placeSound;
        this.breakSound = breakSound;
        this.hitSound = hitSound;
        this.stepSound = stepSound;
    }

    /**
     * @return The sound group for wooden blocks
     */
    public static @NotNull SoundGroup wood() {
        return WOOD;
    }

    /**
     * @return The sound group for stone blocks
     */
    public static @NotNull SoundGroup stone() {
        return STONE;
    }

    /**
     * @return The place sound of this SoundGroup,
     *         or null if the place sound is not set
     */
    public @Nullable Sound getPlaceSound() {
        return this.placeSound;
    }

    /**
     * @return The break sound of this SoundGroup,
     *         or null if the break sound is not set
     */
    public @Nullable Sound getBreakSound() {
        return this.breakSound;
    }

    /**
     * @return The hit sound of this SoundGroup,
     *         or null if the hit sound is not set
     */
    public @Nullable Sound getHitSound() {
        return this.hitSound;
    }

    /**
     * @return The step sound of this SoundGroup,
     *         or null if the step sound is not set
     */
    public @Nullable Sound getStepSound() {
        return this.stepSound;
    }

    /**
     * Plays the place sound of this SoundGroup at the specified location.
     * If the place sound is "block.wood.place", the wood place sound from
     * the {@link Config} will be played instead. If the place sound is null,
     * nothing will be played.
     *
     * @param location The location to play the place sound at
     */
    public void playPlaceSound(@NotNull Location location) {
        if (this.placeSound == null) return;
        location.getWorld().playSound(
                location,
                this.placeSound.key.equalsIgnoreCase("block.wood.place")
                ? MSBlock.getConfiguration().woodSoundPlace
                : this.placeSound.key,
                this.placeSound.category,
                this.placeSound.volume,
                this.placeSound.pitch
        );
    }

    /**
     * Plays the break sound of this SoundGroup at the specified location.
     * If the break sound is "block.wood.break", the wood break sound from
     * the {@link Config} will be played instead. If the break sound is null,
     * nothing will be played.
     *
     * @param location The location to play the break sound at
     */
    public void playBreakSound(@NotNull Location location) {
        if (this.breakSound == null) return;
        location.getWorld().playSound(
                location,
                this.breakSound.key.equalsIgnoreCase("block.wood.break")
                ? MSBlock.getConfiguration().woodSoundBreak
                : this.breakSound.key,
                this.breakSound.category,
                this.breakSound.volume,
                this.breakSound.pitch
        );
    }

    /**
     * Plays the hit sound of this SoundGroup at the specified location.
     * If the hit sound is "block.wood.hit", the wood hit sound from
     * the {@link Config} will be played instead. If the hit sound is null,
     * nothing will be played.
     *
     * @param location The location to play the hit sound at
     */
    public void playHitSound(@NotNull Location location) {
        if (this.hitSound == null) return;
        location.getWorld().playSound(
                location,
                this.hitSound.key.equalsIgnoreCase("block.wood.hit")
                ? MSBlock.getConfiguration().woodSoundHit
                : this.hitSound.key,
                this.hitSound.category,
                this.hitSound.volume,
                this.hitSound.pitch
        );
    }

    /**
     * Plays the step sound of this SoundGroup at the specified location.
     * If the step sound is "block.wood.step", the wood step sound from
     * the {@link Config} will be played instead. If the step sound is null,
     * nothing will be played.
     *
     * @param location The location to play the step sound at
     */
    public void playStepSound(@NotNull Location location) {
        if (this.stepSound == null) return;
        location.getWorld().playSound(
                location,
                this.stepSound.key.equalsIgnoreCase("block.wood.step")
                ? MSBlock.getConfiguration().woodSoundStep
                : this.stepSound.key,
                this.stepSound.category,
                this.stepSound.volume,
                this.stepSound.pitch
        );
    }

    /**
     * Creates a clone of this SoundGroup
     * with the same sounds
     *
     * @return A clone of this SoundGroup
     */
    @Override
    public @NotNull SoundGroup clone() {
        try {
            SoundGroup clone = (SoundGroup) super.clone();
            clone.placeSound = this.placeSound == null ? null : this.placeSound.clone();
            clone.breakSound = this.breakSound == null ? null : this.breakSound.clone();
            clone.hitSound = this.hitSound == null ? null : this.hitSound.clone();
            clone.stepSound = this.stepSound == null ? null : this.stepSound.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone SoundGroup", e);
        }
    }

    @Override
    public @NotNull String toString() {
        return "SoundGroup{" +
                "placeSound=" + this.placeSound +
                ", breakSound=" + this.breakSound +
                ", hitSound=" + this.hitSound +
                ", stepSound=" + this.stepSound +
                '}';
    }

    /**
     * Represents a sound with :
     * <ul>
     *     <li>A key {@link String}</li>
     *     <li>A {@link SoundCategory}</li>
     *     <li>A volume float</li>
     *     <li>A pitch float</li>
     * </ul>
     */
    public static class Sound implements Cloneable {
        private String key;
        private SoundCategory category;
        private float volume;
        private float pitch;

        private Sound(
                @NotNull String key,
                @NotNull SoundCategory category,
                float volume,
                float pitch
        ) {
            this.key = key;
            this.category = category;
            this.volume = volume;
            this.pitch = pitch;
        }

        /**
         * Creates a new Sound instance
         *
         * @param key           The key of the sound
         * @param soundCategory The sound category of the sound
         * @param volume        The volume of the sound
         * @param pitch         The pitch of the sound
         * @return The created Sound instance,
         *         or null if the key is blank
         */
        public static @Nullable Sound create(
                @Nullable String key,
                @NotNull SoundCategory soundCategory,
                float volume,
                float pitch
        ) {
            return StringUtils.isBlank(key)
                    ? null
                    : new Sound(key, soundCategory, volume, pitch);
        }

        /**
         * @return The key of this Sound
         */
        public @NotNull String key() {
            return this.key;
        }

        /**
         * Sets the key of this Sound
         *
         * @param key The key to set
         * @return This Sound instance with the new key
         */
        public @NotNull Sound key(@NotNull String key) {
            this.key = key;
            return this;
        }

        /**
         * @return The sound category of this Sound
         */
        public @NotNull SoundCategory category() {
            return this.category;
        }

        /**
         * Sets the sound category of this Sound
         *
         * @param soundCategory The sound category to set
         * @return This Sound instance with the new sound category
         */
        public @NotNull Sound category(@NotNull SoundCategory soundCategory) {
            this.category = soundCategory;
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
         * @return This Sound instance with the new volume
         */
        public @NotNull Sound volume(float volume) {
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
         * @return This Sound instance with the new pitch
         */
        public @NotNull Sound pitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        /**
         * Creates a clone of this Sound with the same values
         *
         * @return A clone of this Sound
         */
        @Override
        public @NotNull Sound clone() {
            try {
                return (Sound) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Failed to clone Sound", e);
            }
        }

        /**
         * @return A string representation of this Sound
         */
        @Override
        public @NotNull String toString() {
            return "Sound{" +
                    "key='" + this.key + '\'' +
                    ", soundCategory=" + this.category +
                    ", volume=" + this.volume +
                    ", pitch=" + this.pitch +
                    '}';
        }
    }
}
