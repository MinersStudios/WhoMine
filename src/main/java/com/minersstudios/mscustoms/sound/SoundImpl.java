package com.minersstudios.mscustoms.sound;

import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscustoms.MSCustoms;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
final class SoundImpl implements Sound {
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

    SoundImpl(final @NotNull Builder builder) {
        this.key = builder.key();
        this.category = builder.category();
        this.volume = builder.volume();
        this.pitch = builder.pitch();
    }

    @Override
    public @NotNull String getKey() {
        return this.key;
    }

    @Override
    public @NotNull SoundCategory getCategory() {
        return this.category;
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.category, this.volume, this.pitch);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof final SoundImpl that
                        && Float.compare(that.volume, this.volume) == 0
                        && Float.compare(that.pitch, this.pitch) == 0
                        && this.key.equals(that.key)
                        && this.category == that.category
                );
    }

    @Override
    public @NotNull String toString() {
        return "Sound{" +
                "key=" + this.key +
                ", soundCategory=" + this.category +
                ", volume=" + this.volume +
                ", pitch=" + this.pitch +
                '}';
    }

    @Contract(" -> new")
    @Override
    public @NotNull Builder toBuilder() {
        return Sound.builder()
                .key(this.key)
                .category(this.category)
                .volume(this.volume)
                .pitch(this.pitch);
    }

    @Override
    public void play(
            final @NotNull Player player,
            final @NotNull Entity entity
    ) {
        player.playSound(
                entity,
                this.getParsedKey(),
                this.category,
                this.volume,
                this.pitch
        );
    }

    @Override
    public void play(
            final @NotNull Player player,
            final @NotNull MSPosition position
    ) {
        this.play(player, position.toLocation());
    }

    @Override
    public void play(
            final @NotNull Player player,
            final @NotNull Location location
    ) {
        player.playSound(
                location,
                this.getParsedKey(),
                this.category,
                this.volume,
                this.pitch
        );
    }

    public void play(final @NotNull Entity entity) {
        entity.getWorld().playSound(
                entity,
                this.getParsedKey(),
                this.category,
                this.volume,
                this.pitch
        );
    }

    @Override
    public void play(final @NotNull MSPosition position) throws IllegalStateException {
        this.play(position.toLocation());
    }

    @Override
    public void play(final @NotNull Location location) throws IllegalStateException {
        final World world = location.getWorld();

        if (world == null) {
            throw new IllegalStateException("The world of the location cannot be null");
        }

        world.playSound(
                location,
                this.getParsedKey(),
                this.category,
                this.volume,
                this.pitch
        );
    }

    private @NotNull String getParsedKey() {
        return switch (this.key) {
            case WOOD_PLACE_SOUND_KEY -> MSCustoms.config().getWoodSoundPlace();
            case WOOD_BREAK_SOUND_KEY -> MSCustoms.config().getWoodSoundBreak();
            case WOOD_HIT_SOUND_KEY ->   MSCustoms.config().getWoodSoundHit();
            case WOOD_STEP_SOUND_KEY ->  MSCustoms.config().getWoodSoundStep();
            default -> this.key;
        };
    }
}
