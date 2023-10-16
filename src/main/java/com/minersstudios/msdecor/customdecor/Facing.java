package com.minersstudios.msdecor.customdecor;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum Facing {
    //<editor-fold desc="Facings" defaultstate="collapsed">
    ALL(
            EnumSet.of(
                    BlockFace.UP,
                    BlockFace.DOWN,
                    BlockFace.WEST,
                    BlockFace.EAST,
                    BlockFace.NORTH,
                    BlockFace.SOUTH
            )
    ),
    FLOOR(
            EnumSet.of(
                    BlockFace.UP
            )
    ),
    CEILING(
            EnumSet.of(
                    BlockFace.DOWN
            )
    ),
    WALL(
            EnumSet.of(
                    BlockFace.WEST,
                    BlockFace.EAST,
                    BlockFace.NORTH,
                    BlockFace.SOUTH
            )
    );
    //</editor-fold>

    private final EnumSet<BlockFace> blockFaceSet;

    Facing(final @NotNull EnumSet<BlockFace> blockFaceSet) {
        this.blockFaceSet = blockFaceSet;
    }

    /**
     * @param blockFace Block face to be converted
     * @return The converted {@link Facing} or null
     */
    public static @Nullable Facing fromBlockFace(final @NotNull BlockFace blockFace) {
        return switch (blockFace) {
            case DOWN -> CEILING;
            case UP -> FLOOR;
            case WEST, EAST, NORTH, SOUTH -> WALL;
            default -> null;
        };
    }

    /**
     * @return The block faces of a given facing
     */
    public @NotNull @Unmodifiable Set<BlockFace> blockFaceSet() {
        return Collections.unmodifiableSet(this.blockFaceSet);
    }

    /**
     * @param blockFace Block face to be checked
     * @return True if a given block face can be used
     *         for placing the custom decor
     */
    @Contract("null -> false")
    public boolean hasFace(final @Nullable BlockFace blockFace) {
        return blockFace != null
                && this.blockFaceSet.contains(blockFace);
    }

    /**
     * @param blockFace Block face to be checked
     * @return True if a given block face is valid
     */
    @Contract("null -> false")
    public static boolean isValid(final @Nullable BlockFace blockFace) {
        return blockFace == BlockFace.DOWN
                || blockFace == BlockFace.UP
                || blockFace == BlockFace.WEST
                || blockFace == BlockFace.EAST
                || blockFace == BlockFace.NORTH
                || blockFace == BlockFace.SOUTH;
    }
}
