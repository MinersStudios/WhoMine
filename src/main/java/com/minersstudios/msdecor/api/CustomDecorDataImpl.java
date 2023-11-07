package com.minersstudios.msdecor.api;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.location.MSBoundingBox;
import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.location.MSVector;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.mscore.util.*;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.api.action.DecorBreakAction;
import com.minersstudios.msdecor.api.action.DecorClickAction;
import com.minersstudios.msdecor.api.action.DecorPlaceAction;
import com.minersstudios.msdecor.event.CustomDecorBreakEvent;
import com.minersstudios.msdecor.event.CustomDecorClickEvent;
import com.minersstudios.msdecor.event.CustomDecorPlaceEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.block.CraftBlockStates;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Function;

import static com.minersstudios.mscore.plugin.MSPlugin.getGlobalCache;

public abstract class CustomDecorDataImpl<D extends CustomDecorData<D>> implements CustomDecorData<D> {
    protected final NamespacedKey namespacedKey;
    protected final DecorHitBox hitBox;
    protected final Facing facing;
    protected final SoundGroup soundGroup;
    protected final ItemStack itemStack;
    protected final List<Map.Entry<Recipe, Boolean>> recipes;
    protected final EnumSet<DecorParameter> parameterSet;
    protected final double sitHeight;
    protected final int[] lightLevels;
    protected final CustomDecorData.Type<D>[] types;
    protected final EnumMap<Facing, CustomDecorData.Type<D>> faceTypeMap;
    protected final Map<Integer, CustomDecorData.Type<D>> lightLevelTypeMap;
    protected final boolean dropsType;
    protected final DecorClickAction clickAction;
    protected final DecorPlaceAction placeAction;
    protected final DecorBreakAction breakAction;

    protected CustomDecorDataImpl() throws IllegalArgumentException {
        final Builder builder = this.builder();

        builder.preBuild();

        this.namespacedKey = builder.namespacedKey;
        this.hitBox = builder.hitBox;
        this.facing = builder.facing;
        this.soundGroup = builder.soundGroup;
        this.itemStack = builder.itemStack;
        this.recipes =
                builder.recipeBuilderList == null
                ? Collections.emptyList()
                : new ArrayList<>(builder.recipeBuilderList.size());

        if (builder.recipeBuilderList != null) {
            for (final var entry : builder.recipeBuilderList) {
                final var recipeBuilder = entry.getKey();

                if (recipeBuilder.namespacedKey() == null) {
                    recipeBuilder.namespacedKey(this.namespacedKey);
                }

                if (recipeBuilder.result() == null) {
                    recipeBuilder.result(this.itemStack);
                }

                this.recipes.add(
                        Map.entry(
                                recipeBuilder.build(),
                                entry.getValue()
                        )
                );
            }
        }

        this.parameterSet = builder.parameterSet;
        this.sitHeight = builder.sitHeight;
        this.types = builder.types;
        this.faceTypeMap =
                builder.faceTypeMap == null
                ? new EnumMap<>(Facing.class)
                : builder.faceTypeMap;
        this.lightLevels = builder.lightLevels;
        this.lightLevelTypeMap =
                builder.lightLevelTypeMap == null
                ? Collections.emptyMap()
                : builder.lightLevelTypeMap;
        this.clickAction = builder.clickAction;
        this.placeAction = builder.placeAction;
        this.breakAction = builder.breakAction;
        this.dropsType = builder.dropsType;
    }

    protected abstract @NotNull Builder builder();

    @Override
    public final @NotNull NamespacedKey getKey() {
        return this.namespacedKey;
    }

    @Override
    public final @NotNull DecorHitBox getHitBox() {
        return this.hitBox;
    }

    @Override
    public final @NotNull Facing getFacing() {
        return this.facing;
    }

    @Override
    public final @NotNull ItemStack getItem() {
        return this.itemStack.clone();
    }

    @Override
    public final @NotNull SoundGroup getSoundGroup() {
        return this.soundGroup;
    }

    @Override
    public final @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> recipes() {
        return this.recipes;
    }

    @Override
    public @NotNull @Unmodifiable Set<DecorParameter> parameterSet() {
        return this.parameterSet.clone();
    }

    @Override
    public CustomDecorData.Type<D> @NotNull [] types() throws UnsupportedOperationException {
        if (
                !this.isWrenchable()
                || !this.isTyped()
        ) {
            throw new UnsupportedOperationException("This custom decor is not wrenchable or typed!");
        }

        return this.types;
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getTypeOf(final @Nullable Interaction interaction) throws UnsupportedOperationException {
        return interaction == null
                ? null
                : this.getTypeOf(
                        CustomDecor.fromInteraction(interaction).map(
                                customDecor -> customDecor.getDisplay().getItemStack()
                        ).orElse(null)
                );
    }
    
    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getTypeOf(final @Nullable ItemStack itemStack) throws UnsupportedOperationException {
        if (
                !this.isWrenchable()
                && !this.isTyped()
        ) {
            throw new UnsupportedOperationException("This custom decor is not wrenchable or typed!");
        }

        if (itemStack == null) return null;

        final String key = itemStack.getItemMeta().getPersistentDataContainer().get(
                CustomDecorType.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING
        );

        if (key == null) return null;
        if (!CustomDecorType.matchesTypedKey(key)) return this.types[0];
        
        for (final var type : this.types) {
            if (key.equals(type.getKey().getKey())) {
                return type;
            }
        }
        
        return null;
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getNextType(final @Nullable Interaction interaction) throws UnsupportedOperationException {
        return this.getNextType(this.getTypeOf(interaction));
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getNextType(final @Nullable ItemStack itemStack) throws UnsupportedOperationException {
        return this.getNextType(this.getTypeOf(itemStack));
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getNextType(final @Nullable CustomDecorData.Type<? extends CustomDecorData<?>> type) throws UnsupportedOperationException {
        if (
                !this.isWrenchable()
                && !this.isTyped()
        ) {
            throw new UnsupportedOperationException("This custom decor is not wrenchable or typed!");
        }

        if (type == null) return null;

        final int length = this.types.length;

        for (int i = 0; i < length; ++i) {
            if (this.types[i].equals(type)) {
                return this.types[Math.floorMod(i + 1, length)];
            }
        }

        return null;
    }

    @Override
    public @NotNull @Unmodifiable Map<Facing, CustomDecorData.Type<D>> typeFaceMap() throws UnsupportedOperationException {
        if (!this.isFaceTyped()) {
            throw new UnsupportedOperationException("This custom decor is not face typed!");
        }

        return Collections.unmodifiableMap(this.faceTypeMap);
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getFaceTypeOf(final @Nullable Interaction interaction) throws UnsupportedOperationException {
        if (!this.isFaceTyped()) {
            throw new UnsupportedOperationException("This custom decor is not face typed!");
        }

        return interaction == null
                ? null
                : this.getFaceTypeOf(
                        CustomDecor.fromInteraction(interaction).map(
                                customDecor -> customDecor.getDisplay().getItemStack()
                        ).orElse(null)
                );
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getFaceTypeOf(final @Nullable ItemStack itemStack) throws UnsupportedOperationException {
        if (!this.isFaceTyped()) {
            throw new UnsupportedOperationException("This custom decor is not face typed!");
        }

        if (itemStack == null) return null;

        final String key = itemStack.getItemMeta().getPersistentDataContainer().get(
                CustomDecorType.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING
        );

        if (key == null) return null;
        if (!CustomDecorType.matchesTypedKey(key)) {
            return this.faceTypeMap.getOrDefault(Facing.FLOOR, null);
        }

        for (final var type : this.faceTypeMap.values()) {
            if (key.equals(type.getKey().getKey())) {
                return type;
            }
        }

        return null;
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getFaceTypeOf(final @Nullable BlockFace blockFace) throws UnsupportedOperationException {
        if (!this.isFaceTyped()) {
            throw new UnsupportedOperationException("This custom decor is not face typed!");
        }

        return !this.facing.hasFace(blockFace)
                ? null
                : this.getFaceTypeOf(Facing.fromBlockFace(blockFace));
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getFaceTypeOf(final @Nullable Facing facing) throws UnsupportedOperationException {
        if (!this.isFaceTyped()) {
            throw new UnsupportedOperationException("This custom decor is not face typed!");
        }

        return facing == null
                ? null
                : this.faceTypeMap.getOrDefault(facing, null);
    }

    @Override
    public @NotNull @Unmodifiable Map<Integer, CustomDecorData.Type<D>> typeLightLevelMap() throws UnsupportedOperationException {
        if (!this.isLightTyped()) {
            throw new UnsupportedOperationException("This custom decor is not light typed!");
        }

        return this.lightLevelTypeMap;
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getLightTypeOf(final @Nullable Interaction interaction) throws UnsupportedOperationException {
        if (!this.isLightTyped()) {
            throw new UnsupportedOperationException("This custom decor is not light typed!");
        }

        return interaction == null
                ? null
                : this.getLightTypeOf(
                        CustomDecor.fromInteraction(interaction).map(
                                customDecor -> customDecor.getDisplay().getItemStack()
                        ).orElse(null)
                );
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getLightTypeOf(final @Nullable ItemStack itemStack) throws UnsupportedOperationException {
        if (!this.isLightTyped()) {
            throw new UnsupportedOperationException("This custom decor is not light typed!");
        }

        if (itemStack == null) return null;

        final String key = itemStack.getItemMeta().getPersistentDataContainer().get(
                CustomDecorType.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING
        );

        if (key == null) return null;
        if (!CustomDecorType.matchesTypedKey(key)) {
            return this.lightLevelTypeMap.getOrDefault(this.lightLevels[0], null);
        }

        for (final var type : this.lightLevelTypeMap.values()) {
            if (key.equals(type.getKey().getKey())) {
                return type;
            }
        }

        return null;
    }

    @Override
    public @Nullable CustomDecorData.Type<D> getLightTypeOf(final int lightLevel) throws UnsupportedOperationException {
        if (!this.isLightTyped()) {
            throw new UnsupportedOperationException("This custom decor is not light typed!");
        }

        return lightLevel < 0 || lightLevel > 15
                ? null
                : this.lightLevelTypeMap.getOrDefault(lightLevel, null);
    }

    @Override
    public int @NotNull [] lightLevels() throws UnsupportedOperationException {
        if (
                !this.isLightable()
                && !this.isLightTyped()
        ) {
            throw new UnsupportedOperationException("This custom decor is not lightable!");
        }

        return this.lightLevels.clone();
    }

    @Override
    public int getLightLevelOf(final @Nullable Interaction interaction) throws UnsupportedOperationException {
        return interaction != null
                && interaction.getWorld().getBlockAt(interaction.getLocation()).getBlockData() instanceof Light light
                ? light.getLevel()
                : -1;
    }

    @Override
    public int getLightLevelOf(final @Nullable ItemStack itemStack) throws UnsupportedOperationException {
        if (!this.isLightTyped()) {
            throw new UnsupportedOperationException("This custom decor is not light typed!");
        }

        if (itemStack == null) return this.lightLevels[0];

        final String key = itemStack.getItemMeta().getPersistentDataContainer().get(
                CustomDecorType.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING
        );

        if (!CustomDecorType.matchesTypedKey(key)) return this.lightLevels[0];

        for (final var entry : this.lightLevelTypeMap.entrySet()) {
            if (key.equals(entry.getValue().getKey().getKey())) {
                return entry.getKey();
            }
        }

        return this.lightLevels[0];
    }

    @Override
    public int getNextLightLevel(final @Nullable Interaction interaction) throws UnsupportedOperationException {
        return this.getNextLightLevel(this.getLightLevelOf(interaction));
    }

    @Override
    public int getNextLightLevel(final @Nullable ItemStack itemStack) throws UnsupportedOperationException {
        return this.getNextLightLevel(this.getLightLevelOf(itemStack));
    }

    @Override
    public int getNextLightLevel(final int lightLevel) throws UnsupportedOperationException {
        if (
                !this.isLightable()
                && !this.isLightTyped()
        ) {
            throw new UnsupportedOperationException("This custom decor is not lightable!");
        }

        final int length = this.lightLevels.length;

        for (int currentIndex = 0; currentIndex < length; ++currentIndex) {
            if (this.lightLevels[currentIndex] == lightLevel) {
                return this.lightLevels[Math.floorMod(currentIndex + 1, length)];
            }
        }

        return this.lightLevels[0];
    }

    @Override
    public double getSitHeight() throws UnsupportedOperationException {
        if (!this.isSittable()) {
            throw new UnsupportedOperationException("This custom decor is not sittable!");
        }

        return this.sitHeight;
    }

    @Override
    public @NotNull DecorClickAction getClickAction() {
        return this.clickAction;
    }

    @Override
    public @NotNull DecorPlaceAction getPlaceAction() {
        return this.placeAction;
    }

    @Override
    public @NotNull DecorBreakAction getBreakAction() {
        return this.breakAction;
    }

    @Override
    public boolean hasParameters(
            final @NotNull DecorParameter first,
            final @NotNull DecorParameter... rest
    ) {
        return this.parameterSet.contains(first)
                && (
                        rest.length == 0
                        || this.parameterSet.containsAll(Arrays.asList(rest))
                );
    }

    @Override
    public final boolean isSimilar(final @Nullable ItemStack itemStack) {
        if (
                itemStack == null
                || itemStack.getType() != this.itemStack.getType()
                || !itemStack.hasItemMeta()
                || !itemStack.getItemMeta().hasCustomModelData()
                || !this.itemStack.getItemMeta().hasCustomModelData()
        ) return false;

        return itemStack.getItemMeta().getCustomModelData() == this.itemStack.getItemMeta().getCustomModelData();
    }

    @Override
    public final boolean isSimilar(final @Nullable CustomDecorData<? extends CustomDecorData<?>> customDecorData) {
        return customDecorData != null
                && (
                        customDecorData == this
                        || this.isSimilar(customDecorData.getItem())
                );
    }

    @Override
    public boolean isPaintable() {
        return this.parameterSet.contains(DecorParameter.PAINTABLE);
    }

    @Override
    public boolean isSittable() {
        return this.parameterSet.contains(DecorParameter.SITTABLE);
    }

    @Override
    public boolean isWrenchable() {
        return this.parameterSet.contains(DecorParameter.WRENCHABLE);
    }

    @Override
    public boolean isLightable() {
        return this.parameterSet.contains(DecorParameter.LIGHTABLE);
    }

    @Override
    public boolean isTyped() {
        return this.parameterSet.contains(DecorParameter.TYPED);
    }

    @Override
    public boolean isLightTyped() {
        return this.parameterSet.contains(DecorParameter.LIGHT_TYPED);
    }

    @Override
    public boolean isFaceTyped() {
        return this.parameterSet.contains(DecorParameter.FACE_TYPED);
    }

    @Override
    public boolean isAnyTyped() {
        return this.isWrenchable()
                || this.isTyped()
                || this.isLightTyped()
                || this.isFaceTyped();
    }

    @Override
    public boolean isDropsType() {
        return this.dropsType;
    }

    @Override
    public void registerRecipes() {
        if (this.recipes.isEmpty()) return;

        final MSDecor plugin = MSDecor.getInstance();
        final Server server = plugin.getServer();

        plugin.runTask(() -> {
            for (final var entry : this.recipes) {
                final Recipe recipe = entry.getKey();

                server.addRecipe(recipe);

                if (entry.getValue()) {
                    getGlobalCache().customDecorRecipes.add(recipe);
                }
            }
        });
    }

    @Override
    public final void unregisterRecipes() {
        if (this.recipes.isEmpty()) return;
        for (final var entry : this.recipes) {
            final Recipe recipe = entry.getKey();

            if (recipe instanceof final Keyed keyed) {
                Bukkit.removeRecipe(keyed.getKey());

                if (entry.getValue()) {
                    getGlobalCache().customDecorRecipes.remove(recipe);
                }
            }
        }
    }

    @Override
    public void place(
            final @NotNull MSPosition blockLocation,
            final @NotNull Player player,
            final @NotNull BlockFace blockFace,
            final @Nullable EquipmentSlot hand,
            final @Nullable Component customName
    ) {
        if (!this.getFacing().hasFace(blockFace)) return;

        final ServerLevel serverLevel = ((CraftWorld) player.getWorld()).getHandle();
        final MSBoundingBox msbb = this.hitBox.getBoundingBox(blockLocation, player.getYaw());
        final var blockStates = new ArrayList<org.bukkit.block.BlockState>();
        final BlockPos[] blocksToReplace = msbb.getBlockPositions();

        for (final var blockPos : blocksToReplace) {
            final BlockState blockState = serverLevel.getBlockState(blockPos);

            if (!BlockUtils.isReplaceable(blockState.getBlock())) return;

            blockStates.add(CraftBlockStates.getUnplacedBlockState(serverLevel, blockPos, blockState));
        }

        if (
                this.hitBox.getType().isSolid()
                && msbb.max(msbb.max().offset(1.0d)).hasNMSEntity(
                        serverLevel,
                        entity -> !BlockUtils.isIgnorableEntity(entity.getType())
                )
        ) return;

        final ItemStack itemInHand = hand != null
                ? player.getInventory().getItem(hand)
                : this.itemStack;

        if (customName != null) {
            final ItemMeta itemMeta = itemInHand.getItemMeta();

            itemMeta.displayName(customName);
            itemInHand.setItemMeta(itemMeta);
        }

        final float rotation = player.getYaw();
        final CustomDecorPlaceEvent event = new CustomDecorPlaceEvent(
                this.placeInWorld(
                        player.getName(),
                        this.summonItem(blockLocation.yaw(rotation), blockFace, itemInHand),
                        msbb,
                        blocksToReplace,
                        rotation
                ),
                player,
                hand == null ? EquipmentSlot.HAND : hand,
                blockStates
        );
        player.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            event.getCustomDecor().destroy(player, false);

            for (final var replacedBlock : event.getReplacedBlocks()) {
                replacedBlock.setType(replacedBlock.getType());
                replacedBlock.setBlockData(replacedBlock.getBlockData());
            }

            return;
        }

        this.getSoundGroup().playPlaceSound(blockLocation.center());

        if (hand != null) {
            itemInHand.setAmount(
                    player.getGameMode() == GameMode.SURVIVAL
                            ? itemInHand.getAmount() - 1
                            : itemInHand.getAmount()
            );
            player.swingHand(hand);
        }
    }

    @Override
    public void doClickAction(final @NotNull CustomDecorClickEvent event) {
        if (this.clickAction.isSet()) {
            this.clickAction.execute(event);
        }
    }

    @Override
    public void doPlaceAction(final @NotNull CustomDecorPlaceEvent event) {
        if (this.placeAction.isSet()) {
            this.placeAction.execute(event);
        }
    }

    @Override
    public void doBreakAction(final @NotNull CustomDecorBreakEvent event) {
        if (this.breakAction.isSet()) {
            this.breakAction.execute(event);
        }
    }

    private @NotNull CustomDecor placeInWorld(
            final @NotNull String placerName,
            final @NotNull ItemDisplay itemDisplay,
            final @NotNull MSBoundingBox boundingBox,
            final BlockPos @NotNull [] replacePositions,
            final float rotation
    ) {
        final Interaction[] interactions = this.fillInteractions(
                itemDisplay,
                boundingBox,
                this.hitBox.getVectorInBlock(rotation)
        );
        final DecorHitBox.Type type = this.hitBox.getType();

        if (!type.isNone()) {
            final var blocks = fillBlocks(
                    placerName,
                    ((CraftWorld) itemDisplay.getWorld()).getHandle(),
                    replacePositions,
                    type.getNMSMaterial().defaultBlockState()
            );

            if (
                    this.isLightable()
                    || this.isLightTyped()
            ) {
                final int lightLevel =
                        this.isLightTyped()
                        ? this.getLightLevelOf(itemDisplay.getItemStack())
                        : this.lightLevels[0];

                for (final var currentBlock : blocks) {
                    if (currentBlock.getBlockData() instanceof final Light light) {
                        light.setLevel(lightLevel);
                        currentBlock.setBlockData(light);
                    }
                }
            }
        }

        return new CustomDecor(this, itemDisplay, interactions, boundingBox);
    }

    private @NotNull ItemDisplay summonItem(
            final @NotNull MSPosition position,
            final @NotNull BlockFace blockFace,
            final @NotNull ItemStack item
    ) {
        final World world = position.world();

        if (world == null) {
            throw new IllegalArgumentException("The world of the position cannot be null!");
        }

        final ItemStack itemStack = item.clone();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final CustomDecorData.Type<D> type;

        itemStack.setAmount(1);

        if (this.isLightTyped()) {
            type = this.lightLevelTypeMap.get(this.lightLevels[0]);
        } else if (this.isFaceTyped()) {
            type = this.faceTypeMap.get(Facing.fromBlockFace(blockFace));
        } else if (this.isWrenchable()) {
            type = this.getTypeOf(itemStack);
        } else {
            type = null;
        }

        return world.spawn(
                position.center()
                .offset(
                        this.hitBox.getModelOffsetX(),
                        this.hitBox.getModelOffsetY(),
                        this.hitBox.getModelOffsetZ()
                )
                .yaw(
                        this.hitBox.getX() > 1.0d || this.hitBox.getZ() > 1.0d
                        || this.hitBox.getX() < -1.0d || this.hitBox.getZ() < -1.0d
                                ? LocationUtils.to90(position.yaw()) + 180.0f
                                : LocationUtils.to45(position.yaw()) + 180.0f
                )
                .toLocation(),
                ItemDisplay.class,
                itemDisplay -> {
                    itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);

                    if (
                            type == null
                            || CustomDecorType.matchesTypedKey(
                                    itemMeta.getPersistentDataContainer().get(
                                            CustomDecorType.TYPE_NAMESPACED_KEY,
                                            PersistentDataType.STRING
                                    )
                            )
                    ) {
                        itemDisplay.setItemStack(itemStack);
                    } else {
                        final ItemStack typeItem = type.getItem();
                        final ItemMeta typeMeta = typeItem.getItemMeta();

                        typeMeta.displayName(itemMeta.displayName());
                        typeItem.setItemMeta(typeMeta);

                        itemDisplay.setItemStack(typeItem);
                    }

                    itemDisplay.setDisplayHeight(1.0f);
                    itemDisplay.setDisplayWidth(1.0f);
                });
    }

    private Interaction @NotNull [] fillInteractions(
            final @NotNull ItemDisplay itemDisplay,
            final @NotNull MSBoundingBox boundingBox,
            final @NotNull MSVector offset
    ) {
        final World world = itemDisplay.getWorld();
        final BlockPos[] spawnPoses = boundingBox.getBlockPositions(
                0,
                this.hitBox.getFacing() == Facing.CEILING
                        ? (int) (this.hitBox.getY() - 1)
                        : 0,
                0,
                0,
                this.hitBox.getFacing() == Facing.CEILING
                        ? 0
                        : (int) (-this.hitBox.getY() + 1),
                0
        );
        final int length = spawnPoses.length;
        final Interaction[] interactions = new Interaction[length];

        final float width = this.hitBox.getInteractionWidth();
        final float height = this.hitBox.getInteractionHeight();

        final double offsetX = offset.x();
        final double offsetY = offset.y();
        final double offsetZ = offset.z();

        for (int i = 0; i < length; ++i) {
            final BlockPos blockPos = spawnPoses[i];
            interactions[i] = world.spawn(
                    new Location(
                            null,
                            blockPos.getX() + offsetX,
                            blockPos.getY() + offsetY,
                            blockPos.getZ() + offsetZ
                    ),
                    Interaction.class,
                    interaction -> {
                        interaction.setInteractionHeight(height);
                        interaction.setInteractionWidth(width);
                        interaction.setResponsive(false);
                    }
            );
        }

        this.processInteractions(itemDisplay, interactions, boundingBox);

        return interactions;
    }

    private void processInteractions(
            final @NotNull ItemDisplay display,
            final Interaction @NotNull [] interactions,
            final @NotNull MSBoundingBox msbb
    ) {
        final Interaction firstInteraction = interactions[0];
        final String firstUUID = firstInteraction.getUniqueId().toString();
        final PersistentDataContainer firstContainer = firstInteraction.getPersistentDataContainer();
        final var uuids = new ArrayList<String>();

        for (int i = 1; i < interactions.length; ++i) {
            final Interaction interaction = interactions[i];

            uuids.add(interaction.getUniqueId().toString());
            interaction.getPersistentDataContainer().set(
                    DecorHitBox.HITBOX_CHILD_NAMESPACED_KEY,
                    PersistentDataType.STRING,
                    firstUUID
            );
        }

        firstContainer.set(
                CustomDecorType.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.namespacedKey.getKey()
        );

        firstContainer.set(
                DecorHitBox.HITBOX_DISPLAY_NAMESPACED_KEY,
                PersistentDataType.STRING,
                display.getUniqueId().toString()
        );

        firstContainer.set(
                DecorHitBox.HITBOX_INTERACTIONS_NAMESPACED_KEY,
                PersistentDataType.STRING,
                String.join(",", uuids)
        );

        firstContainer.set(
                DecorHitBox.HITBOX_BOUNDING_BOX_NAMESPACED_KEY,
                PersistentDataType.STRING,
                String.join(
                        ",",
                        String.valueOf(msbb.minX()),
                        String.valueOf(msbb.minY()),
                        String.valueOf(msbb.minZ()),
                        String.valueOf(msbb.maxX()),
                        String.valueOf(msbb.maxY()),
                        String.valueOf(msbb.maxZ())
                )
        );
    }

    static @NotNull List<Block> fillBlocks(
            final @NotNull String changerName,
            final @NotNull ServerLevel serverLevel,
            final BlockPos @NotNull [] replacePositions,
            final @NotNull BlockState fillBlockState
    ) {
        final var blockList = new ArrayList<Block>();
        final var list = new ArrayList<BlockPos>();
        final Material fillMaterial = fillBlockState.getBukkitMaterial();
        final BlockData fillBlockData = fillBlockState.createCraftBlockData();

        for (final var blockPos : replacePositions) {
            BlockState blockState = net.minecraft.world.level.block.Block.updateFromNeighbourShapes(fillBlockState, serverLevel, blockPos);
            final Location location = LocationUtils.nmsToBukkit(blockPos, serverLevel);

            if (blockState.isAir()) {
                blockState = fillBlockState;
            } else {
                MSDecor.getCoreProtectAPI().logRemoval(
                        changerName,
                        location,
                        blockState.getBukkitMaterial(),
                        blockState.createCraftBlockData()
                );
            }

            serverLevel.setBlock(blockPos, blockState, 2);
            list.add(blockPos.immutable());
            blockList.add(location.getBlock());

            if (!fillMaterial.isAir()) {
                MSDecor.getCoreProtectAPI().logPlacement(
                        changerName,
                        location,
                        fillMaterial,
                        fillBlockData
                );
            }
        }

        for (final var blockPos : list) {
            serverLevel.blockUpdated(
                    blockPos,
                    serverLevel.getBlockState(blockPos).getBlock()
            );
        }

        return blockList;
    }

    private static @NotNull ItemStack setTypeKey(
            final @NotNull ItemStack itemStack,
            final @NotNull String typeKey
    ) {
        final ItemMeta meta = itemStack.getItemMeta();
        final PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(CustomDecorType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING, typeKey);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public final class Builder {
        private NamespacedKey namespacedKey;
        private DecorHitBox hitBox;
        private Facing facing;
        private ItemStack itemStack;
        private SoundGroup soundGroup;
        private List<Map.Entry<RecipeBuilder<?>, Boolean>> recipeBuilderList;
        private EnumSet<DecorParameter> parameterSet;
        private double sitHeight;
        private CustomDecorData.Type<D>[] types;
        private EnumMap<Facing, CustomDecorData.Type<D>> faceTypeMap;
        private int[] lightLevels;
        private Map<Integer, CustomDecorData.Type<D>> lightLevelTypeMap;
        private DecorClickAction clickAction;
        private DecorPlaceAction placeAction;
        private DecorBreakAction breakAction;
        private boolean dropsType;

        public Builder() {
            this.sitHeight = Double.NaN;
            this.clickAction = DecorClickAction.NONE;
            this.placeAction = DecorPlaceAction.NONE;
            this.breakAction = DecorBreakAction.NONE;
        }

        public @NotNull CustomDecorDataImpl<D> build() throws IllegalArgumentException {
            return new CustomDecorDataImpl<>() {

                @Override
                protected @NotNull Builder builder() {
                    return Builder.this;
                }
            };
        }

        public @NotNull Builder preBuild() throws IllegalArgumentException {
            if (this.namespacedKey == null) {
                throw new IllegalArgumentException("Key is not set!");
            }

            if (this.hitBox == null) {
                throw new IllegalArgumentException("Hit box is not set!");
            }

            if (this.facing == null) {
                throw new IllegalArgumentException("Facing is not set!");
            }

            if (this.soundGroup == null) {
                throw new IllegalArgumentException("Sound group is not set!");
            }

            if (this.itemStack == null) {
                throw new IllegalArgumentException("Item stack is not set!");
            }

            if (this.parameterSet != null) {
                if (
                        this.isPaintable()
                        || this.isWrenchable()
                ) {
                    final ItemMeta meta = this.itemStack.getItemMeta();
                    final var currentLore = meta.lore();
                    final var newLore = new ArrayList<Component>();

                    if (this.isPaintable()) {
                        newLore.add(Badges.PAINTABLE_LORE);
                    }

                    if (this.isWrenchable()) {
                        newLore.add(Badges.WRENCHABLE_LORE);
                    }

                    if (currentLore != null) {
                        newLore.add(Component.empty());
                        newLore.addAll(currentLore);
                    }

                    meta.lore(newLore);
                    this.itemStack.setItemMeta(meta);
                }

                if (
                        this.isSittable()
                        && this.sitHeight != this.sitHeight
                ) {
                    throw new IllegalArgumentException("Sit height is not set, but sittable parameter is set!");
                }

                if (
                        this.isWrenchable()
                        && this.types == null
                ) {
                    throw new IllegalArgumentException("Types are not set, but wrenchable parameter is set!");
                }

                if (
                        this.isTyped()
                        && this.types == null
                ) {
                    throw new IllegalArgumentException("Types are not set, but typed parameter is set!");
                }

                if (
                        this.isLightable()
                        && this.lightLevels == null
                ) {
                    throw new IllegalArgumentException("Light levels are not set, but lightable parameter is set!");
                }

                if (
                        (
                                this.isLightable()
                                || this.isLightTyped()
                        )
                        && !this.hitBox.getType().isLight()

                ) {
                    throw new IllegalArgumentException("Lightable or light typed parameter is set, but hit box type is not light!");
                }

                if (this.isLightTyped()) {
                    if (this.lightLevels == null) {
                        throw new IllegalArgumentException("Light levels are not set, but light typed parameter is set!");
                    }

                    if (
                            this.lightLevelTypeMap == null
                            || this.lightLevelTypeMap.isEmpty()
                    ) {
                        throw new IllegalArgumentException("Light level type map is not set, but light typed parameter is set!");
                    }
                }

                if (
                        this.isFaceTyped()
                        && (
                                this.faceTypeMap == null
                                || this.faceTypeMap.isEmpty()
                        )
                ) {
                    throw new IllegalArgumentException("Face type map is not set, but face typed parameter is set!");
                }
            } else {
                this.parameterSet = EnumSet.noneOf(DecorParameter.class);
            }

            if (!this.clickAction.isSet()) {
                if (
                        this.isWrenchable()
                        && this.isSittable()
                ) {
                    this.clickAction = DecorParameter.wrenchableSittableAction();
                } else if (
                        this.isWrenchable()
                        && this.isLightable()
                ) {
                    this.clickAction = DecorParameter.wrenchableLightableAction();
                } else {
                    for (final var parameter : this.parameterSet) {
                        if (parameter.getClickAction().isSet()) {
                            this.clickAction = parameter.getClickAction();
                            break;
                        }
                    }
                }
            }

            return this;
        }

        public NamespacedKey key() {
            return this.namespacedKey;
        }

        public @NotNull Builder key(final @NotNull String key) throws IllegalArgumentException {
            ChatUtils.validateKey(key);

            this.namespacedKey = new NamespacedKey(CustomDecorType.NAMESPACE, key);
            return this;
        }

        public DecorHitBox hitBox() {
            return this.hitBox;
        }

        public @NotNull Builder hitBox(final @NotNull DecorHitBox hitBox) {
            this.hitBox = hitBox;
            return this;
        }

        public Facing facing() {
            return this.facing;
        }

        public @NotNull Builder facing(final @NotNull Facing facing) {
            this.facing = facing;
            return this;
        }

        public SoundGroup soundGroup() {
            return this.soundGroup;
        }

        public @NotNull Builder soundGroup(final @NotNull SoundGroup soundGroup) {
            this.soundGroup = soundGroup;
            return this;
        }

        public ItemStack itemStack() {
            return this.itemStack;
        }

        public @NotNull Builder itemStack(final @NotNull ItemStack itemStack) throws IllegalStateException {
            if (this.namespacedKey == null) {
                throw new IllegalStateException("Key is not set! Set key before setting item stack!");
            }

            this.itemStack = setTypeKey(itemStack, this.namespacedKey.getKey());
            return this;
        }

        public List<Map.Entry<RecipeBuilder<?>, Boolean>> recipeBuilderList() {
            return this.recipeBuilderList;
        }

        @SafeVarargs
        public final @NotNull Builder recipes(
                final @NotNull Function<Builder, Map.Entry<RecipeBuilder<?>, Boolean>> first,
                final Function<Builder, Map.Entry<RecipeBuilder<?>, Boolean>> @NotNull ... rest
        ) {
            this.recipeBuilderList = new ArrayList<>(rest.length + 1);

            this.recipeBuilderList.add(first.apply(this));

            for (final var entry : rest) {
                this.recipeBuilderList.add(entry.apply(this));
            }

            return this;
        }

        @SafeVarargs
        public final @NotNull Builder recipes(
                final @NotNull Map.Entry<RecipeBuilder<?>, Boolean> first,
                final Map.Entry<RecipeBuilder<?>, Boolean> @NotNull ... rest
        ) {
            final int restLength = rest.length;
            this.recipeBuilderList = new ArrayList<>(restLength + 1);

            this.recipeBuilderList.add(first);

            if (restLength != 0) {
                this.recipeBuilderList.addAll(Arrays.asList(rest));
            }

            return this;
        }

        public EnumSet<DecorParameter> parameterSet() {
            return this.parameterSet;
        }

        public @NotNull Builder parameters(
                final @NotNull DecorParameter first,
                final DecorParameter @NotNull ... rest
        ) throws IllegalArgumentException {
            final var parameters = EnumSet.of(first, rest);

            if (
                    parameters.contains(DecorParameter.TYPED)
                    && parameters.contains(DecorParameter.WRENCHABLE)
            ) {
                throw new IllegalArgumentException("Typed and wrenchable parameters cannot be set together!");
            }

            if (
                    parameters.contains(DecorParameter.TYPED)
                    && parameters.contains(DecorParameter.LIGHT_TYPED)
            ) {
                throw new IllegalArgumentException("Typed and sittable parameters cannot be set together!");
            }

            if (
                    parameters.contains(DecorParameter.TYPED)
                    && parameters.contains(DecorParameter.FACE_TYPED)
            ) {
                throw new IllegalArgumentException("Typed and face typed parameters cannot be set together!");
            }

            if (
                    parameters.contains(DecorParameter.WRENCHABLE)
                    && parameters.contains(DecorParameter.LIGHT_TYPED)
            ) {
                throw new IllegalArgumentException("Wrenchable and light typed parameters cannot be set together!");
            }

            if (
                    parameters.contains(DecorParameter.WRENCHABLE)
                    && parameters.contains(DecorParameter.FACE_TYPED)
            ) {
                throw new IllegalArgumentException("Wrenchable and face typed parameters cannot be set together!");
            }

            if (
                    parameters.contains(DecorParameter.LIGHT_TYPED)
                    && parameters.contains(DecorParameter.FACE_TYPED)
            ) {
                throw new IllegalArgumentException("Light typed and face typed parameters cannot be set together!");
            }

            if (
                    parameters.contains(DecorParameter.LIGHT_TYPED)
                    && parameters.contains(DecorParameter.LIGHTABLE)
            ) {
                throw new IllegalArgumentException("Light typed and lightable parameters cannot be set together!");
            }

            if (
                    parameters.contains(DecorParameter.FACE_TYPED)
                    && this.facing != Facing.ALL
            ) {
                throw new IllegalArgumentException("Face typed parameter cannot be set together with a non-all facing!");
            }

            this.parameterSet = parameters;
            return this;
        }

        public double sitHeight() {
            return this.sitHeight;
        }

        public @NotNull Builder sitHeight(final @Range(from = -9, to = 9) double sitHeight) throws IllegalStateException, IllegalArgumentException {
            this.validateParam(
                    DecorParameter.SITTABLE,
                    "Set sittable parameter before setting sit height!"
            );

            if (sitHeight < -9 || sitHeight > 9) {
                throw new IllegalArgumentException("Sit height '" + sitHeight + "' is not in range [-9, 9]!");
            }

            this.sitHeight = sitHeight;
            return this;
        }

        public CustomDecorData.Type<D>[] types() {
            return this.types;
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        public final @NotNull Builder types(
                final @NotNull Function<Builder, CustomDecorData.Type<D>> first,
                final Function<Builder, CustomDecorData.Type<D>> @NotNull ... rest
        ) {
            validateAnyOfParams(
                    "Set wrenchable or typed parameter before setting types!",
                    DecorParameter.WRENCHABLE, DecorParameter.TYPED
            );

            final var firstType = first.apply(this);
            final CustomDecorData.Type<D>[] restTypes = (CustomDecorData.Type<D>[]) new CustomDecorData.Type<?>[rest.length];

            for (int i = 0; i < rest.length; i++) {
                restTypes[i] = rest[i].apply(this);
            }

            return this.types(firstType,  restTypes);
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        public final @NotNull Builder types(
                final @NotNull CustomDecorData.Type<D> first,
                final CustomDecorData.Type<D> @NotNull ... rest
        ) throws IllegalStateException {
            validateAnyOfParams(
                    "Set wrenchable or typed parameter before setting types!",
                    DecorParameter.WRENCHABLE, DecorParameter.TYPED
            );

            if (this.itemStack == null) {
                throw new IllegalStateException("Item stack is not set! Set item stack before setting wrench types!");
            }

            final int restLength = rest.length;
            this.types = (CustomDecorData.Type<D>[]) new CustomDecorData.Type<?>[restLength + 2];

            if (restLength != 0) {
                System.arraycopy(rest, 0, this.types, 2, restLength);
            }

            this.types[0] =
                    new Type(
                            this,
                            "default",
                            this.itemStack
                    );
            this.types[1] = first;

            return this;
        }

        public @NotNull EnumMap<Facing, CustomDecorData.Type<D>> faceTypeMap() {
            return this.faceTypeMap;
        }

        public @NotNull Builder faceTypes(
                final @NotNull Function<Builder, CustomDecorData.Type<D>> floorType,
                final @NotNull Function<Builder, CustomDecorData.Type<D>> ceilingType,
                final @NotNull Function<Builder, CustomDecorData.Type<D>> wallType
        ) {
            this.validateParam(
                    DecorParameter.FACE_TYPED,
                    "Set face typed parameter before setting face type map!"
            );

            return this.faceTypes(
                    floorType.apply(this),
                    ceilingType.apply(this),
                    wallType.apply(this)
            );
        }

        public @NotNull Builder faceTypes(
                final @NotNull CustomDecorData.Type<D> floorType,
                final @NotNull CustomDecorData.Type<D> ceilingType,
                final @NotNull CustomDecorData.Type<D> wallType
        ) throws IllegalStateException {
            this.validateParam(
                    DecorParameter.FACE_TYPED,
                    "Set face typed parameter before setting face type map!"
            );

            this.faceTypeMap = new EnumMap<>(Facing.class);
            this.faceTypeMap.put(Facing.FLOOR, floorType);
            this.faceTypeMap.put(Facing.CEILING, ceilingType);
            this.faceTypeMap.put(Facing.WALL, wallType);

            return this;
        }

        public int[] lightLevels() {
            return this.lightLevels;
        }

        public @NotNull Builder lightLevels(
                final int first,
                final int @NotNull ... rest
        ) throws IllegalStateException, IllegalArgumentException {
            this.validateAnyOfParams(
                    "Set lightable or light typed parameter before setting light levels!",
                    DecorParameter.LIGHTABLE, DecorParameter.LIGHT_TYPED
            );

            final int length = rest.length + 1;
            this.lightLevels = new int[rest.length + 1];

            System.arraycopy(rest, 0, this.lightLevels, 1, rest.length);
            this.lightLevels[0] = first;

            for (int i = 0; i < length; i++) {
                final int level = this.lightLevels[i];

                if (level < 0 || level > 15) {
                    throw new IllegalArgumentException("Light level '" + level + "' is not in range [0, 15]!");
                }

                for (int j = i + 1; j < length; j++) {
                    if (level == this.lightLevels[j]) {
                        throw new IllegalArgumentException("Light level '" + level + "' is duplicated! Light levels must be unique!");
                    }
                }
            }

            return this;
        }

        public Map<Integer, CustomDecorData.Type<D>> lightLevelTypeMap() {
            return this.lightLevelTypeMap;
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        public final @NotNull Builder lightLevelTypes(
                final @NotNull Function<Builder, Map.Entry<Integer, CustomDecorData.Type<D>>> first,
                final Function<Builder, Map.Entry<Integer, CustomDecorData.Type<D>>> @NotNull ... rest
        ) {
            this.validateParam(
                    DecorParameter.LIGHT_TYPED,
                    "Set light typed parameter before setting light level type map!"
            );

            final var firstType = first.apply(this);
            final var restTypes = (Map.Entry<Integer, CustomDecorData.Type<D>>[]) new Map.Entry<?, ?>[rest.length];

            for (int i = 0; i < rest.length; i++) {
                restTypes[i] = rest[i].apply(this);
            }

            return this.lightLevelTypes(firstType, restTypes);
        }

        @SafeVarargs
        public final @NotNull Builder lightLevelTypes(
                final @NotNull Map.Entry<Integer, CustomDecorData.Type<D>> first,
                final Map.Entry<Integer, CustomDecorData.Type<D>> @NotNull ... rest
        ) throws IllegalStateException {
            this.validateParam(
                    DecorParameter.LIGHT_TYPED,
                    "Set light typed parameter before setting light level type map!"
            );

            this.lightLevelTypeMap = new HashMap<>();

            this.putLightLevelType(first.getKey(), first.getValue());

            for (final var entry : rest) {
                this.putLightLevelType(entry.getKey(), entry.getValue());
            }

            if (this.lightLevelTypeMap.size() != this.lightLevels.length) {
                throw new IllegalStateException("Light level type map size is not equal to light levels size!");
            }

            return this;
        }

        public DecorClickAction clickAction() {
            return this.clickAction;
        }

        public @NotNull Builder clickAction(final @NotNull DecorClickAction rightClickAction) {
            this.clickAction = rightClickAction;
            return this;
        }

        public DecorPlaceAction placeAction() {
            return this.placeAction;
        }

        public @NotNull Builder placeAction(final @NotNull DecorPlaceAction placeAction) {
            this.placeAction = placeAction;
            return this;
        }

        public DecorBreakAction breakAction() {
            return this.breakAction;
        }

        public @NotNull Builder breakAction(final @NotNull DecorBreakAction breakAction) {
            this.breakAction = breakAction;
            return this;
        }

        public boolean dropsType() {
            return this.dropsType;
        }

        public @NotNull Builder dropsType(final boolean dropsType) throws IllegalStateException {
            this.validateAnyOfParams(
                    "Drop type can be set only if one of these parameters is set!",
                    DecorParameter.WRENCHABLE,
                    DecorParameter.LIGHT_TYPED,
                    DecorParameter.FACE_TYPED
            );

            this.dropsType = dropsType;
            return this;
        }

        public boolean isPaintable() {
            return this.parameterSet != null
                    && this.parameterSet.contains(DecorParameter.PAINTABLE);
        }

        public boolean isSittable() {
            return this.parameterSet != null
                    && this.parameterSet.contains(DecorParameter.SITTABLE);
        }

        public boolean isWrenchable() {
            return this.parameterSet != null
                    && this.parameterSet.contains(DecorParameter.WRENCHABLE);
        }

        public boolean isLightable() {
            return this.parameterSet != null
                    && this.parameterSet.contains(DecorParameter.LIGHTABLE);
        }

        public boolean isTyped() {
            return this.parameterSet != null
                    && this.parameterSet.contains(DecorParameter.TYPED);
        }

        public boolean isLightTyped() {
            return this.parameterSet != null
                    && this.parameterSet.contains(DecorParameter.LIGHT_TYPED);
        }

        public boolean isFaceTyped() {
            return this.parameterSet != null
                    && this.parameterSet.contains(DecorParameter.FACE_TYPED);
        }

        public boolean isAnyTyped() {
            return this.parameterSet != null
                    && (
                            this.isWrenchable()
                            || this.isTyped()
                            || this.isLightTyped()
                            || this.isFaceTyped()
                    );
        }

        private void putLightLevelType(
                final int level,
                final @NotNull CustomDecorData.Type<D> type
        ) {
            if (this.lightLevelTypeMap == null) {
                throw new IllegalStateException("Light level type map is not set! Set light level type map before putting light level type!");
            }

            if (this.lightLevels == null) {
                throw new IllegalStateException("Light levels are not set! Set light levels before putting light level type!");
            }

            if (this.lightLevelTypeMap.containsKey(level)) {
                throw new IllegalArgumentException("Light level '" + level + "' is duplicated! Light levels must be unique!");
            }


            for (final var lightLevel : this.lightLevels) {
                if (level == lightLevel) {
                    this.lightLevelTypeMap.put(level, type);
                    return;
                }
            }

            throw new IllegalArgumentException("Light level '" + level + "' not found in light levels!");
        }

        private void validateParam(
                final @NotNull DecorParameter param,
                final @NotNull String message
        ) throws IllegalStateException {
            if (this.parameterSet == null) {
                throw new IllegalStateException("Parameters are not set! First set parameters!");
            }

            if (!this.parameterSet.contains(param)) {
                throw new IllegalStateException("Parameter '" + param + "' is not set! " + message);
            }
        }

        private void validateParams(
                final @NotNull String message,
                final @NotNull DecorParameter first,
                final DecorParameter @NotNull ... rest
        ) throws IllegalStateException {
            if (this.parameterSet == null) {
                throw new IllegalStateException("Parameters are not set! First set parameters!");
            }

            if (!this.parameterSet.contains(first)) {
                throw new IllegalStateException("Parameter '" + first + "' is not set! " + message);
            }

            for (final var param : rest) {
                if (!this.parameterSet.contains(param)) {
                    throw new IllegalStateException("Parameter '" + param + "' is not set! " + message);
                }
            }
        }

        private void validateAnyOfParams(
                final @NotNull String message,
                final @NotNull DecorParameter first,
                final DecorParameter @NotNull ... rest
        ) throws IllegalStateException {
            if (this.parameterSet == null) {
                throw new IllegalStateException("Parameters are not set! First set parameters!");
            }

            if (this.parameterSet.contains(first)) return;

            for (final var param : rest) {
                if (this.parameterSet.contains(param)) return;
            }

            final StringBuilder builder = new StringBuilder();

            builder.append(first);

            for (final var param : rest) {
                builder.append(", ").append(param);
            }

            throw new IllegalStateException("Any of parameters : " + builder + " is not set! " + message);
        }
    }

    protected final class Type implements CustomDecorData.Type<D> {
        private final NamespacedKey namespacedKey;
        private final ItemStack itemStack;

        public Type(
                final @NotNull Builder builder,
                final @NotNull String key,
                final @NotNull ItemStack itemStack
        ) {
            ChatUtils.validateKey(key);

            final String typedKey = builder.namespacedKey.getKey() + ".type." + key;
            this.namespacedKey = new NamespacedKey(CustomDecorType.NAMESPACE, typedKey);
            this.itemStack = setTypeKey(itemStack, typedKey);
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return this.namespacedKey;
        }

        @Override
        public @NotNull ItemStack getItem() {
            return this.itemStack.clone();
        }

        @Override
        @Contract("null -> false")
        public boolean equals(final @Nullable Object type) {
            return type == this
                    || (
                            type != null
                            && type.getClass() == this.getClass()
                            && ((CustomDecorData.Type<?>) type).getKey().equals(this.getKey())
                    );
        }

        @Override
        public @NotNull String toString() {
            return "Type{" +
                    "namespacedKey=" + this.namespacedKey +
                    ", itemStack=" + this.itemStack +
                    '}';
        }

        @Override
        @SuppressWarnings("unchecked")
        public @NotNull D buildData() {
            return (D) CustomDecorDataImpl.this.builder()
                    .key(this.namespacedKey.getKey())
                    .itemStack(this.itemStack)
                    .preBuild()
                    .build();
        }
    }
}
