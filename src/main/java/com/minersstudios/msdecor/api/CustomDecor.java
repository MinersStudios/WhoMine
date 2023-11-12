package com.minersstudios.msdecor.api;

import com.minersstudios.mscore.location.MSBoundingBox;
import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.event.CustomDecorBreakEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Light;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public final class CustomDecor {
    private final CustomDecorData<?> data;
    private final ItemDisplay display;
    private final Interaction[] interactions;
    private final MSBoundingBox msbb;

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

    public static @NotNull Optional<CustomDecor> fromBlock(final @Nullable org.bukkit.block.Block block) {
        return block == null
                ? Optional.empty()
                : fromInteraction(MSDecorUtils.getNearbyInteraction(block.getLocation().toCenterLocation()));
    }

    public static @NotNull Optional<CustomDecor> fromInteraction(final @Nullable Interaction interaction) {
        if (interaction == null) return Optional.empty();

        final PersistentDataContainer container = interaction.getPersistentDataContainer();
        return container.isEmpty()
                ? Optional.empty()
                : DecorHitBox.isParent(interaction)
                ? Optional.ofNullable(fromParent(interaction))
                : DecorHitBox.isChild(interaction)
                ? Optional.ofNullable(fromChild(interaction))
                : Optional.empty();
    }

    public @NotNull CustomDecorData<?> getData() {
        return this.data;
    }

    public @NotNull ItemDisplay getDisplay() {
        return this.display;
    }

    public Interaction @NotNull [] getInteractions() {
        return this.interactions.clone();
    }

    public @NotNull MSBoundingBox getBoundingBox() {
        return this.msbb;
    }

    public void destroy(
            final @NotNull Entity destroyer,
            final boolean dropItem
    ) {
        final CustomDecorBreakEvent event = new CustomDecorBreakEvent(this, destroyer);
        destroyer.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        final CraftWorld world = (CraftWorld) destroyer.getWorld();
        final MSPosition center = this.msbb.getCenter(world);

        if (dropItem) {
            ItemStack displayItem = this.display.getItemStack();

            if (displayItem == null) {
                displayItem = this.data.getItem();

                MSDecor.logger().warning("Trying to drop a null item from a custom decor at " + this.display.getLocation());
            }

            final ItemStack itemStack =
                    !this.data.isAnyTyped()
                    || this.data.isDropsType()
                            ? displayItem.clone()
                            : this.data.getItem();
            final ItemMeta itemMeta = itemStack.getItemMeta();
            final ItemMeta displayMeta = displayItem.getItemMeta();

            if (
                    displayMeta instanceof final LeatherArmorMeta displayColorable
                    && itemMeta instanceof final LeatherArmorMeta itemColorable
            ) {
                itemColorable.setColor(displayColorable.getColor());
            }

            itemMeta.displayName(displayMeta.displayName());
            itemStack.setItemMeta(itemMeta);

            world.dropItemNaturally(
                    center.toLocation(),
                    itemStack
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
    }

    public static void place(
            final @NotNull CustomDecorType type,
            final @NotNull MSPosition blockLocation,
            final @NotNull Player player,
            final @NotNull BlockFace blockFace
    ) {
        place(type, blockLocation, player, blockFace, null, null);
    }

    public static void place(
            final @NotNull CustomDecorType type,
            final @NotNull MSPosition blockLocation,
            final @NotNull Player player,
            final @NotNull BlockFace blockFace,
            final @Nullable EquipmentSlot hand
    ) {
        place(type, blockLocation, player, blockFace, hand, null);
    }

    public static void place(
            final @NotNull CustomDecorType type,
            final @NotNull MSPosition blockLocation,
            final @NotNull Player player,
            final @NotNull BlockFace blockFace,
            final @Nullable EquipmentSlot hand,
            final @Nullable Component customName
    ) {
        type.getCustomDecorData().place(
                blockLocation,
                player,
                blockFace,
                hand,
                customName
        );
    }

    public static void destroyInBlock(
            final @NotNull Player player,
            final @NotNull Block block
    ) {
        destroyInBlock(player, block, player.getGameMode() == GameMode.SURVIVAL);
    }

    public static void destroyInBlock(
            final @NotNull Entity destroyer,
            final @NotNull Block block,
            final boolean dropItem
    ) {
        for (final var interaction : MSDecorUtils.getNearbyInteractions(block.getLocation().toCenterLocation())) {
            destroy(destroyer, interaction, dropItem);
        }
    }

    public static void destroy(
            final @NotNull Player player,
            final @NotNull Interaction interacted
    ) {
        destroy(player, interacted, player.getGameMode() == GameMode.SURVIVAL);
    }

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

        if (container.isEmpty()) return null;

        CustomDecorData<?> data = null;
        ItemDisplay display = null;
        final var interactions = new ArrayList<Interaction>();
        MSBoundingBox msbb = null;

        interactions.add(interaction);

        for (final var key : container.getKeys()) {
            final String value = container.get(key, PersistentDataType.STRING);

            if (StringUtils.isBlank(value)) continue;

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

                    if (coordinates.length != 6) return null;

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
            return StringUtils.isBlank(uuid)
                    || !(interaction.getWorld().getEntity(UUID.fromString(uuid)) instanceof final Interaction parent)
                    ? null
                    : fromParent(parent);
        } catch (final IllegalArgumentException ignored) {
            return null;
        }
    }
}
