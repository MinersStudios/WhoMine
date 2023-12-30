package com.minersstudios.msdecor.api;

import com.minersstudios.mscore.location.MSBoundingBox;
import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.MSDecorUtils;
import com.minersstudios.msdecor.event.CustomDecorBreakEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Light;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents CustomDecor, handling custom decor in the server world. This class
 * facilitates the creation, manipulation, and destruction of custom decorations.
 */
public final class CustomDecor {
    private final CustomDecorData<?> data;
    private final ItemDisplay display;
    private final Interaction[] interactions;
    private final MSBoundingBox msbb;

    /**
     * Constructs a CustomDecor instance based on provided parameters
     *
     * @param data         The CustomDecorData associated with this decor
     * @param display      The ItemDisplay representing the model display of
     *                     this decor
     * @param interactions Array of Interactions representing the hitbox of
     *                     this decor
     * @param msbb         The MSBoundingBox defining the hitbox of the decor
     */
    public CustomDecor(
            final @NotNull CustomDecorData<?> data,
            final @NotNull ItemDisplay display,
            final Interaction @NotNull [] interactions,
            final @NotNull MSBoundingBox msbb
    ) {
        this.data = data;
        this.display = display;
        this.interactions = interactions;
        this.msbb = msbb;
    }

    /**
     * Retrieves a CustomDecor instance based on a given block
     *
     * @param block The block from which to retrieve the CustomDecor
     * @return An Optional containing the CustomDecor instance if found,
     *         otherwise an empty Optional
     */
    public static @NotNull Optional<CustomDecor> fromBlock(final @Nullable Block block) {
        return block == null
                ? Optional.empty()
                : fromInteraction(
                        MSDecorUtils.getNearbyInteraction(
                                MSPosition.of(block.getLocation().toCenterLocation())
                        )
                );
    }

    /**
     * Retrieves a CustomDecor instance based on a given interaction
     *
     * @param interaction The interaction from which to retrieve the CustomDecor
     * @return An Optional containing the CustomDecor instance if found,
     *         otherwise an empty Optional
     */
    public static @NotNull Optional<CustomDecor> fromInteraction(final @Nullable Interaction interaction) {
        if (interaction == null) {
            return Optional.empty();
        }

        final PersistentDataContainer container = interaction.getPersistentDataContainer();

        return container.isEmpty()
                ? Optional.empty()
                : DecorHitBox.isParent(container)
                ? Optional.ofNullable(fromParent(interaction))
                : DecorHitBox.isChild(container)
                ? Optional.ofNullable(fromChild(interaction))
                : Optional.empty();
    }

    /**
     * @return The CustomDecorData associated with this decor
     */
    public @NotNull CustomDecorData<?> getData() {
        return this.data;
    }

    /**
     * @return The ItemDisplay representing the model display of this decor
     */
    public @NotNull ItemDisplay getDisplay() {
        return this.display;
    }

    /**
     * @return Array of Interactions representing the hitbox of this decor
     */
    public Interaction @NotNull [] getInteractions() {
        return this.interactions.clone();
    }

    /**
     * @return The MSBoundingBox defining the hitbox of the decor
     */
    public @NotNull MSBoundingBox getBoundingBox() {
        return this.msbb;
    }

    /**
     * Destroys this custom decor in the server world
     *
     * @param destroyer The entity who broke the custom decor
     * @param dropItem  Whether to drop the item
     */
    public void destroy(
            final @NotNull Entity destroyer,
            final boolean dropItem
    ) {
        final CustomDecorBreakEvent event = new CustomDecorBreakEvent(this, destroyer);
        destroyer.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        final CraftWorld world = (CraftWorld) destroyer.getWorld();
        final MSPosition center = this.msbb.getCenter(world);

        if (dropItem) {
            ItemStack displayItem = this.display.getItemStack();

            if (displayItem == null) {
                displayItem = this.data.getItem();

                MSLogger.warning("Trying to drop a null item from a custom decor at " + this.display.getLocation());
            }

            world.dropItemNaturally(
                    center.toLocation(),
                    !this.data.isAnyTyped()
                    || this.data.isDropType()
                            ? displayItem.clone()
                            : CustomDecorDataImpl.copyMetaForTypeItem(
                                    this.data.getItem(),
                                    displayItem
                            )
            );
        }

        if (!this.data.getHitBox().getType().isNone()) {
            CustomDecorDataImpl.fillBlocks(
                    destroyer.getName(),
                    world.getHandle(),
                    this.msbb.getBlockPositions(),
                    Blocks.AIR.defaultBlockState(),
                    blockPos -> {
                        final Block block = world.getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                        if (
                                block.getBlockData() instanceof final Light light
                                && light.isWaterlogged()
                        ) {
                            block.setType(Material.WATER);
                            return false;
                        }

                        return true;
                    }
            );
        }

        for (final var interaction : this.interactions) {
            interaction.remove();
        }

        this.display.remove();
        this.data.getSoundGroup().playBreakSound(center);
        this.getData().doBreakAction(event);
    }

    /**
     * Places a CustomDecorType in the server world
     *
     * @param type      The CustomDecorType to place
     * @param position  The location of the block to place the decor at
     * @param player    The player who placed the decor
     * @param blockFace The block face to place the decor on
     * @throws IllegalArgumentException If the world is not specified in the
     *                                  position
     */
    public static void place(
            final @NotNull CustomDecorType type,
            final @NotNull MSPosition position,
            final @NotNull Player player,
            final @NotNull BlockFace blockFace
    ) throws IllegalArgumentException {
        place(type, position, player, blockFace, null, null);
    }

    /**
     * Places a CustomDecorType in the server world
     *
     * @param type       The CustomDecorType to place
     * @param position   The location of the block to place the decor at
     * @param player     The player who placed the decor
     * @param blockFace  The block face to place the decor on
     * @param hand       The hand, which was used to place the decor, can be null
     * @throws IllegalArgumentException If the world is not specified in the
     *                                  position
     */
    public static void place(
            final @NotNull CustomDecorType type,
            final @NotNull MSPosition position,
            final @NotNull Player player,
            final @NotNull BlockFace blockFace,
            final @Nullable EquipmentSlot hand
    ) throws IllegalArgumentException {
        place(type, position, player, blockFace, hand, null);
    }

    /**
     * Places a CustomDecorType in the server world
     *
     * @param type       The CustomDecorType to place
     * @param position   The location of the block to place the decor at
     * @param player     The player who placed the decor
     * @param blockFace  The block face to place the decor on
     * @param hand       The hand, which was used to place the decor, can be null
     * @param customName The custom name of the decor, can be null
     * @throws IllegalArgumentException If the world is not specified in the
     *                                  position
     */
    public static void place(
            final @NotNull CustomDecorType type,
            final @NotNull MSPosition position,
            final @NotNull Player player,
            final @NotNull BlockFace blockFace,
            final @Nullable EquipmentSlot hand,
            final @Nullable Component customName
    ) throws IllegalArgumentException {
        type.getCustomDecorData().place(
                position,
                player,
                blockFace,
                hand,
                customName
        );
    }

    /**
     * Destroys the decor in the block, which was destroyed by the player
     *
     * @param player The player, who destroyed the decor
     * @param block  The block, which was destroyed
     */
    public static void destroyInBlock(
            final @NotNull Player player,
            final @NotNull Block block
    ) {
        destroyInBlock(player, block, player.getGameMode() == GameMode.SURVIVAL);
    }

    /**
     * Destroys the decor in the block, which was destroyed by the entity
     *
     * @param destroyer The entity, who destroyed the decor
     * @param block     The block, which was destroyed
     * @param dropItem  Whether to drop the item
     */
    public static void destroyInBlock(
            final @NotNull Entity destroyer,
            final @NotNull Block block,
            final boolean dropItem
    ) {
        for (
                final var interaction :
                MSDecorUtils.getNearbyInteractions(
                        MSPosition.of(block.getLocation().toCenterLocation())
                )
        ) {
            destroy(destroyer, interaction, dropItem);
        }
    }

    /**
     * Destroys the decor, which was destroyed by the player
     *
     * @param player     The player who destroyed the decor
     * @param interacted The interacted entity
     */
    public static void destroy(
            final @NotNull Player player,
            final @NotNull Interaction interacted
    ) {
        destroy(player, interacted, player.getGameMode() == GameMode.SURVIVAL);
    }

    /**
     * Destroys the decor, which was destroyed by the entity
     *
     * @param destroyer  The entity who destroyed the decor
     * @param interacted The interacted entity
     * @param dropItem   Whether to drop the item
     */
    public static void destroy(
            final @NotNull Entity destroyer,
            final @NotNull Interaction interacted,
            final boolean dropItem
    ) {
        fromInteraction(interacted)
        .ifPresent(customDecor -> customDecor.destroy(destroyer, dropItem));
    }

    private static @Nullable CustomDecor fromParent(final @NotNull Interaction interaction) {
        final PersistentDataContainer container = interaction.getPersistentDataContainer();

        if (container.isEmpty()) {
            return null;
        }

        CustomDecorData<?> data = null;
        ItemDisplay display = null;
        final var interactions = new ObjectArrayList<Interaction>();
        MSBoundingBox msbb = null;

        interactions.add(interaction);

        for (final var key : container.getKeys()) {
            final String value = container.get(key, PersistentDataType.STRING);

            if (ChatUtils.isBlank(value)) {
                continue;
            }

            switch (key.getKey()) {
                case CustomDecorType.TYPE_TAG_NAME -> data = CustomDecorData.fromKey(value).orElse(null);
                case DecorHitBox.HITBOX_DISPLAY_KEY -> {
                    try {
                        if (interaction.getWorld().getEntity(UUID.fromString(value)) instanceof final ItemDisplay itemDisplay) {
                            display = itemDisplay;
                        }
                    } catch (final IllegalArgumentException ignored) {
                        return null;
                    }
                }
                case DecorHitBox.HITBOX_INTERACTIONS_KEY -> {
                    for (final var uuid : value.split(",")) {
                        try {
                            if (interaction.getWorld().getEntity(UUID.fromString(uuid)) instanceof final Interaction child) {
                                interactions.add(child);
                            }
                        } catch (final IllegalArgumentException ignored) {
                            return null;
                        }
                    }
                }
                case DecorHitBox.HITBOX_BOUNDING_BOX_KEY -> {
                    final String[] coordinates = value.split(",");

                    if (coordinates.length != 6) {
                        return null;
                    }

                    try {
                        msbb = MSBoundingBox.of(
                                Double.parseDouble(coordinates[0]),
                                Double.parseDouble(coordinates[1]),
                                Double.parseDouble(coordinates[2]),
                                Double.parseDouble(coordinates[3]),
                                Double.parseDouble(coordinates[4]),
                                Double.parseDouble(coordinates[5])
                        );
                    } catch (final NumberFormatException ignored) {
                        return null;
                    }
                }
            }
        }

        return data == null
                || display == null
                || msbb == null
                ? null
                : new CustomDecor(
                        data,
                        display,
                        interactions.toArray(new Interaction[0]),
                        msbb
                );
    }

    private static @Nullable CustomDecor fromChild(final @NotNull Interaction interaction) {
        final String uuid =
                interaction.getPersistentDataContainer()
                .get(
                        DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY,
                        PersistentDataType.STRING
                );

        try {
            return ChatUtils.isBlank(uuid)
                    || !(interaction.getWorld().getEntity(UUID.fromString(uuid)) instanceof final Interaction parent)
                    ? null
                    : fromParent(parent);
        } catch (final IllegalArgumentException ignored) {
            return null;
        }
    }
}
