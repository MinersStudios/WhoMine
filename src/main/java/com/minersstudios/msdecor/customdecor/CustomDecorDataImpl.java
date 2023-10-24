package com.minersstudios.msdecor.customdecor;

import com.minersstudios.mscore.util.BlockUtils;
import com.minersstudios.mscore.util.LocationUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.events.CustomDecorBreakEvent;
import com.minersstudios.msdecor.events.CustomDecorClickEvent;
import com.minersstudios.msdecor.events.CustomDecorPlaceEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

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
    protected final CustomDecorData.Type<D>[] wrenchTypes;
    protected final int[] lightLevels;
    protected final EnumMap<Facing, CustomDecorData.Type<D>> faceTypeMap;
    protected final Map<Integer, CustomDecorData.Type<D>> lightLevelTypeMap;
    protected final Consumer<CustomDecorClickEvent> clickAction;
    protected final Consumer<CustomDecorPlaceEvent> placeAction;
    protected final Consumer<CustomDecorBreakEvent> breakAction;
    protected final boolean dropsType;

    protected CustomDecorDataImpl() throws IllegalArgumentException {
        final Builder builder = this.builder();

        builder.preBuild();

        this.namespacedKey = builder.namespacedKey;
        this.hitBox = builder.hitBox;
        this.facing = builder.facing;
        this.soundGroup = builder.soundGroup;
        this.itemStack = builder.itemStack;
        this.recipes =
                builder.recipeList == null
                ? Collections.emptyList()
                : builder.recipeList;
        this.parameterSet =
                builder.parameterSet == null
                ? EnumSet.noneOf(DecorParameter.class)
                : builder.parameterSet;
        this.sitHeight = builder.sitHeight;
        this.wrenchTypes = builder.wrenchTypes;
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
    public CustomDecorData.Type<D> @NotNull [] wrenchTypes() throws UnsupportedOperationException {
        if (!this.isWrenchable()) {
            throw new UnsupportedOperationException("This custom decor is not wrenchable!");
        }

        return this.wrenchTypes;
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getWrenchTypeOf(final @Nullable Interaction interaction) throws UnsupportedOperationException {
        if (!this.isWrenchable()) {
            throw new UnsupportedOperationException("This custom decor is not wrenchable!");
        }

        return interaction == null
                ? null
                : this.getWrenchTypeOf(
                        CustomDecor.fromInteraction(interaction).map(
                                customDecor -> customDecor.getDisplay().getItemStack()
                        ).orElse(null)
                );
    }
    
    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getWrenchTypeOf(final @Nullable ItemStack itemStack) throws UnsupportedOperationException {
        if (!this.isWrenchable()) {
            throw new UnsupportedOperationException("This custom decor is not wrenchable!");
        }

        if (itemStack == null) return null;

        final String key = itemStack.getItemMeta().getPersistentDataContainer().get(
                CustomDecorType.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING
        );

        if (key == null) return null;
        if (!CustomDecorType.matchesTypedKey(key)) return this.wrenchTypes[0];
        
        for (final var type : this.wrenchTypes) {
            if (key.equals(type.getKey().getKey())) {
                return type;
            }
        }
        
        return null;
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getNextWrenchType(final @Nullable Interaction interaction) throws UnsupportedOperationException {
        return this.getNextWrenchType(this.getWrenchTypeOf(interaction));
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getNextWrenchType(final @Nullable ItemStack itemStack) throws UnsupportedOperationException {
        return this.getNextWrenchType(this.getWrenchTypeOf(itemStack));
    }

    @Override
    @Contract("null -> null")
    public @Nullable CustomDecorData.Type<D> getNextWrenchType(final @Nullable CustomDecorData.Type<? extends CustomDecorData<?>> type) throws UnsupportedOperationException {
        if (!this.isWrenchable()) {
            throw new UnsupportedOperationException("This custom decor is not wrenchable!");
        }

        if (
                type == null
                || type.getDecorType() != this.wrenchTypes[0].getDecorType()
        ) return null;

        final int length = this.wrenchTypes.length;

        for (int i = 0; i < length; ++i) {
            if (this.wrenchTypes[i].equals(type)) {
                return this.wrenchTypes[Math.floorMod(i + 1, length)];
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

        return this.lightLevelTypeMap.getOrDefault(lightLevel, null);
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
                : 0;
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
    public boolean isLightTyped() {
        return this.parameterSet.contains(DecorParameter.LIGHT_TYPED);
    }

    @Override
    public boolean isFaceTyped() {
        return this.parameterSet.contains(DecorParameter.FACE_TYPED);
    }

    @Override
    public boolean isTyped() {
        return this.isWrenchable() || this.isLightTyped() || this.isFaceTyped();
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
            final @NotNull Block replaceableBlock,
            final @NotNull Player player,
            final @NotNull BlockFace blockFace,
            final @Nullable EquipmentSlot hand,
            final @Nullable Component customName
    ) {
        if (!this.getFacing().hasFace(blockFace)) return;

        final ServerLevel serverLevel = ((CraftWorld) replaceableBlock.getWorld()).getHandle();
        final Location location = replaceableBlock.getLocation();
        final BoundingBox bb = this.hitBox.getNMSBoundingBox(location, player.getYaw());
        final var blockStates = new ArrayList<org.bukkit.block.BlockState>();
        final BlockPos[] blocksToReplace = LocationUtils.getBlockPosesBetween(bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ());

        for (final var blockPos : blocksToReplace) {
            final BlockState blockState = serverLevel.getBlockState(blockPos);

            if (!BlockUtils.isReplaceable(blockState.getBlock())) {
                return;
            }

            blockStates.add(CraftBlockStates.getUnplacedBlockState(serverLevel, blockPos, blockState));
        }

        if (this.hitBox.getType().isSolid()) {
            for (final var entity : LocationUtils.getNearbyNMSEntities(serverLevel, AABB.of(bb))) {
                if (!BlockUtils.isIgnorableEntity(entity.getType())) return;
            }
        }

        final ItemStack itemInHand = hand != null
                ? player.getInventory().getItem(hand)
                : this.itemStack;

        if (customName != null) {
            final ItemMeta itemMeta = itemInHand.getItemMeta();

            itemMeta.displayName(customName);
            itemInHand.setItemMeta(itemMeta);
        }

        final CustomDecorPlaceEvent event = new CustomDecorPlaceEvent(
                this.setHitBox(
                        player.getName(),
                        replaceableBlock,
                        this.summonItem(replaceableBlock, blockFace, player, itemInHand),
                        bb,
                        blocksToReplace
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

        this.getSoundGroup().playPlaceSound(location.toCenterLocation());
        MSDecor.getCoreProtectAPI().logPlacement(player.getName(), location, Material.VOID_AIR, replaceableBlock.getBlockData());

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
        if (this.clickAction != null) {
            this.clickAction.accept(event);
        }
    }

    @Override
    public void doPlaceAction(final @NotNull CustomDecorPlaceEvent event) {
        if (this.placeAction != null) {
            this.placeAction.accept(event);
        }
    }

    @Override
    public void doBreakAction(final @NotNull CustomDecorBreakEvent event) {
        if (this.breakAction != null) {
            this.breakAction.accept(event);
        }
    }

    private @NotNull ItemDisplay summonItem(
            final @NotNull Block block,
            final @NotNull BlockFace blockFace,
            final @NotNull Player player,
            final @NotNull ItemStack itemInHand
    ) {
        return block.getWorld().spawn(
                block.getLocation().toCenterLocation().add(
                        this.hitBox.getModelOffsetX(),
                        this.hitBox.getModelOffsetY(),
                        this.hitBox.getModelOffsetZ()
                ),
                ItemDisplay.class,
                itemDisplay -> {
                    itemDisplay.setRotation(
                            this.hitBox.getX() > 1.0d || this.hitBox.getZ() > 1.0d
                            || this.hitBox.getX() < -1.0d || this.hitBox.getZ() < -1.0d
                            ? LocationUtils.to90(player.getYaw())
                            : LocationUtils.to45(player.getYaw()),
                            0.0f
                    );
                    itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);

                    final ItemMeta itemMeta = itemInHand.getItemMeta();
                    CustomDecorData.Type<D> type = null;

                    if (this.isLightTyped()) {
                        type = this.lightLevelTypeMap.get(this.lightLevels[0]);
                    } else if (this.isFaceTyped()) {
                        type = this.faceTypeMap.get(Facing.fromBlockFace(blockFace));
                    } else if (this.isWrenchable()) {
                        type = this.getWrenchTypeOf(itemInHand);
                    }

                    if (
                            type == null
                            || CustomDecorType.matchesTypedKey(
                                    itemMeta.getPersistentDataContainer().get(
                                            CustomDecorType.TYPE_NAMESPACED_KEY,
                                            PersistentDataType.STRING
                                    )
                            )
                    ) {
                        itemDisplay.setItemStack(itemInHand);
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

    private @NotNull CustomDecor setHitBox(
            final @NotNull String placer,
            final @NotNull Block block,
            final @NotNull ItemDisplay itemDisplay,
            final @NotNull BoundingBox boundingBox,
            final BlockPos @NotNull [] blockPoses
    ) {
        final World world = block.getWorld();
        final DecorHitBox.Type type = hitBox.getType();
        final double x = this.hitBox.getX();
        final double y = this.hitBox.getY();
        final double z = this.hitBox.getZ();
        final Interaction[] interactions =
                fillInteractions(
                        world,
                        boundingBox,
                        interaction -> {
                            interaction.setInteractionHeight((float) y);
                            interaction.setInteractionWidth(
                                    x == z && x < 1.0d
                                    ? (float) x
                                    : 1.0f
                            );
                            interaction.setResponsive(false);
                        },
                        y > 0.0d
                        ? 0.0d
                        : -Math.ceil(y)
                );

        DecorHitBox.processInteractions(this, itemDisplay, interactions, boundingBox);

        if (type != DecorHitBox.Type.NONE) {
            final var blocks = fillBlocks(placer, ((CraftWorld) world).getHandle(), blockPoses, type.getNMSBlock());

            if (
                    this.isLightable()
                    || this.isLightTyped()
            ) {
                final int lightLevel = this.isLightTyped()
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

        if (
                block.getBlockData() instanceof final Light light
                && this.hasParameters(DecorParameter.LIGHTABLE)
        ) {
            light.setLevel(this.lightLevels[0]);
            block.setBlockData(light, true);
        }

        return new CustomDecor(
                this,
                itemDisplay,
                interactions,
                boundingBox
        );
    }

    private static Interaction @NotNull [] fillInteractions(
            final @NotNull World world,
            final @NotNull BoundingBox box,
            final @NotNull Consumer<Interaction> function,
            final double offsetY
    ) {
        final BlockPos[] blockPoses = LocationUtils.getBlockPosesBetween(box.minX(), box.minY(), box.minZ(), box.maxX(), box.minY(), box.maxZ());
        final int length = blockPoses.length;
        final Interaction[] interactions = new Interaction[length];

        for (int i = 0; i < length; ++i) {
            final BlockPos blockPos = blockPoses[i];
            interactions[i] = world.spawn(
                    new Location(
                            null,
                            blockPos.getX() + 0.5d,
                            blockPos.getY() + offsetY,
                            blockPos.getZ() + 0.5d
                    ),
                    Interaction.class,
                    function
            );
        }

        return interactions;
    }

    static @NotNull List<Block> fillBlocks(
            final @NotNull String placer,
            final @NotNull ServerLevel serverLevel,
            final BlockPos @NotNull [] blockPoses,
            final @NotNull net.minecraft.world.level.block.Block block
    ) {
        final var blockList = new ArrayList<Block>();
        final var list = new ArrayList<BlockPos>();
        final BlockState fillBlockState = block.defaultBlockState();
        final Material fillMaterial = fillBlockState.getBukkitMaterial();
        final BlockData fillBlockData = fillBlockState.createCraftBlockData();

        for (final var blockPos : blockPoses) {
            final Location location = LocationUtils.nmsToBukkit(blockPos, serverLevel);
            BlockState blockState = net.minecraft.world.level.block.Block.updateFromNeighbourShapes(fillBlockState, serverLevel, blockPos);

            if (blockState.isAir()) {
                blockState = fillBlockState;
            } else {
                MSDecor.getCoreProtectAPI().logRemoval(
                        placer,
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
                        placer,
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

        if (!container.has(CustomDecorType.TYPE_NAMESPACED_KEY)) {
            container.set(CustomDecorType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING, typeKey);
            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    public final class Builder {
        private NamespacedKey namespacedKey;
        private DecorHitBox hitBox;
        private Facing facing;
        private ItemStack itemStack;
        private SoundGroup soundGroup;
        private List<Map.Entry<Recipe, Boolean>> recipeList;
        private EnumSet<DecorParameter> parameterSet;
        private double sitHeight = Double.NaN;
        private CustomDecorData.Type<D>[] wrenchTypes;
        private EnumMap<Facing, CustomDecorData.Type<D>> faceTypeMap;
        private int[] lightLevels;
        private Map<Integer, CustomDecorData.Type<D>> lightLevelTypeMap;
        private Consumer<CustomDecorClickEvent> clickAction;
        private Consumer<CustomDecorPlaceEvent> placeAction;
        private Consumer<CustomDecorBreakEvent> breakAction;
        private boolean dropsType;

        private static final String KEY_REGEX = "[a-z0-9./_-]+";
        private static final Pattern KEY_PATTERN = Pattern.compile(KEY_REGEX);

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

            if (
                    isGreaterThanOneWithDecimal(this.hitBox.getX())
                    || isGreaterThanOneWithDecimal(this.hitBox.getZ())
            ) {
                throw new IllegalArgumentException("Hit box x and z values cannot be greater than one with decimal!");
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

            if (this.recipeList == null) {
                this.recipeList = Collections.emptyList();
            }

            if (this.parameterSet == null) {
                this.parameterSet = EnumSet.noneOf(DecorParameter.class);
            } else {
                if (
                        this.isSittable()
                        && this.sitHeight != this.sitHeight
                ) {
                    throw new IllegalArgumentException("Sit height is not set, but sittable parameter is set!");
                }

                if (
                        this.isWrenchable()
                        && this.wrenchTypes == null
                ) {
                    throw new IllegalArgumentException("Wrench types are not set, but wrenchable parameter is set!");
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
            }

            if (this.clickAction == null) {
                if (
                        this.isWrenchable()
                        && this.isSittable()
                ) {
                    this.clickAction = DecorParameter.WRENCHABLE_SITTABLE_CLICK_ACTION;
                } else if (
                        this.isWrenchable()
                        && this.isLightable()
                ) {
                    this.clickAction = DecorParameter.WRENCHABLE_LIGHTABLE_CLICK_ACTION;
                } else if (this.isSittable()) {
                    this.clickAction = DecorParameter.SITTABLE_RIGHT_CLICK_ACTION;
                } else if (this.isWrenchable()) {
                    this.clickAction = DecorParameter.WRENCHABLE_RIGHT_CLICK_ACTION;
                } else if (this.isLightTyped()) {
                    this.clickAction = DecorParameter.LIGHT_TYPED_RIGHT_CLICK_ACTION;
                } else if (this.isLightable()) {
                    this.clickAction = DecorParameter.LIGHTABLE_RIGHT_CLICK_ACTION;
                }
            }

            return this;
        }

        public NamespacedKey key() {
            return this.namespacedKey;
        }

        public @NotNull Builder key(final @NotNull String key) throws IllegalArgumentException {
            if (!KEY_PATTERN.matcher(key).matches()) {
                throw new IllegalArgumentException("Key '" + key + "' does not match regex '" + KEY_REGEX + "'!");
            }

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

        public List<Map.Entry<Recipe, Boolean>> recipeList() {
            return this.recipeList;
        }

        @SafeVarargs
        public final @NotNull Builder recipes(
                final @NotNull Function<Builder, Map.Entry<Recipe, Boolean>> first,
                final Function<Builder, Map.Entry<Recipe, Boolean>> @NotNull ... rest
        ) {
            final var recipeList = new ArrayList<Map.Entry<Recipe, Boolean>>();

            recipeList.add(first.apply(this));

            for (final var entry : rest) {
                recipeList.add(entry.apply(this));
            }

            this.recipeList = recipeList;
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

        public CustomDecorData.Type<D>[] wrenchTypes() {
            return this.wrenchTypes;
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        public final @NotNull Builder wrenchTypes(
                final @NotNull Function<Builder, CustomDecorData.Type<D>> first,
                final Function<Builder, CustomDecorData.Type<D>> @NotNull ... rest
        ) {
            this.validateParam(
                    DecorParameter.WRENCHABLE,
                    "Set wrenchable parameter before setting wrench types!"
            );

            final var firstType = first.apply(this);
            final CustomDecorData.Type<D>[] restTypes = (CustomDecorData.Type<D>[]) new CustomDecorData.Type<?>[rest.length];

            for (int i = 0; i < rest.length; i++) {
                restTypes[i] = rest[i].apply(this);
            }

            return this.wrenchTypes(firstType,  restTypes);
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        public final @NotNull Builder wrenchTypes(
                final @NotNull CustomDecorData.Type<D> first,
                final CustomDecorData.Type<D> @NotNull ... rest
        ) throws IllegalStateException {
            this.validateParam(
                    DecorParameter.WRENCHABLE,
                    "Set wrenchable parameter before setting wrench types!"
            );

            if (this.itemStack == null) {
                throw new IllegalStateException("Item stack is not set! Set item stack before setting wrench types!");
            }

            final int restLength = rest.length;
            this.wrenchTypes = (CustomDecorData.Type<D>[]) new CustomDecorData.Type<?>[restLength + 2];

            if (restLength != 0) {
                System.arraycopy(rest, 0, this.wrenchTypes, 2, restLength);
            }

            this.wrenchTypes[0] =
                    new Type(
                            this,
                            "default",
                            this.itemStack
                    );
            this.wrenchTypes[1] = first;

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

        public Consumer<CustomDecorClickEvent> clickAction() {
            return this.clickAction;
        }

        public @NotNull Builder clickAction(final @NotNull Consumer<CustomDecorClickEvent> rightClickAction) {
            this.clickAction = rightClickAction;
            return this;
        }

        public Consumer<CustomDecorPlaceEvent> placeAction() {
            return this.placeAction;
        }

        public @NotNull Builder placeAction(final @NotNull Consumer<CustomDecorPlaceEvent> placeAction) {
            this.placeAction = placeAction;
            return this;
        }

        public Consumer<CustomDecorBreakEvent> breakAction() {
            return this.breakAction;
        }

        public @NotNull Builder breakAction(final @NotNull Consumer<CustomDecorBreakEvent> breakAction) {
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

        public boolean isSittable() {
            return this.parameterSet.contains(DecorParameter.SITTABLE);
        }

        public boolean isWrenchable() {
            return this.parameterSet.contains(DecorParameter.WRENCHABLE);
        }

        public boolean isLightable() {
            return this.parameterSet.contains(DecorParameter.LIGHTABLE);
        }

        public boolean isLightTyped() {
            return this.parameterSet.contains(DecorParameter.LIGHT_TYPED);
        }

        public boolean isFaceTyped() {
            return this.parameterSet.contains(DecorParameter.FACE_TYPED);
        }

        public boolean isTyped() {
            return this.isWrenchable() || this.isLightTyped() || this.isFaceTyped();
        }

        private static boolean isGreaterThanOneWithDecimal(final double value) {
            return (value > 1.0d && Math.floor(value) - value != 0.0d)
                    || (value < -1.0d && Math.ceil(value) - value != 0.0d);
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
        private final CustomDecorType decorType;
        private final ItemStack itemStack;

        @SuppressWarnings("unchecked")
        public Type(
                final @NotNull Builder builder,
                final @NotNull String key,
                final @NotNull ItemStack itemStack
        ) {
            if (!Builder.KEY_PATTERN.matcher(key).matches()) {
                throw new IllegalArgumentException("Key '" + key + "' does not match regex '" + Builder.KEY_REGEX + "'!");
            }

            this.namespacedKey = new NamespacedKey(CustomDecorType.NAMESPACE, builder.namespacedKey.getKey() + ".type." + key);
            this.decorType = CustomDecorType.fromClass((Class<D>) CustomDecorDataImpl.this.getClass());
            this.itemStack = setTypeKey(itemStack, this.namespacedKey.getKey());
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return this.namespacedKey;
        }

        @Override
        public @NotNull CustomDecorType getDecorType() {
            return this.decorType;
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
