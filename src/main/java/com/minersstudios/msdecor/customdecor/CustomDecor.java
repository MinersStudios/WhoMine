package com.minersstudios.msdecor.customdecor;

import com.minersstudios.mscore.util.LocationUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.events.CustomDecorBreakEvent;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public final class CustomDecor {
    private final CustomDecorData<?> data;
    private final ItemDisplay display;
    private final List<Interaction> interactions;
    private final net.minecraft.world.level.levelgen.structure.BoundingBox nmsBoundingBox;

    public CustomDecor(
            final @NotNull CustomDecorData<?> data,
            final @NotNull ItemDisplay display,
            final @NotNull List<Interaction> interactions,
            final @NotNull net.minecraft.world.level.levelgen.structure.BoundingBox nmsBoundingBox
    ) {
        this.data = data;
        this.display = display;
        this.interactions = Collections.unmodifiableList(interactions);
        this.nmsBoundingBox = nmsBoundingBox;
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
                : DecorHitBox.isHitBoxParent(interaction)
                ? Optional.ofNullable(fromParent(interaction))
                : DecorHitBox.isHitBoxChild(interaction)
                ? Optional.ofNullable(fromChild(interaction))
                : Optional.empty();
    }

    public @NotNull CustomDecorData<?> getData() {
        return this.data;
    }

    public @NotNull ItemDisplay getDisplay() {
        return this.display;
    }

    public @NotNull @Unmodifiable List<Interaction> getInteractions() {
        return this.interactions;
    }

    public @NotNull net.minecraft.world.level.levelgen.structure.BoundingBox getNMSBoundingBox() {
        return this.nmsBoundingBox;
    }

    public void destroy(
            final @NotNull Player player,
            final boolean dropItem
    ) {
        final CustomDecorBreakEvent event = new CustomDecorBreakEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        final CraftWorld world = (CraftWorld) player.getWorld();

        if (dropItem) {
            final ItemStack displayItemStack = this.display.getItemStack();
            assert displayItemStack != null;
            final ItemStack itemStack =
                    this.data.isDropsType()
                    ? displayItemStack
                    : this.data.getItem();
            final ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.displayName(displayItemStack.getItemMeta().displayName());
            itemStack.setItemMeta(itemMeta);

            world.dropItemNaturally(
                    LocationUtils.nmsToBukkit(this.nmsBoundingBox.getCenter()).subtract(0.25d, 0.25d, 0.25d),
                    itemStack
            );
        }

        if (!this.data.getHitBox().getType().isNone()) {
            CustomDecorDataImpl.fillBlocks(
                    player.getName(),
                    world.getHandle(),
                    LocationUtils.getBlockPosesBetween(
                            this.nmsBoundingBox.minX(),
                            this.nmsBoundingBox.minY(),
                            this.nmsBoundingBox.minZ(),
                            this.nmsBoundingBox.maxX(),
                            this.nmsBoundingBox.maxY(),
                            this.nmsBoundingBox.maxZ()
                    ),
                    Blocks.AIR
            );
        }

        for (final var interaction : this.interactions) {
            interaction.remove();
        }

        this.display.remove();
        this.data.getSoundGroup().playBreakSound(LocationUtils.nmsToBukkit(this.nmsBoundingBox.getCenter(), player.getWorld()));
    }

    private static @Nullable CustomDecor fromParent(final @NotNull Interaction interaction) {
        final PersistentDataContainer container = interaction.getPersistentDataContainer();

        if (container.isEmpty()) return null;

        CustomDecorData<?> data = null;
        ItemDisplay display = null;
        final var interactions = new ArrayList<Interaction>();
        net.minecraft.world.level.levelgen.structure.BoundingBox boundingBox = null;

        interactions.add(interaction);

        for (final var key : container.getKeys()) {
            final String value = container.get(key, PersistentDataType.STRING);

            if (StringUtils.isBlank(value)) continue;

            switch (key.getKey()) {
                case CustomDecorType.TYPE_TAG_NAME -> data = CustomDecorData.fromKey(value).orElse(null);
                case DecorHitBox.HITBOX_DISPLAY -> {
                    try {
                        if (interaction.getWorld().getEntity(UUID.fromString(value)) instanceof final ItemDisplay itemDisplay) {
                            display = itemDisplay;
                        }
                    } catch (final IllegalArgumentException ignored) {
                        return null;
                    }
                }
                case DecorHitBox.HITBOX_INTERACTIONS -> {
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
                case DecorHitBox.HITBOX_BOUNDING_BOX -> {
                    final String[] coordinates = value.split(",");

                    if (coordinates.length != 6) return null;

                    try {
                        boundingBox = new net.minecraft.world.level.levelgen.structure.BoundingBox(
                                Integer.parseInt(coordinates[0]),
                                Integer.parseInt(coordinates[1]),
                                Integer.parseInt(coordinates[2]),
                                Integer.parseInt(coordinates[3]),
                                Integer.parseInt(coordinates[4]),
                                Integer.parseInt(coordinates[5])
                        );
                    } catch (final NumberFormatException ignored) {
                        return null;
                    }
                }
            }
        }

        return data == null
                || display == null
                || boundingBox == null
                ? null
                : new CustomDecor(
                        data,
                        display,
                        interactions,
                        boundingBox
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
