package com.minersstudios.mscustoms.custom.block.params.settings;

import com.minersstudios.mscustoms.custom.block.params.ToolType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Class representing the tool rules for a custom block. This class holds
 * information about the tool type and whether the tool is forced. If the
 * tool is forced, the player must use the specified tool type to break the
 * block to get the drops.
 */
@Immutable
public final class Tool {
    private final ToolType type;
    private final boolean force;

    private static final Tool DEFAULT = new Tool(ToolType.HAND, false);

    private Tool(
            final @NotNull ToolType type,
            final boolean force
    ) {
        this.type = type;
        this.force = force;
    }

    /**
     * Factory method for creating a new Tool object with the specified tool
     * type and whether the tool is forced
     *
     * @param type  The tool type for the custom block
     * @param force Whether the tool is forced
     * @return A new Tool object with the specified tool type
     */
    @Contract("_, _ -> new")
    public static @NotNull Tool create(
            final @NotNull ToolType type,
            final boolean force
    ) {
        return new Tool(type, force);
    }

    /**
     * @return The default Tool object
     */
    public static @NotNull Tool defaultTool() {
        return DEFAULT;
    }

    /**
     * @return The tool type for the custom block
     */
    public @NotNull ToolType getToolType() {
        return this.type;
    }

    /**
     * @return Whether the tool is forced for the custom block
     */
    public boolean isForce() {
        return this.force;
    }

    /**
     * @return A string representation of the Tool rules
     */
    @Override
    public @NotNull String toString() {
        return "Tool{" +
                "type=" + this.type +
                ", force=" + this.force +
                '}';
    }
}
