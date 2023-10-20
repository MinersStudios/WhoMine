package com.minersstudios.msdecor.customdecor;

import com.minersstudios.mscore.util.LocationUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.events.CustomDecorBreakEvent;
import com.minersstudios.msdecor.events.CustomDecorRightClickEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Predicate;

public interface CustomDecorData<D extends CustomDecorData<D>> extends Keyed {

    /**
     * @return The unique namespaced key identifying
     *         the custom decor
     */
    @Override
    @NotNull NamespacedKey getKey();

    /**
     * @return The hit box of custom decor
     */
    @NotNull DecorHitBox getHitBox();

    /**
     * @return The allowed place faces of custom decor
     */
    @NotNull Facing getFacing();

    /**
     * @return The clone of the {@link ItemStack}
     *         representing the custom decor
     */
    @NotNull ItemStack getItem();

    /**
     * @return The {@link Material} of custom decor
     */
    @NotNull SoundGroup getSoundGroup();

    /**
     * @return An unmodifiable list of {@link Recipe}
     *         entries representing the associated recipes
     */
    @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> recipes();

    @NotNull @Unmodifiable Set<DecorParameter> parameterSet();

    Type<D> @NotNull [] wrenchTypes() throws UnsupportedOperationException;

    @Contract("null -> null")
    @Nullable Type<D> getNextType(final @Nullable Type<? extends CustomDecorData<?>> type) throws UnsupportedOperationException;

    @NotNull @Unmodifiable Map<Facing, Type<D>> typeFaceMap() throws UnsupportedOperationException;

    @Contract("null -> null")
    @Nullable Type<D> getTypeByFace(final @Nullable BlockFace blockFace) throws UnsupportedOperationException;

    @NotNull @Unmodifiable Map<Integer, Type<D>> typeLightLevelMap() throws UnsupportedOperationException;

    @Contract("null -> null")
    @Nullable Type<D> getTypeByLightLevel(final @Nullable Integer lightLevel) throws UnsupportedOperationException;

    int @NotNull [] lightLevels() throws UnsupportedOperationException;

    int nextLightLevel(final int lightLevel) throws UnsupportedOperationException;

    double getSitHeight() throws UnsupportedOperationException;

    boolean hasParameters(
            final @NotNull DecorParameter first,
            final @NotNull DecorParameter... rest
    );

    /**
     * Check whether a given item stack is similar to
     * this custom decor
     *
     * @param itemStack The {@link ItemStack} to compare
     *                  with this custom decor
     * @return True if the item stack is similar to this
     *         custom decor
     */
    @Contract("null -> false")
    boolean isSimilar(final @Nullable ItemStack itemStack);

    /**
     * Check whether a given custom decor data is similar to
     * this custom decor data
     *
     * @param data The {@link CustomDecorData} to compare with
     *             this custom decor
     * @return True if the custom decor data is similar to this
     *         custom decor data
     * @see #isSimilar(ItemStack)
     */
    @Contract("null -> false")
    boolean isSimilar(final @Nullable CustomDecorData<? extends CustomDecorData<?>> data);

    boolean isSittable();

    boolean isWrenchable();

    boolean isLightable();

    boolean isLightTyped();

    boolean isFaceTyped();

    /**
     * Register the associated recipes of this custom decor
     * with the server
     *
     * @see #unregisterRecipes()
     */
    void registerRecipes();

    /**
     * Unregister the associated recipes of this custom decor
     * from the server
     *
     * @see #registerRecipes()
     */
    void unregisterRecipes();

    void place(
            final @NotNull Block replaceableBlock,
            final @NotNull Player player,
            final @NotNull BlockFace blockFace,
            final @Nullable EquipmentSlot hand,
            final @Nullable Component customName
    );

    /**
     * Perform the right click action of this custom decor
     */
    void doRightClickAction(
            final @NotNull CustomDecorRightClickEvent event,
            final @NotNull Interaction interaction
    );

    static @NotNull Optional<CustomDecorData<?>> fromKey(final @Nullable String key) {
        if (key == null) return Optional.empty();

        final CustomDecorType type = CustomDecorType.fromKey(key);
        return type == null
                ? Optional.empty()
                : Optional.of(CustomDecorType.CLASS_TO_DATA_MAP.get(type.getClazz()));
    }

    static <D extends CustomDecorData<D>> @NotNull Optional<D> fromKey(
            final @Nullable String key,
            final @Nullable Class<D> clazz
    ) {
        if (
                key == null
                        || clazz == null
        ) return Optional.empty();

        final CustomDecorType type = CustomDecorType.fromKey(key);
        return type != null
                && clazz.isInstance(type.getCustomDecorData())
                ? Optional.of(type.getCustomDecorData(clazz))
                : Optional.empty();
    }

    static <D extends CustomDecorData<?>> @NotNull Optional<D> fromClass(final @Nullable Class<D> clazz) {
        return clazz == null
                ? Optional.empty()
                : Optional.ofNullable(clazz.cast(CustomDecorType.CLASS_TO_DATA_MAP.get(clazz)));
    }

    static @NotNull Optional<CustomDecorData<?>> fromItemStack(final @Nullable ItemStack itemStack) {
        if (itemStack == null) return Optional.empty();

        final ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null
                ? Optional.empty()
                : fromKey(itemMeta.getPersistentDataContainer().get(CustomDecorType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING));
    }

    static <D extends CustomDecorData<D>> @NotNull Optional<D> fromItemStack(
            final @Nullable ItemStack itemStack,
            final @Nullable Class<D> clazz
    ) {
        if (
                itemStack == null
                        || clazz == null
        ) return Optional.empty();

        final ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null
                ? Optional.empty()
                : fromKey(
                itemMeta.getPersistentDataContainer().get(CustomDecorType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING),
                clazz
        );
    }

    static @NotNull Optional<CustomDecorData<?>> fromInteraction(final @Nullable Interaction interaction) {
        if (interaction == null) return Optional.empty();

        final PersistentDataContainer container = interaction.getPersistentDataContainer();

        if (container.isEmpty()) return Optional.empty();

        if (DecorHitBox.isHitBoxChild(interaction)) {
            final String uuid = container.get(DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY, PersistentDataType.STRING);

            try {
                return StringUtils.isBlank(uuid)
                        || !(interaction.getWorld().getEntity(UUID.fromString(uuid)) instanceof final Interaction parent)
                        ? Optional.empty()
                        : fromKey(parent.getPersistentDataContainer().get(CustomDecorType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING));
            } catch (final IllegalArgumentException ignored) {
                return Optional.empty();
            }
        } else if (DecorHitBox.isHitBoxParent(interaction)) {
            return fromKey(container.get(CustomDecorType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING));
        }

        return Optional.empty();
    }

    static <D extends CustomDecorData<D>> @NotNull Optional<D> fromInteraction(
            final @Nullable Interaction interaction,
            final @Nullable Class<D> clazz
    ) {
        if (
                interaction == null
                        || clazz == null
        ) return Optional.empty();

        final PersistentDataContainer container = interaction.getPersistentDataContainer();

        if (container.isEmpty()) return Optional.empty();

        if (DecorHitBox.isHitBoxParent(interaction)) {
            return fromKey(
                    container.get(CustomDecorType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING),
                    clazz
            );
        } else if (container.has(DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY)) {
            final String uuid = container.get(DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY, PersistentDataType.STRING);

            try {
                return StringUtils.isBlank(uuid)
                        || !(interaction.getWorld().getEntity(UUID.fromString(uuid)) instanceof final Interaction parent)
                        ? Optional.empty()
                        : fromKey(
                        parent.getPersistentDataContainer().get(CustomDecorType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING),
                        clazz
                );
            } catch (final IllegalArgumentException ignored) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    static @NotNull Optional<CustomDecorData<?>> fromBlock(final @Nullable Block block) {
        return block == null
                ? Optional.empty()
                : fromInteraction(MSDecorUtils.getNearbyInteraction(block.getLocation().toCenterLocation()));
    }

    static <D extends CustomDecorData<D>> @NotNull Optional<D> fromBlock(
            final @Nullable Block block,
            final @Nullable Class<D> clazz
    ) {
        return block == null
                || clazz == null
                ? Optional.empty()
                : fromInteraction(MSDecorUtils.getNearbyInteraction(block.getLocation().toCenterLocation()), clazz);
    }

    static void destroyInBlock(
            final @Nullable Player player,
            final @Nullable Block block
    ) {
        if (
                player == null
                || block == null
        ) return;

        final ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        final Location location = block.getLocation().toCenterLocation();
        final AABB aabb = LocationUtils.bukkitToAABB(org.bukkit.util.BoundingBox.of(location, 5, 5, 5));
        final Predicate<net.minecraft.world.entity.Entity> predicate = entity -> entity instanceof net.minecraft.world.entity.Interaction;

        for (final var entity : LocationUtils.getNearbyNMSEntities(level, aabb, predicate)) {
            if (entity.getBukkitEntity() instanceof final Interaction interaction) {
                destroy(player, interaction, location);
            }
        }
    }

    static void destroy(
            final @Nullable Player player,
            final @Nullable Interaction interacted,
            final @Nullable Location interactedLocation
    ) {
        if (
                player == null
                || interacted == null
                || interactedLocation == null
        ) return;

        DecorHitBox.Elements.fromInteraction(interacted)
        .ifPresent(
                elements -> {
                    final var data = elements.getData();

                    final CustomDecorBreakEvent event = new CustomDecorBreakEvent(data, elements, player);
                    Bukkit.getPluginManager().callEvent(event);

                    if (event.isCancelled()) return;

                    final ItemDisplay display = elements.getDisplay();
                    final CraftWorld world = (CraftWorld) player.getWorld();

                    if (player.getGameMode() == GameMode.SURVIVAL) {
                        world.dropItemNaturally(
                                LocationUtils.nmsToBukkit(elements.getNMSBoundingBox().getCenter()).subtract(0.25d, 0.25d, 0.25d),
                                display.getItemStack()
                        );
                    }

                    if (!data.getHitBox().getType().isNone()) {
                        final BoundingBox bb = elements.getNMSBoundingBox();

                        CustomDecorDataImpl.fillBlocks(
                                player.getName(),
                                world.getHandle(),
                                LocationUtils.getBlockPosesBetween(bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ()),
                                Blocks.AIR
                        );
                    }

                    for (final var interaction : elements.getInteractions()) {
                        interaction.remove();
                    }

                    display.remove();
                    data.getSoundGroup().playBreakSound(interactedLocation.toCenterLocation());
                }
        );
    }

    interface Type<D extends CustomDecorData<D>> {
        @NotNull Class<D> getParentClass();

        @NotNull String getKey();

        @NotNull ItemStack getItem();

        @Contract("null -> false")
        boolean isSimilar(final @Nullable Type<? extends Type<?>> type);

        @NotNull D buildData();
    }
}
