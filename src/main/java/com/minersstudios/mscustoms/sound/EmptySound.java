package com.minersstudios.mscustoms.sound;

import com.minersstudios.mscore.location.MSPosition;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;

@Immutable
final class EmptySound implements Sound {
    static final Sound EMPTY = new EmptySound();

    @Override
    public @NotNull String getKey() {
        return "";
    }

    @Override
    public @NotNull SoundCategory getCategory() {
        return SoundCategory.MASTER;
    }

    @Override
    public float getVolume() {
        return 0.0f;
    }

    @Override
    public float getPitch() {
        return 0.0f;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Contract("null -> false")
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof final EmptySound that
                        && that.isEmpty()
                );
    }

    @Override
    public @NotNull String toString() {
        return "EmptySound{}";
    }

    @Contract(" -> new")
    @Override
    public @NotNull Builder toBuilder() {
        return Sound.builder();
    }

    @Override
    public void play(
            final @NotNull Player player,
            final @NotNull Entity entity
    ) {
        // Do nothing
    }

    @Override
    public void play(
            final @NotNull Player player,
            final @NotNull MSPosition position
    ) {
        // Do nothing
    }

    @Override
    public void play(
            final @NotNull Player player,
            final @NotNull Location location
    ) {
        // Do nothing
    }

    @Override
    public void play(final @NotNull Entity entity) {
        // Do nothing
    }

    @Override
    public void play(final @NotNull MSPosition position) {
        // Do nothing
    }

    @Override
    public void play(final @NotNull Location location) {
        // Do nothing
    }
}
