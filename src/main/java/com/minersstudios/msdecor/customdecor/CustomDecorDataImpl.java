package com.minersstudios.msdecor.customdecor;

import com.minersstudios.mscore.util.BlockUtils;
import com.minersstudios.mscore.util.LocationUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.events.CustomDecorPlaceEvent;
import com.minersstudios.msdecor.events.CustomDecorRightClickEvent;
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
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Light;
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
import java.util.function.BiConsumer;
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
    protected final Type<D>[] wrenchTypes;
    protected final int[] lightLevels;
    protected final Map<Facing, Type<D>> faceTypeMap;
    protected final Map<Integer, Type<D>> lightLevelTypeMap;
    protected final BiConsumer<CustomDecorRightClickEvent, Interaction> rightClickAction;
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
                builder.recipes == null
                ? Collections.emptyList()
                : builder.recipes;
        this.parameterSet =
                builder.parameterSet == null
                ? EnumSet.noneOf(DecorParameter.class)
                : builder.parameterSet;
        this.sitHeight = builder.sitHeight;
        this.wrenchTypes = builder.wrenchTypes;
        this.faceTypeMap =
                builder.faceTypeMap == null
                ? Collections.emptyMap()
                : builder.faceTypeMap;
        this.lightLevels = builder.lightLevels;
        this.lightLevelTypeMap =
                builder.lightLevelTypeMap == null
                ? Collections.emptyMap()
                : builder.lightLevelTypeMap;
        this.rightClickAction = builder.rightClickAction;
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
    public Type<D> @NotNull [] wrenchTypes() throws UnsupportedOperationException {
        if (!this.isWrenchable()) {
            throw new UnsupportedOperationException("This custom decor is not wrenchable!");
        }

        return this.wrenchTypes;
    }

    @Override
    @Contract("null -> null")
    public @Nullable Type<D> getTypeOf(final @Nullable Interaction interaction) throws UnsupportedOperationException {
        if (!this.isWrenchable()) {
            throw new UnsupportedOperationException("This custom decor is not wrenchable!");
        }
        
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
    public @Nullable Type<D> getTypeOf(final @Nullable ItemStack itemStack) throws UnsupportedOperationException {
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
    public @Nullable Type<D> getNextType(final @Nullable Interaction interaction) throws UnsupportedOperationException {
        return this.getNextType(this.getTypeOf(interaction));
    }

    @Override
    @Contract("null -> null")
    public @Nullable Type<D> getNextType(final @Nullable ItemStack itemStack) throws UnsupportedOperationException {
        return this.getNextType(this.getTypeOf(itemStack));
    }

    @Override
    @Contract("null -> null")
    public @Nullable Type<D> getNextType(final @Nullable Type<? extends CustomDecorData<?>> type) throws UnsupportedOperationException {
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
    public @NotNull @Unmodifiable Map<Facing, Type<D>> typeFaceMap() throws UnsupportedOperationException {
        if (!this.parameterSet.contains(DecorParameter.FACE_TYPED)) {
            throw new UnsupportedOperationException("This custom decor is not face typed!");
        }

        return this.faceTypeMap;
    }

    @Override
    @Contract("null -> null")
    public @Nullable Type<D> getTypeByFace(final @Nullable BlockFace blockFace) throws UnsupportedOperationException {
        if (!this.parameterSet.contains(DecorParameter.FACE_TYPED)) {
            throw new UnsupportedOperationException("This custom decor is not face typed!");
        }

        if (!this.facing.hasFace(blockFace)) return null;

        final Facing facing = Facing.fromBlockFace(blockFace);
        return facing == null
                ? null
                : this.faceTypeMap.getOrDefault(facing, null);
    }

    @Override
    public @NotNull @Unmodifiable Map<Integer, Type<D>> typeLightLevelMap() throws UnsupportedOperationException {
        if (!this.parameterSet.contains(DecorParameter.LIGHT_TYPED)) {
            throw new UnsupportedOperationException("This custom decor is not light typed!");
        }

        return this.lightLevelTypeMap;
    }

    @Override
    @Contract("null -> null")
    public @Nullable Type<D> getTypeByLightLevel(final @Nullable Integer lightLevel) throws UnsupportedOperationException {
        if (!this.parameterSet.contains(DecorParameter.LIGHT_TYPED)) {
            throw new UnsupportedOperationException("This custom decor is not light typed!");
        }

        return this.lightLevelTypeMap.getOrDefault(lightLevel, null);
    }

    @Override
    public int @NotNull [] lightLevels() throws UnsupportedOperationException {
        if (!this.parameterSet.contains(DecorParameter.LIGHTABLE)) {
            throw new UnsupportedOperationException("This custom decor is not lightable!");
        }

        return this.lightLevels.clone();
    }

    @Override
    public int nextLightLevel(final int lightLevel) throws UnsupportedOperationException {
        if (!this.parameterSet.contains(DecorParameter.LIGHTABLE)) {
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
        if (!this.parameterSet.contains(DecorParameter.SITTABLE)) {
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
        final BlockPos[] blocksToReplace = LocationUtils.getBlockPosesBetween(bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ());

        for (final var blockPos : blocksToReplace) {
            if (!BlockUtils.isReplaceable(serverLevel.getBlockState(blockPos).getBlock())) return;
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
                        this.summonItem(replaceableBlock, player, itemInHand),
                        bb,
                        blocksToReplace
                ),
                replaceableBlock.getState(),
                player,
                hand == null ? EquipmentSlot.HAND : hand
        );
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            event.getCustomDecor().destroy(player, false);
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
    public void doRightClickAction(
            final @NotNull CustomDecorRightClickEvent event,
            final @NotNull Interaction interaction
    ) {
        if (this.rightClickAction != null) {
            this.rightClickAction.accept(event, interaction);
        }
    }

    private @NotNull ItemDisplay summonItem(
            final @NotNull Block block,
            final @NotNull Player player,
            final @NotNull ItemStack itemInHand
    ) {
        return block.getWorld().spawn(block.getLocation().toCenterLocation(), ItemDisplay.class, itemDisplay -> {
            itemDisplay.setRotation(
                    this.hitBox.getX() > 1.0d || this.hitBox.getZ() > 1.0d
                    || this.hitBox.getX() < -1.0d || this.hitBox.getZ() < -1.0d
                            ? LocationUtils.to90(player.getYaw())
                            : LocationUtils.to45(player.getYaw()),
                    0.0f
            );
            itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
            itemDisplay.setItemStack(itemInHand);
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
        final var interactions =
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

            if (this.isLightable()) {
                final int firstLightLevel = this.lightLevels[0];

                for (final var currentBlock : blocks) {
                    if (currentBlock.getBlockData() instanceof final Light light) {
                        light.setLevel(firstLightLevel);
                        currentBlock.setBlockData(light);
                    }
                }
            }
        }

        if (
                block.getBlockData() instanceof final Levelled levelled
                && this.hasParameters(DecorParameter.LIGHTABLE)
        ) {
            levelled.setLevel(this.lightLevels[0]);
            block.setBlockData(levelled, true);
        }

        return new CustomDecor(
                this,
                itemDisplay,
                interactions,
                boundingBox
        );
    }

    private static @NotNull List<Interaction> fillInteractions(
            final @NotNull World world,
            final @NotNull BoundingBox box,
            final @NotNull Consumer<Interaction> function,
            final double offsetY
    ) {
        final var interactions = new ArrayList<Interaction>();

        for (final var blockPos : LocationUtils.getBlockPosesBetween(box.minX(), box.minY(), box.minZ(), box.maxX(), box.minY(), box.maxZ())) {
            interactions.add(world.spawn(
                    new Location(
                            null,
                            blockPos.getX() + 0.5d,
                            blockPos.getY() + offsetY,
                            blockPos.getZ() + 0.5d
                    ),
                    Interaction.class,
                    function
            ));
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

    public class Builder {
        protected NamespacedKey namespacedKey;
        protected DecorHitBox hitBox;
        protected Facing facing;
        protected ItemStack itemStack;
        protected SoundGroup soundGroup;
        protected List<Map.Entry<Recipe, Boolean>> recipes;
        protected EnumSet<DecorParameter> parameterSet;
        protected double sitHeight = Double.NaN;
        protected Type<D>[] wrenchTypes;
        protected Map<Facing, Type<D>> faceTypeMap;
        protected int[] lightLevels;
        protected Map<Integer, Type<D>> lightLevelTypeMap;
        protected BiConsumer<CustomDecorRightClickEvent, Interaction> rightClickAction;
        protected boolean dropsType;

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

            if (this.recipes == null) {
                this.recipes = Collections.emptyList();
            }

            if (this.parameterSet == null) {
                this.parameterSet = EnumSet.noneOf(DecorParameter.class);
            } else {
                if (
                        this.parameterSet.contains(DecorParameter.SITTABLE)
                        && this.sitHeight != this.sitHeight
                ) {
                    throw new IllegalArgumentException("Sit height is not set, but sittable parameter is set!");
                }

                if (
                        this.parameterSet.contains(DecorParameter.WRENCHABLE)
                        && this.wrenchTypes == null
                ) {
                    throw new IllegalArgumentException("Wrench types are not set, but wrenchable parameter is set!");
                }

                if (
                        this.parameterSet.contains(DecorParameter.LIGHTABLE)
                        && this.lightLevels == null
                ) {
                    throw new IllegalArgumentException("Light levels are not set, but lightable parameter is set!");
                }

                if (
                        (
                                this.parameterSet.contains(DecorParameter.LIGHTABLE)
                                || this.parameterSet.contains(DecorParameter.LIGHT_TYPED)
                        )
                        && !this.hitBox.getType().isLight()

                ) {
                    throw new IllegalArgumentException("Lightable or light typed parameter is set, but hit box type is not light!");
                }

                if (this.parameterSet.contains(DecorParameter.LIGHT_TYPED)) {
                    if (this.lightLevels == null) {
                        throw new IllegalArgumentException("Light levels are not set, but light typed parameter is set!");
                    }

                    if (this.lightLevelTypeMap == null || this.lightLevelTypeMap.isEmpty()) {
                        throw new IllegalArgumentException("Light level type map is not set, but light typed parameter is set!");
                    }
                }

                if (
                        this.parameterSet.contains(DecorParameter.FACE_TYPED)
                        && (
                                this.faceTypeMap == null
                                || this.faceTypeMap.isEmpty()
                        )
                ) {
                    throw new IllegalArgumentException("Face type map is not set, but face typed parameter is set!");
                }
            }

            if (this.rightClickAction == null) {
                if (this.parameterSet.contains(DecorParameter.SITTABLE)) {
                    this.rightClickAction = DecorParameter.SITTABLE_RIGHT_CLICK_ACTION;
                } else if (this.parameterSet.contains(DecorParameter.WRENCHABLE)) {
                    this.rightClickAction = DecorParameter.WRENCHABLE_RIGHT_CLICK_ACTION;
                } else if (this.parameterSet.contains(DecorParameter.LIGHTABLE)) {
                    this.rightClickAction = DecorParameter.LIGHTABLE_RIGHT_CLICK_ACTION;
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

            final ItemMeta meta = itemStack.getItemMeta();
            final PersistentDataContainer container = meta.getPersistentDataContainer();

            if (!container.has(CustomDecorType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING)) {
                container.set(
                        CustomDecorType.TYPE_NAMESPACED_KEY,
                        PersistentDataType.STRING,
                        this.namespacedKey.getKey()
                );
                itemStack.setItemMeta(meta);
            }

            this.itemStack = itemStack;
            return this;
        }

        public List<Map.Entry<Recipe, Boolean>> recipes() {
            return this.recipes;
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

            this.recipes = recipeList;
            return this;
        }

        public DecorParameter[] parameters() {
            return
                    this.parameterSet == null
                    ? new DecorParameter[0]
                    : this.parameterSet.toArray(new DecorParameter[0]);
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

        public @NotNull Builder sitHeight(final double sitHeight) throws IllegalStateException {
            if (this.parameterSet == null) {
                throw new IllegalStateException("Parameters are not set! Set parameters before setting sit height!");
            }

            if (!this.parameterSet.contains(DecorParameter.SITTABLE)) {
                throw new IllegalStateException("Sittable parameter is not set! Set sittable parameter before setting sit height!");
            }

            this.sitHeight = sitHeight;
            return this;
        }

        public Type<D>[] wrenchTypes() {
            return this.wrenchTypes;
        }

        @SafeVarargs
        @SuppressWarnings("unchecked")
        public final @NotNull Builder wrenchTypes(
                final @NotNull Function<Builder, Type<D>> first,
                final Function<Builder, Type<D>> @NotNull ... rest
        ) {
            final var firstType = first.apply(this);
            final Type<D>[] restTypes = (Type<D>[]) new Type<?>[rest.length];

            for (int i = 0; i < rest.length; i++) {
                restTypes[i] = rest[i].apply(this);
            }

            return this.wrenchTypes(firstType,  restTypes);
        }

        @SuppressWarnings("unchecked")
        public @NotNull Builder wrenchTypes(
                final @NotNull Type<D> first,
                final Type<D> @NotNull ... rest
        ) throws IllegalStateException {
            if (this.itemStack == null) {
                throw new IllegalStateException("Item stack is not set! Set item stack before setting wrench types!");
            }

            if (this.parameterSet == null) {
                throw new IllegalStateException("Parameters are not set! Set parameters before setting wrench types!");
            }

            if (!this.parameterSet.contains(DecorParameter.WRENCHABLE)) {
                throw new IllegalStateException("Wrenchable parameter is not set! Set wrenchable parameter before setting wrench types!");
            }

            this.wrenchTypes = (Type<D>[]) new Type<?>[rest.length + 2];

            if (rest.length != 0) {
                System.arraycopy(rest, 0, this.wrenchTypes, 2, this.wrenchTypes.length);
            }

            this.wrenchTypes[0] =
                    new TypeBuilder()
                    .key(this, "default")
                    .itemStack(this.itemStack)
                    .build();
            this.wrenchTypes[1] = first;

            return this;
        }

        public Map<Facing, Type<D>> faceTypeMap() {
            return this.faceTypeMap;
        }

        public @NotNull Builder faceTypeMap(final @NotNull Map<Facing, Type<D>> faceTypeMap) throws IllegalStateException, IllegalArgumentException {
            if (this.parameterSet == null) {
                throw new IllegalStateException("Parameters are not set! Set parameters before setting face type map!");
            }

            if (!this.parameterSet.contains(DecorParameter.FACE_TYPED)) {
                throw new IllegalStateException("Face typed parameter is not set! Set face typed parameter before setting face type map!");
            }

            if (this.faceTypeMap.isEmpty()) {
                throw new IllegalArgumentException("Face type map is empty! Set face type map before setting face type map!");
            }

            this.faceTypeMap = faceTypeMap;
            return this;
        }

        public int[] lightLevels() {
            return this.lightLevels;
        }

        public @NotNull Builder lightLevels(
                final int first,
                final int @NotNull ... rest
        ) throws IllegalStateException, IllegalArgumentException {
            if (this.parameterSet == null) {
                throw new IllegalStateException("Parameters are not set! Set parameters before setting light levels!");
            }

            if (!this.parameterSet.contains(DecorParameter.LIGHTABLE)) {
                throw new IllegalStateException("Lightable parameter is not set! Set lightable parameter before setting light levels!");
            }

            this.lightLevels = new int[rest.length + 1];

            System.arraycopy(rest, 0, this.lightLevels, 1, rest.length);
            this.lightLevels[0] = first;

            for (final var level : this.lightLevels) {
                if (level < 0 || level > 15) {
                    throw new IllegalArgumentException("Light level '" + level + "' is not in range [0, 15]!");
                }
            }

            for (int i = 0; i < this.lightLevels.length; i++) {
                for (int j = i + 1; j < this.lightLevels.length; j++) {
                    if (this.lightLevels[i] == this.lightLevels[j]) {
                        throw new IllegalArgumentException("Light level '" + this.lightLevels[i] + "' is duplicated! Light levels must be unique!");
                    }
                }
            }

            return this;
        }

        public Map<Integer, Type<D>> lightLevelTypeMap() {
            return this.lightLevelTypeMap;
        }

        public @NotNull Builder lightLevelTypeMap(final @NotNull Map<Integer, Type<D>> lightLevelTypeMap) throws IllegalStateException, IllegalArgumentException {
            if (this.parameterSet == null) {
                throw new IllegalStateException("Parameters are not set! Set parameters before setting light level type map!");
            }

            if (!this.parameterSet.contains(DecorParameter.LIGHT_TYPED)) {
                throw new IllegalStateException("Light typed parameter is not set! Set light typed parameter before setting light level type map!");
            }

            if (this.lightLevelTypeMap.isEmpty()) {
                throw new IllegalArgumentException("Light level type map is empty! Set light level type map before setting light level type map!");
            }

            this.lightLevelTypeMap = lightLevelTypeMap;
            return this;
        }

        public BiConsumer<CustomDecorRightClickEvent, Interaction> rightClickAction() {
            return this.rightClickAction;
        }

        public @NotNull Builder rightClickAction(final @NotNull BiConsumer<CustomDecorRightClickEvent, Interaction> rightClickAction) {
            this.rightClickAction = rightClickAction;
            return this;
        }

        public boolean dropsType() {
            return this.dropsType;
        }

        public @NotNull Builder dropsType(final boolean dropsType) throws IllegalStateException {
            if (
                    this.parameterSet == null
                    || (
                            !this.parameterSet.contains(DecorParameter.WRENCHABLE)
                            && !this.parameterSet.contains(DecorParameter.LIGHT_TYPED)
                            && !this.parameterSet.contains(DecorParameter.FACE_TYPED)
                    )
            ) {
                throw new IllegalStateException("Parameters are not set! Set parameters before setting drops type!");
            }

            this.dropsType = dropsType;
            return this;
        }

        private static boolean isGreaterThanOneWithDecimal(final double value) {
            return (value > 1.0d && Math.floor(value) - value != 0.0d)
                    || (value < -1.0d && Math.ceil(value) - value != 0.0d);
        }
    }

    public class TypeBuilder {
        protected NamespacedKey namespacedKey;
        protected ItemStack itemStack;

        public @NotNull Type<D> build() throws IllegalArgumentException {
            return new TypeImpl(this) {};
        }

        public NamespacedKey key() {
            return this.namespacedKey;
        }

        public @NotNull TypeBuilder key(
                final @NotNull Builder builder,
                final @NotNull String key
        ) throws IllegalArgumentException {
            if (!Builder.KEY_PATTERN.matcher(key).matches()) {
                throw new IllegalArgumentException("Key '" + key + "' does not match regex '" + Builder.KEY_REGEX + "'!");
            }

            this.namespacedKey = new NamespacedKey(CustomDecorType.NAMESPACE, builder.namespacedKey.getKey() + ".type." + key);
            return this;
        }

        public ItemStack itemStack() {
            return this.itemStack;
        }

        public @NotNull TypeBuilder itemStack(final @NotNull ItemStack itemStack) throws IllegalStateException {
            if (this.namespacedKey == null) {
                throw new IllegalStateException("Key is not set! Set key before setting item stack!");
            }

            final ItemMeta meta = itemStack.getItemMeta();
            final PersistentDataContainer container = meta.getPersistentDataContainer();

            if (!container.has(CustomDecorType.TYPE_NAMESPACED_KEY, PersistentDataType.STRING)) {
                container.set(
                        CustomDecorType.TYPE_NAMESPACED_KEY,
                        PersistentDataType.STRING,
                        this.namespacedKey.getKey()
                );
                itemStack.setItemMeta(meta);
            }

            this.itemStack = itemStack;
            return this;
        }
    }

    private abstract class TypeImpl implements CustomDecorData.Type<D> {
        private final NamespacedKey namespacedKey;
        private final CustomDecorType decorType;
        private final ItemStack itemStack;

        @SuppressWarnings("unchecked")
        private TypeImpl(final @NotNull TypeBuilder builder) {
            this.namespacedKey = builder.namespacedKey;
            this.decorType = CustomDecorType.fromClass((Class<D>) CustomDecorDataImpl.this.getClass());
            this.itemStack = builder.itemStack;
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
                            && ((Type<?>) type).getKey().equals(this.getKey())
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
