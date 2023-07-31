package com.minersstudios.msblock.customblock.file;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public interface PlacingType {

    class Default implements PlacingType {
        private final NoteBlockData noteBlockData;

        public Default(@NotNull NoteBlockData noteBlockData) {
            this.noteBlockData = noteBlockData;
        }

        public @NotNull NoteBlockData getNoteBlockData() {
            return this.noteBlockData;
        }
    }

    class Directional implements PlacingType {
        private final Map<BlockFace, NoteBlockData> map;

        public Directional(@NotNull Map<BlockFace, NoteBlockData> map) {
            this.map = map;
        }

        public Directional(
                @NotNull NoteBlockData up,
                @NotNull NoteBlockData down,
                @NotNull NoteBlockData north,
                @NotNull NoteBlockData east,
                @NotNull NoteBlockData south,
                @NotNull NoteBlockData west
        ) {
            this(ImmutableMap.of(
                    BlockFace.UP, up,
                    BlockFace.DOWN, down,
                    BlockFace.NORTH, north,
                    BlockFace.EAST, east,
                    BlockFace.SOUTH, south,
                    BlockFace.WEST, west
            ));
        }

        public @NotNull NoteBlockData getNoteBlockData(@NotNull BlockFace face) {
            return this.map.get(face);
        }

        public @NotNull @Unmodifiable Map<BlockFace, NoteBlockData> getMap() {
            return this.map;
        }
    }

    class Orientable implements PlacingType {
        private final Map<Axis, NoteBlockData> map;

        public Orientable(@NotNull Map<Axis, NoteBlockData> map) {
            this.map = map;
        }

        public Orientable(
                @NotNull NoteBlockData x,
                @NotNull NoteBlockData y,
                @NotNull NoteBlockData z
        ) {
            this(ImmutableMap.of(
                    Axis.X, x,
                    Axis.Y, y,
                    Axis.Z, z
            ));
        }

        public @NotNull NoteBlockData getNoteBlockData(@NotNull Axis axis) {
            return this.map.get(axis);
        }

        public @NotNull @Unmodifiable Map<Axis, NoteBlockData> getMap() {
            return this.map;
        }
    }
}
