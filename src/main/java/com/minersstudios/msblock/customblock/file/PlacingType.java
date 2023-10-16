package com.minersstudios.msblock.customblock.file;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/**
 * Abstract class representing different types of placing
 * behavior for custom note block data. Each implementing
 * class corresponds to a specific type of placing behavior.
 */
public abstract class PlacingType {

    /**
     * Creates a Default placing type with the specified
     * note block data
     *
     * @param noteBlockData The note block data associated
     *                      with this placing type
     * @return The Default PlacingType object
     */
    @Contract("_ -> new")
    public static @NotNull Default defaultType(final @NotNull NoteBlockData noteBlockData) {
        return new Default(noteBlockData);
    }

    /**
     * Creates a Directional placing type with the specified
     * map of note block data for each block face
     *
     * @param map The map of BlockFace to NoteBlockData
     *            representing different directions
     * @return The Directional PlacingType object
     * @throws IllegalArgumentException If the map does not contain
     *                                  exactly 6 entries or contains
     *                                  unsupported BlockFaces
     * @see Directional#isSupported(BlockFace)
     */
    @Contract("_ -> new")
    public static @NotNull Directional directionalType(final @NotNull Map<BlockFace, NoteBlockData> map) throws IllegalArgumentException {
        if (map.size() != 6) {
            throw new IllegalArgumentException("Map must contain 6 entries");
        }

        for (final var blockFace : map.keySet()) {
            if (!Directional.isSupported(blockFace)) {
                throw new IllegalArgumentException("Unsupported BlockFace: " + blockFace);
            }
        }

        return new Directional(map);
    }

    /**
     * Creates a Directional placing type with note block data
     * for each supported block face direction
     *
     * @param up    The note block data for {@link BlockFace#UP}
     * @param down  The note block data for {@link BlockFace#DOWN}
     * @param north The note block data for {@link BlockFace#NORTH}
     * @param east  The note block data for {@link BlockFace#EAST}
     * @param south The note block data for {@link BlockFace#SOUTH}
     * @param west  The note block data for {@link BlockFace#WEST}
     * @return The Directional PlacingType object
     */
    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull Directional directionalType(
            final @NotNull NoteBlockData up,
            final @NotNull NoteBlockData down,
            final @NotNull NoteBlockData north,
            final @NotNull NoteBlockData east,
            final @NotNull NoteBlockData south,
            final @NotNull NoteBlockData west
    ) {
        return new Directional(ImmutableMap.of(
                BlockFace.UP, up,
                BlockFace.DOWN, down,
                BlockFace.NORTH, north,
                BlockFace.EAST, east,
                BlockFace.SOUTH, south,
                BlockFace.WEST, west
        ));
    }

    /**
     * Creates an Orientable placing type with the specified map
     * of note block data for each axis orientation
     *
     * @param map The map of Axis to NoteBlockData representing
     *            different orientations
     * @return The Orientable PlacingType object
     * @throws IllegalArgumentException If the map does not contain
     *                                  exactly 3 entries
     */
    @Contract("_ -> new")
    public static @NotNull Orientable orientableType(final @NotNull Map<Axis, NoteBlockData> map) throws IllegalArgumentException {
        if (map.size() != 3) {
            throw new IllegalArgumentException("Map must contain 3 entries");
        }

        return new Orientable(map);
    }

    /**
     * Creates an Orientable placing type with note block data
     * for each supported axis orientation (X, Y, Z)
     *
     * @param x The note block data for {@link Axis#X}
     * @param y The note block data for {@link Axis#Y}
     * @param z The note block data for {@link Axis#Z}
     * @return The Orientable PlacingType object
     */
    @Contract("_, _, _ -> new")
    public static @NotNull Orientable orientableType(
            final @NotNull NoteBlockData x,
            final @NotNull NoteBlockData y,
            final @NotNull NoteBlockData z
    ) {
        return new Orientable(ImmutableMap.of(
                Axis.X, x,
                Axis.Y, y,
                Axis.Z, z
        ));
    }

    /**
     * Default placing type for a note block data. Includes only
     * one note block data.
     */
    public static class Default extends PlacingType {
        private final NoteBlockData noteBlockData;

        private Default(final @NotNull NoteBlockData noteBlockData) {
            this.noteBlockData = noteBlockData;
        }

        /**
         * @return The note block data associated with this
         *         placing type
         */
        public @NotNull NoteBlockData getNoteBlockData() {
            return this.noteBlockData;
        }
    }

    /**
     * Directional placing type for a note block data. Includes
     * a map of note block data for each block face. Each block
     * face is mapped to a note block data. The note block data
     * for the block face is used when the note block is placed
     * on that block face.
     */
    public static class Directional extends PlacingType {
        private final Map<BlockFace, NoteBlockData> map;

        private Directional(final @NotNull Map<BlockFace, NoteBlockData> map) {
            this.map = map;
        }

        public @NotNull NoteBlockData getNoteBlockData(final @NotNull BlockFace face) {
            if (!isSupported(face)) {
                throw new IllegalArgumentException("Unsupported BlockFace: " + face);
            }

            return this.map.get(face);
        }

        /**
         * @return An unmodifiable map of BlockFace to NoteBlockData
         *         representing different directions
         */
        public @NotNull @Unmodifiable Map<BlockFace, NoteBlockData> getMap() {
            return this.map;
        }

        /**
         * @param face The block face to check
         * @return True if the specified block face is supported
         *         by this placing type
         */
        public static boolean isSupported(final @NotNull BlockFace face) {
            return face == BlockFace.UP
                    || face == BlockFace.DOWN
                    || face == BlockFace.NORTH
                    || face == BlockFace.EAST
                    || face == BlockFace.SOUTH
                    || face == BlockFace.WEST;
        }
    }

    /**
     * Orientable placing type for a note block data. Includes a
     * map of note block data for each axis orientation (X, Y, Z).
     * Each axis orientation is mapped to a note block data. The
     * note block data for the axis orientation is used when the
     * note block is placed on that axis orientation.
     */
    public static class Orientable extends PlacingType {
        private final Map<Axis, NoteBlockData> map;

        private Orientable(final @NotNull Map<Axis, NoteBlockData> map) {
            this.map = map;
        }

        /**
         * Gets the note block data associated with the specified
         * axis orientation (X, Y, Z)
         *
         * @param axis The axis orientation
         * @return The note block data for the given orientation
         */
        public @NotNull NoteBlockData getNoteBlockData(final @NotNull Axis axis) {
            return this.map.get(axis);
        }

        /**
         * @return An unmodifiable map of Axis to NoteBlockData
         *         representing different orientations
         */
        public @NotNull @Unmodifiable Map<Axis, NoteBlockData> getMap() {
            return this.map;
        }
    }
}
