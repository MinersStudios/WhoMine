package com.minersstudios.msdecor.api;

import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.utility.BlockUtils;
import com.minersstudios.mscore.utility.LocationUtils;
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
    CEILING(
            EnumSet.of(
                    BlockFace.DOWN
            )
    ),
    FLOOR(
            EnumSet.of(
                    BlockFace.UP
            )
    ),
    WALL(
            EnumSet.of(
                    BlockFace.NORTH,
                    BlockFace.NORTH_EAST,
                    BlockFace.NORTH_WEST,
                    BlockFace.EAST,
                    BlockFace.SOUTH,
                    BlockFace.SOUTH_WEST,
                    BlockFace.SOUTH_EAST,
                    BlockFace.WEST
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
    public static @Nullable Facing fromBlockFace(final @Nullable BlockFace blockFace) {
        return blockFace == null
                ? null
                : switch (blockFace) {
                    case DOWN -> CEILING;
                    case UP -> FLOOR;
                    case NORTH,
                            NORTH_EAST,
                            NORTH_WEST,
                            EAST,
                            SOUTH,
                            SOUTH_WEST,
                            SOUTH_EAST,
                            WEST -> WALL;
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

    @Contract("null, _ -> false")
    public boolean hasFace(
            final @Nullable MSPosition positionAtFace,
            final float yaw
    ) {
        if (positionAtFace == null) {
            return false;
        }

        if (positionAtFace.world() == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        final MSPosition newPosition = switch (this) {
            case FLOOR -> positionAtFace.clone().offset(0, -1, 0);
            case CEILING -> positionAtFace.clone().offset(0, 1, 0);
            case WALL -> positionAtFace.clone().offset(
                    LocationUtils.degreesToBlockFace90(yaw).getDirection()
            );
        };

        return !BlockUtils.isReplaceable(newPosition.getBlock().getType());
    }

    /**
     * @param blockFace Block face to be checked
     * @return True if a given block face is valid
     */
    @Contract("null -> false")
    public static boolean isValid(final @Nullable BlockFace blockFace) {
        return blockFace != null
                && switch (blockFace) {
                    case DOWN,
                            UP,
                            NORTH,
                            NORTH_EAST,
                            NORTH_WEST,
                            EAST,
                            SOUTH,
                            SOUTH_WEST,
                            SOUTH_EAST,
                            WEST -> true;
                    default -> false;
                };
    }
}
