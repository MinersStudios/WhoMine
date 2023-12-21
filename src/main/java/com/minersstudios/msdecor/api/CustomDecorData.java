package com.minersstudios.msdecor.api;

import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.MSDecorUtils;
import com.minersstudios.msdecor.api.action.DecorBreakAction;
import com.minersstudios.msdecor.api.action.DecorClickAction;
import com.minersstudios.msdecor.api.action.DecorPlaceAction;
import com.minersstudios.msdecor.event.CustomDecorBreakEvent;
import com.minersstudios.msdecor.event.CustomDecorClickEvent;
import com.minersstudios.msdecor.event.CustomDecorPlaceEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Interaction;
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

/**
 * This interface represents the data of a custom decor in the game. It provides
 * methods to get and manipulate the properties of the decor, etc.
 * <br>
 * It is recommended to extend the {@link CustomDecorDataImpl} class when
 * creating custom decor data classes. This class provides a base implementation
 * of the {@link CustomDecorData} interface. It also provides a builder to
 * create custom decor data instances.
 *
 * @param <D> The type of the custom decor data
 * @see CustomDecorDataImpl
 */
public interface CustomDecorData<D extends CustomDecorData<D>> extends Keyed {

    /**
     * @return The unique namespaced key identifying the custom decor
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
    @NotNull @Unmodifiable Set<Facing> getFacingSet();

    /**
     * @return The clone of the {@link ItemStack} representing the custom decor
     */
    @NotNull ItemStack getItem();

    /**
     * @return The {@link Material} of custom decor
     */
    @NotNull SoundGroup getSoundGroup();

    /**
     * @return An unmodifiable list of {@link Recipe} entries representing the
     *         associated recipes
     */
    @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> recipes();

    /**
     * @return The unmodifiable set of {@link DecorParameter}
     * @see DecorParameter
     */
    @NotNull @Unmodifiable EnumSet<DecorParameter> parameterSet();

    /**
     * @return Clone of the different custom decor types for wrenchable and
     *         typed custom decor
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#WRENCHABLE}
     *                                       or {@link DecorParameter#TYPED}
     */
    Type<D> @NotNull [] types() throws UnsupportedOperationException;

    /**
     * @param interaction Interaction to get a type from
     * @return Custom decor type obtained from given interaction
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#WRENCHABLE}
     *                                       or {@link DecorParameter#TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getTypeOf(final @Nullable Interaction interaction) throws UnsupportedOperationException;

    /**
     * @param itemStack Item stack to get a type from
     * @return Custom decor type obtained from given item stack
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#WRENCHABLE}
     *                                       or {@link DecorParameter#TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getTypeOf(final @Nullable ItemStack itemStack) throws UnsupportedOperationException;

    /**
     * @param interaction Interaction to get the next type from
     * @return Next custom decor type in the {@link #types()}, obtained from
     *         given interaction
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#WRENCHABLE}
     *                                       or {@link DecorParameter#TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getNextType(final @Nullable Interaction interaction) throws UnsupportedOperationException;

    /**
     * @param itemStack Item stack to get the next type from
     * @return Next custom decor type in the {@link #types()}, obtained from
     *         given item stack
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#WRENCHABLE}
     *                                       or {@link DecorParameter#TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getNextType(final @Nullable ItemStack itemStack) throws UnsupportedOperationException;

    /**
     * @param type Current type to get the next type from
     * @return Next custom decor type in the {@link #types()} array
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#WRENCHABLE}
     *                                       or {@link DecorParameter#TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getNextType(final @Nullable Type<? extends CustomDecorData<?>> type) throws UnsupportedOperationException;

    /**
     * @return The unmodifiable map of {@link BlockFace} to {@link Type}, used
     *         to place different types of custom decor according to the face
     *         when placed in the world
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#FACE_TYPED}
     */
    @NotNull @Unmodifiable Map<Facing, Type<D>> typeFaceMap() throws UnsupportedOperationException;

    /**
     * @param interaction Interaction to get a type from
     * @return Custom decor face type obtained from given interaction
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#FACE_TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getFaceTypeOf(final @Nullable Interaction interaction) throws UnsupportedOperationException;

    /**
     * @param itemStack Item stack to get a type from
     * @return Custom decor face type obtained from given item stack
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#FACE_TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getFaceTypeOf(final @Nullable ItemStack itemStack) throws UnsupportedOperationException;

    /**
     * @param blockFace Block face to get a face type from
     * @return Custom decor face type obtained from given block face
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#FACE_TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getFaceTypeOf(final @Nullable BlockFace blockFace) throws UnsupportedOperationException;

    /**
     * @param facing Facing to get a face type from
     * @return Custom decor face type obtained from given facing
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#FACE_TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getFaceTypeOf(final @Nullable Facing facing) throws UnsupportedOperationException;

    /**
     * @return The unmodifiable map of {@link Integer} to {@link Type}, used to
     *         place different types of custom decor with different light levels
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#LIGHT_TYPED}
     */
    @NotNull @Unmodifiable Map<Integer, Type<D>> typeLightLevelMap() throws UnsupportedOperationException;

    /**
     * @param interaction Interaction to get a type from
     * @return Custom decor light type obtained from given interaction
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#LIGHT_TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getLightTypeOf(final @Nullable Interaction interaction) throws UnsupportedOperationException;

    /**
     * @param itemStack Item stack to get a type from
     * @return Custom decor light type obtained from given item stack
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#LIGHT_TYPED}
     */
    @Contract("null -> null")
    @Nullable Type<D> getLightTypeOf(final @Nullable ItemStack itemStack) throws UnsupportedOperationException;

    /**
     * @param lightLevel Light level to get a light type from
     * @return Custom decor light type obtained from a given light level
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#LIGHT_TYPED}
     */
    @Nullable Type<D> getLightTypeOf(final int lightLevel) throws UnsupportedOperationException;

    /**
     * @return Clone of the different custom decor light levels for lightable
     *         and light typed custom decor
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#LIGHT_TYPED}
     *                                       or {@link DecorParameter#LIGHTABLE}
     */
    int @NotNull [] lightLevels() throws UnsupportedOperationException;

    /**
     * @param interaction Interaction to get a light level from
     * @return Light level of placed custom decor obtained from given interaction
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#LIGHT_TYPED}
     *                                       or {@link DecorParameter#LIGHTABLE}
     */
    int getLightLevelOf(final @Nullable Interaction interaction) throws UnsupportedOperationException;

    /**
     * @param itemStack Item stack to get a light level from
     * @return Light level of custom decor light type obtained from given item
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#LIGHT_TYPED}
     */
    int getLightLevelOf(final @Nullable ItemStack itemStack) throws UnsupportedOperationException;

    /**
     * @param interaction Interaction to get the next light level from
     * @return Next light level in the {@link #lightLevels()} array, obtained
     *         from given interaction
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#LIGHT_TYPED}
     *                                       or {@link DecorParameter#LIGHTABLE}
     */
    int getNextLightLevel(final @Nullable Interaction interaction) throws UnsupportedOperationException;

    /**
     * @param itemStack Item stack to get the next light level from
     * @return Next light level in the {@link #lightLevels()} array, obtained
     *         from given item stack
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#LIGHT_TYPED}
     */
    int getNextLightLevel(final @Nullable ItemStack itemStack) throws UnsupportedOperationException;

    /**
     * @param lightLevel Current light level to get the next light level from
     * @return Next light level in the {@link #lightLevels()} array
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#LIGHT_TYPED}
     *                                       or {@link DecorParameter#LIGHTABLE}
     */
    int getNextLightLevel(final int lightLevel) throws UnsupportedOperationException;

    /**
     * @return Sit height of custom decor for sittable custom decor
     * @throws UnsupportedOperationException If the custom decor is not
     *                                       {@link DecorParameter#SITTABLE}
     */
    double getSitHeight() throws UnsupportedOperationException;

    /**
     * @return The action of a custom decor that occurs when it is pressed by
     *         the player's hand
     */
    @NotNull DecorClickAction getClickAction();

    /**
     * @return The action of a custom decor that occurs when it is placed in the
     *         world
     */
    @NotNull DecorPlaceAction getPlaceAction();

    /**
     * @return The action of a custom decor that occurs when it is broken
     */
    @NotNull DecorBreakAction getBreakAction();

    /**
     * @param first The first parameter to check for existence
     * @param rest  The rest of the parameters to check for existence
     *              (optional)
     * @return True if the custom decor has all the given parameters
     * @see DecorParameter
     */
    boolean hasParameters(
            final @NotNull DecorParameter first,
            final DecorParameter @NotNull ... rest
    );

    /**
     * Check whether a given item stack is similar to this custom decor
     *
     * @param itemStack The {@link ItemStack} to compare with this custom decor
     * @return True if the given item stack is similar to the item stack of this
     *         custom decor
     */
    @Contract("null -> false")
    boolean isSimilar(final @Nullable ItemStack itemStack);

    /**
     * Check whether given custom decor data is similar to this custom decor
     * data
     *
     * @param data The dara to compare with this custom decor data
     * @return True if the given custom decor data is similar to this custom
     *         decor data
     * @see #isSimilar(ItemStack)
     */
    @Contract("null -> false")
    boolean isSimilar(final @Nullable CustomDecorData<? extends CustomDecorData<?>> data);

    /**
     * @return True if this custom decor has the paintable parameter in its
     *         {@link #parameterSet()}
     * @see DecorParameter#PAINTABLE
     */
    boolean isPaintable();

    /**
     * @return True if this custom decor has the sittable parameter in its
     *         {@link #parameterSet()}
     * @see DecorParameter#SITTABLE
     */
    boolean isSittable();

    /**
     * @return True if this custom decor has the wrenchable parameter in its
     *         {@link #parameterSet()}
     * @see DecorParameter#WRENCHABLE
     */
    boolean isWrenchable();

    /**
     * @return True if this custom decor has the lightable parameter in its
     *         {@link #parameterSet()}
     * @see DecorParameter#LIGHTABLE
     */
    boolean isLightable();

    /**
     * @return True if this custom decor has the typed parameter in its
     *         {@link #parameterSet()}
     * @see DecorParameter#TYPED
     */
    boolean isTyped();

    /**
     * @return True if this custom decor has the light typed parameter in its
     *         {@link #parameterSet()}
     * @see DecorParameter#LIGHT_TYPED
     */
    boolean isLightTyped();

    /**
     * @return True if this custom decor has the face typed parameter in its
     *         {@link #parameterSet()}
     * @see DecorParameter#FACE_TYPED
     */
    boolean isFaceTyped();

    /**
     * @return True if this custom decor has any of the typed parameters in its
     *         {@link #parameterSet()}
     * @see DecorParameter#WRENCHABLE
     * @see DecorParameter#TYPED
     * @see DecorParameter#LIGHT_TYPED
     * @see DecorParameter#FACE_TYPED
     */
    boolean isAnyTyped();

    /**
     * @return True, if this custom decor drops its type when destroyed,
     *         otherwise it drops the main item stack
     * @see #isAnyTyped()
     */
    boolean isDropType();

    /**
     * Performs the click action of this custom decor
     * (called when the custom decor is clicked)
     *
     * @param event The event to perform the click action
     * @see DecorClickAction
     */
    void doClickAction(final @NotNull CustomDecorClickEvent event);

    /**
     * Performs the break action of this custom decor
     * (called when the custom decor is broken)
     *
     * @param event The event to perform the break action
     * @see DecorBreakAction
     */
    void doBreakAction(final @NotNull CustomDecorBreakEvent event);

    /**
     * Perform the place action of this custom decor
     * (called when the custom decor is placed)
     *
     * @param event The event to perform the place action
     * @see DecorPlaceAction
     */
    void doPlaceAction(final @NotNull CustomDecorPlaceEvent event);

    /**
     * Register the associated recipes of this custom decor with the server
     *
     * @param server The server to register the recipes with
     * @see #unregisterRecipes(Server)
     */
    void registerRecipes(final @NotNull Server server);

    /**
     * Unregister the associated recipes of this custom decor from the server
     *
     * @param server The server to unregister the recipes from
     * @see #registerRecipes(Server)
     */
    void unregisterRecipes(final @NotNull Server server);

    /**
     * Places the custom decor
     *
     * @param position   The location to place the custom decor at
     * @param player     The player who placed the custom decor
     * @param blockFace  The face of the block to place the custom decor at
     * @param hand       The hand used to place the custom decor
     * @param customName The custom name of the custom decor
     * @throws IllegalArgumentException If the world is not specified in the
     *                                  position
     */
    void place(
            final @NotNull MSPosition position,
            final @NotNull Player player,
            final @NotNull BlockFace blockFace,
            final @Nullable EquipmentSlot hand,
            final @Nullable Component customName
    ) throws IllegalArgumentException;


    /**
     * @param key The key to get the custom decor data from
     * @return An optional of the custom decor data obtained from the given key
     */
    static @NotNull Optional<CustomDecorData<?>> fromKey(final @Nullable String key) {
        if (key == null) {
            return Optional.empty();
        }

        final CustomDecorType type = CustomDecorType.fromKey(key);

        return type == null
                ? Optional.empty()
                : Optional.of(type.getCustomDecorData());
    }

    /**
     * @param key   The key to get the custom decor data from
     * @param clazz The class of the custom decor data to get
     * @param <D>   The type of the custom decor data to get
     * @return An optional of the custom decor data obtained from the given key
     *         and class if the class is an instance of the custom decor data
     */
    static <D extends CustomDecorData<D>> @NotNull Optional<D> fromKey(
            final @Nullable String key,
            final @Nullable Class<D> clazz
    ) {
        if (
                key == null
                || clazz == null
        ) {
            return Optional.empty();
        }

        final CustomDecorType type = CustomDecorType.fromKey(key);

        return type != null
                && clazz.isInstance(type.getCustomDecorData())
                ? Optional.of(type.getCustomDecorData(clazz))
                : Optional.empty();
    }

    /**
     * @param clazz The class of the custom decor data to get
     * @param <D>   The type of the custom decor data to get
     * @return An optional of the custom decor data obtained from the given class
     *         if the class is an instance of the custom decor data
     */
    static <D extends CustomDecorData<?>> @NotNull Optional<D> fromClass(final @Nullable Class<D> clazz) {
        if (clazz == null) {
            return Optional.empty();
        }

        final CustomDecorType type = CustomDecorType.fromClass(clazz);

        return type == null
                ? Optional.empty()
                : Optional.of(clazz.cast(type));
    }

    /**
     * @param itemStack The item stack to get the custom decor data from
     * @return An optional of the custom decor data obtained from the given item
     *         stack. It gets the key from the item stack and then gets the
     *         custom decor data from the received key.
     * @see #fromKey(String)
     */
    static @NotNull Optional<CustomDecorData<?>> fromItemStack(final @Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return Optional.empty();
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        return itemMeta == null
                ? Optional.empty()
                : fromKey(
                        itemMeta.getPersistentDataContainer()
                        .get(
                                CustomDecorType.TYPE_NAMESPACED_KEY,
                                PersistentDataType.STRING
                        )
                );
    }

    /**
     * @param itemStack The item stack to get the custom decor data from
     * @param clazz     The class of the custom decor data to get
     * @param <D>       The type of the custom decor data to get
     * @return An optional of the custom decor data obtained from the given item
     *         stack and class if the class is an instance of the custom decor
     *         data. It gets the key from the item stack and then gets the
     *         custom decor data from the received key and specified class.
     * @see #fromKey(String, Class)
     */
    static <D extends CustomDecorData<D>> @NotNull Optional<D> fromItemStack(
            final @Nullable ItemStack itemStack,
            final @Nullable Class<D> clazz
    ) {
        if (
                itemStack == null
                || clazz == null
        ) {
            return Optional.empty();
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        return itemMeta == null
                ? Optional.empty()
                : fromKey(
                        itemMeta.getPersistentDataContainer()
                        .get(
                                CustomDecorType.TYPE_NAMESPACED_KEY,
                                PersistentDataType.STRING
                        ),
                        clazz
                );
    }

    /**
     * @param interaction The interaction to get the custom decor data from
     * @return An optional of the custom decor data obtained from the given
     *         interaction. It gets the key from the interaction and then gets
     *         the custom decor data from the received key.
     * @see #fromKey(String)
     */
    static @NotNull Optional<CustomDecorData<?>> fromInteraction(final @Nullable Interaction interaction) {
        if (interaction == null) {
            return Optional.empty();
        }

        final PersistentDataContainer container = interaction.getPersistentDataContainer();

        if (container.isEmpty()) {
            return Optional.empty();
        }

        if (DecorHitBox.isChild(interaction)) {
            final String uuid = container.get(DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY, PersistentDataType.STRING);

            try {
                return ChatUtils.isBlank(uuid)
                        || !(interaction.getWorld().getEntity(UUID.fromString(uuid)) instanceof final Interaction parent)
                        ? Optional.empty()
                        : fromKey(
                                parent.getPersistentDataContainer()
                                .get(
                                        CustomDecorType.TYPE_NAMESPACED_KEY,
                                        PersistentDataType.STRING
                                )
                        );
            } catch (final IllegalArgumentException ignored) {
                return Optional.empty();
            }
        } else if (DecorHitBox.isParent(interaction)) {
            return fromKey(
                    container.get(
                            CustomDecorType.TYPE_NAMESPACED_KEY,
                            PersistentDataType.STRING
                    )
            );
        }

        return Optional.empty();
    }

    /**
     * @param interaction The interaction to get the custom decor data from
     * @param clazz       The class of the custom decor data to get
     * @param <D>         The type of the custom decor data to get
     * @return An optional of the custom decor data obtained from the given
     *         interaction and class if the class is an instance of the custom
     *         decor data. It gets the key from the interaction and then gets
     *         the custom decor data from the received key and specified class.
     * @see #fromKey(String, Class)
     */
    static <D extends CustomDecorData<D>> @NotNull Optional<D> fromInteraction(
            final @Nullable Interaction interaction,
            final @Nullable Class<D> clazz
    ) {
        if (
                interaction == null
                || clazz == null
        ) {
            return Optional.empty();
        }

        final PersistentDataContainer container = interaction.getPersistentDataContainer();

        if (container.isEmpty()) {
            return Optional.empty();
        }

        if (DecorHitBox.isChild(container)) {
            final String uuid = container.get(DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY, PersistentDataType.STRING);

            try {
                return ChatUtils.isBlank(uuid)
                        || !(interaction.getWorld().getEntity(UUID.fromString(uuid)) instanceof final Interaction parent)
                        ? Optional.empty()
                        : fromKey(
                                parent.getPersistentDataContainer()
                                .get(
                                        CustomDecorType.TYPE_NAMESPACED_KEY,
                                        PersistentDataType.STRING
                                ),
                                clazz
                        );
            } catch (final IllegalArgumentException ignored) {
                return Optional.empty();
            }
        } else if (DecorHitBox.isParent(container)) {
            return fromKey(
                    container.get(
                            CustomDecorType.TYPE_NAMESPACED_KEY,
                            PersistentDataType.STRING
                    ),
                    clazz
            );
        }

        return Optional.empty();
    }

    /**
     * @param block The block to get the custom decor data from
     * @return An optional of the custom decor data obtained from the location
     *         of the given block
     * @see #fromPosition(MSPosition)
     */
    static @NotNull Optional<CustomDecorData<?>> fromBlock(final @Nullable Block block) {
        return block == null
                ? Optional.empty()
                : fromPosition(
                        MSPosition.of(block.getLocation())
                );
    }

    /**
     * @param block The block to get the custom decor data from
     * @param clazz The class of the custom decor data to get
     * @param <D>   The type of the custom decor data to get
     * @return An optional of the custom decor data obtained from the location
     *         of the given block and class if the class is an instance of the
     *         custom decor data
     * @see #fromPosition(MSPosition, Class)
     */
    static <D extends CustomDecorData<D>> @NotNull Optional<D> fromBlock(
            final @Nullable Block block,
            final @Nullable Class<D> clazz
    ) {
        return block == null
                || clazz == null
                ? Optional.empty()
                : fromPosition(
                        MSPosition.of(block.getLocation()),
                        clazz
                );
    }

    /**
     * @param position The position to get the custom decor data from
     * @return An optional of the custom decor data obtained from the given
     *         position
     * @throws IllegalArgumentException If the world is not specified in the
     *                                  position
     * @see #fromInteraction(Interaction)
     */
    static @NotNull Optional<CustomDecorData<?>> fromPosition(final @Nullable MSPosition position) throws IllegalArgumentException {
        return position == null
                ? Optional.empty()
                : fromInteraction(
                        MSDecorUtils.getNearbyInteraction(position.center())
                );
    }

    /**
     * @param position The position to get the custom decor data from
     * @param clazz    The class of the custom decor data to get
     * @param <D>      The type of the custom decor data to get
     * @return An optional of the custom decor data obtained from the given
     *         position and class if the class is an instance of the custom
     *         decor data
     * @throws IllegalArgumentException If the world is not specified in the
     *                                  position
     * @see #fromInteraction(Interaction, Class)
     */
    static <D extends CustomDecorData<D>> @NotNull Optional<D> fromPosition(
            final @Nullable MSPosition position,
            final @Nullable Class<D> clazz
    ) throws IllegalArgumentException {
        return position == null
                || clazz == null
                ? Optional.empty()
                : fromInteraction(
                        MSDecorUtils.getNearbyInteraction(position.center()),
                        clazz
                );
    }

    /**
     * This interface represents the type of custom decor data
     *
     * @param <D> The custom decor data type that owns this type
     */
    interface Type<D extends CustomDecorData<D>> extends Keyed {

        /**
         * @return The unique namespaced key identifying the custom decor type
         */
        @NotNull NamespacedKey getKey();

        /**
         * @return The item stack representing the custom decor type
         */
        @NotNull ItemStack getItem();

        /**
         * @return True if the given object is the same as this custom decor
         *         type, false otherwise
         */
        @Override
        @Contract("null -> false")
        boolean equals(final @Nullable Object type);

        /**
         * @return A string representation of this custom decor type
         */
        @Override
        @NotNull String toString();

        /**
         * @return Custom decor data of this custom decor type
         */
        @NotNull D buildData();
    }
}
