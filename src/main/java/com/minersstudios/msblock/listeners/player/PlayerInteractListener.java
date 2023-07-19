package com.minersstudios.msblock.listeners.player;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.customblock.CustomBlock;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.events.CustomBlockRightClickEvent;
import com.minersstudios.msblock.utils.CustomBlockUtils;
import com.minersstudios.msblock.utils.PlayerUtils;
import com.minersstudios.msblock.utils.UseBucketsAndSpawnableItems;
import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.utils.BlockUtils;
import com.minersstudios.mscore.utils.MSBlockUtils;
import com.minersstudios.mscore.utils.MSDecorUtils;
import com.minersstudios.mscore.listener.AbstractMSListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Set;

@MSListener
public class PlayerInteractListener extends AbstractMSListener {
    private Block blockAtFace;
    private Location interactionPoint;
    private InteractionHand interactionHand;
    private ItemStack itemInHand;
    private Player player;
    private GameMode gameMode;
    private net.minecraft.world.item.ItemStack nmsItem;
    private CustomBlockData clickedCustomBlockData;

    private final SecureRandom random = new SecureRandom();

    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlaceArmorStand(@NotNull PlayerInteractEvent event) {
        if (
                event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getClickedBlock() == null
                || event.getHand() == null
                || !MSBlockUtils.isCustomBlock(event.getPlayer().getInventory().getItemInMainHand())
        ) return;
        event.setUseItemInHand(Event.Result.DENY);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (
                event.getClickedBlock() == null
                || event.getHand() == null
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) return;

        Block clickedBlock = event.getClickedBlock();
        BlockFace blockFace = event.getBlockFace();
        EquipmentSlot hand = event.getHand();
        this.player = event.getPlayer();
        this.gameMode = this.player.getGameMode();
        ItemStack itemInMainHand = this.player.getInventory().getItemInMainHand();

        if (
                event.getAction() == Action.RIGHT_CLICK_BLOCK
                && Tag.SHULKER_BOXES.isTagged(clickedBlock.getType())
                && clickedBlock.getRelative(BlockFace.UP).getType() == Material.NOTE_BLOCK
                && clickedBlock.getState() instanceof ShulkerBox shulkerBox
                && clickedBlock.getBlockData() instanceof Directional directional
                && BlockUtils.REPLACE.contains(clickedBlock.getRelative(directional.getFacing()).getType())
        ) {
            event.setCancelled(true);
            PlayerUtils.openShulkerBoxWithoutAnimation(this.player, shulkerBox);
        }

        if (MSDecorUtils.isCustomDecor(itemInMainHand)) return;
        if (hand != EquipmentSlot.HAND && MSBlockUtils.isCustomBlock(itemInMainHand)) {
            hand = EquipmentSlot.HAND;
        }
        this.itemInHand = this.player.getInventory().getItem(hand);
        this.interactionPoint = PlayerUtils.getInteractionPoint(this.player.getEyeLocation(), 8);

        if (
                clickedBlock.getBlockData() instanceof NoteBlock noteBlock
                && !this.itemInHand.getType().isAir()
                && (hand == EquipmentSlot.HAND || hand == EquipmentSlot.OFF_HAND)
                && !MSBlockUtils.isCustomBlock(this.itemInHand)
                && this.gameMode != GameMode.ADVENTURE
                && this.gameMode != GameMode.SPECTATOR
        ) {
            this.clickedCustomBlockData = CustomBlockData.fromNoteBlock(noteBlock);
            this.blockAtFace = clickedBlock.getRelative(blockFace);
            this.nmsItem = CraftItemStack.asNMSCopy(this.itemInHand);
            this.interactionHand =
                    hand == EquipmentSlot.HAND
                            ? InteractionHand.MAIN_HAND
                            : InteractionHand.OFF_HAND;
            if (this.interactionPoint != null) {
                if (clickedBlock.getType() == Material.NOTE_BLOCK) {
                    CustomBlockRightClickEvent customBlockRightClickEvent = new CustomBlockRightClickEvent(
                            new CustomBlock(clickedBlock, this.player, this.clickedCustomBlockData),
                            this.player,
                            this.itemInHand,
                            hand,
                            blockFace,
                            this.interactionPoint
                    );
                    Bukkit.getPluginManager().callEvent(customBlockRightClickEvent);
                    if (customBlockRightClickEvent.isCancelled()) return;
                }
                this.useItemInHand(event);
            }
        }

        if (
                MSBlockUtils.isCustomBlock(this.itemInHand)
                && (event.getHand() == EquipmentSlot.HAND || hand == EquipmentSlot.OFF_HAND)
                && BlockUtils.REPLACE.contains(clickedBlock.getRelative(blockFace).getType())
                && this.gameMode != GameMode.ADVENTURE
                && this.gameMode != GameMode.SPECTATOR
                && this.interactionPoint != null
        ) {
            if (
                    ((clickedBlock.getType().isInteractable()
                    && !Tag.STAIRS.isTagged(clickedBlock.getType()))
                    && clickedBlock.getType() != Material.NOTE_BLOCK)
                    && !this.player.isSneaking()
            ) return;

            Block replaceableBlock =
                    BlockUtils.REPLACE.contains(clickedBlock.getType())
                            ? clickedBlock
                            : clickedBlock.getRelative(blockFace);

            for (Entity nearbyEntity : replaceableBlock.getWorld().getNearbyEntities(replaceableBlock.getLocation().toCenterLocation(), 0.5d, 0.5d, 0.5d)) {
                if (!CustomBlockUtils.IGNORABLE_ENTITIES.contains(nearbyEntity.getType())) return;
            }

            ItemMeta itemMeta = this.itemInHand.getItemMeta();

            if (itemMeta == null || !itemMeta.hasCustomModelData()) return;

            CustomBlockData customBlockData = CustomBlockData.fromCustomModelData(itemMeta.getCustomModelData());
            var blockFaces = customBlockData.getFaces();
            var blockAxes = customBlockData.getAxes();
            CustomBlock customBlock = new CustomBlock(replaceableBlock, this.player, customBlockData);

            if (blockFaces != null) {
                if (customBlockData.getPlacingType() == CustomBlockData.PlacingType.BY_BLOCK_FACE) {
                    customBlock.setCustomBlock(hand, blockFace, null);
                } else if (customBlockData.getPlacingType() == CustomBlockData.PlacingType.BY_EYE_POSITION) {
                    customBlock.setCustomBlock(hand, this.getBlockFaceByEyes(blockFaces), null);
                }
            } else if (blockAxes != null) {
                if (customBlockData.getPlacingType() == CustomBlockData.PlacingType.BY_BLOCK_FACE) {
                    customBlock.setCustomBlock(hand, null, this.getAxis());
                } else if (customBlockData.getPlacingType() == CustomBlockData.PlacingType.BY_EYE_POSITION) {
                    customBlock.setCustomBlock(hand, null, this.getAxisByEyes(blockAxes));
                }
            } else {
                customBlock.setCustomBlock(hand);
            }
        }
    }

    private void useItemInHand(@NotNull PlayerInteractEvent event) {
        BlockFace blockFace = event.getBlockFace();
        BlockData materialBlockData = BlockUtils.getBlockDataByMaterial(this.itemInHand.getType());

        if (CustomBlockUtils.SPAWNABLE_ITEMS.contains(this.itemInHand.getType())) {
            new UseBucketsAndSpawnableItems(this.player, this.blockAtFace, blockFace, this.itemInHand);
        } else if (Tag.SLABS.isTagged(this.itemInHand.getType())) {
            boolean placeDouble = true;
            Material itemMaterial = this.itemInHand.getType();

            if (this.blockAtFace.getType() != itemMaterial) {
                this.useOn();
                placeDouble = false;
            }

            if (!(this.blockAtFace.getBlockData() instanceof Slab slab)) return;

            if (placeDouble && this.blockAtFace.getType() == itemMaterial) {
                SoundGroup soundGroup = slab.getSoundGroup();

                slab.setType(Slab.Type.DOUBLE);
                this.blockAtFace.getWorld().playSound(
                        this.blockAtFace.getLocation(),
                        soundGroup.getPlaceSound(),
                        SoundCategory.BLOCKS,
                        soundGroup.getVolume(),
                        soundGroup.getPitch()
                );

                if (this.gameMode == GameMode.SURVIVAL) {
                    this.itemInHand.setAmount(this.itemInHand.getAmount() - 1);
                }
            } else if (
                    blockFace == BlockFace.DOWN
                    || this.interactionPoint.getY() > 0.5d
                    && this.interactionPoint.getY() < 1.0d
                    && this.blockAtFace.getType() == itemMaterial
            ) {
                slab.setType(Slab.Type.TOP);
            } else if (
                    blockFace == BlockFace.UP
                    || this.interactionPoint.getY() < 0.5d
                    && this.interactionPoint.getY() > 0.0d
                    && this.blockAtFace.getType() == itemMaterial
            ) {
                slab.setType(Slab.Type.BOTTOM);
            }

            this.blockAtFace.setBlockData(slab);
        }

        if (!BlockUtils.REPLACE.contains(this.blockAtFace.getType())) return;

        var placeableMaterials = this.clickedCustomBlockData.getPlaceableMaterials();
        if (placeableMaterials != null && placeableMaterials.contains(this.itemInHand.getType())) {
            this.blockAtFace.setType(this.itemInHand.getType(), false);
            this.itemInHand.setAmount(this.itemInHand.getAmount() - 1);
        }

        if (materialBlockData instanceof FaceAttachable) {
            this.useOn();
            FaceAttachable faceAttachable = (FaceAttachable) this.blockAtFace.getBlockData();

            switch (blockFace) {
                case UP -> faceAttachable.setAttachedFace(FaceAttachable.AttachedFace.FLOOR);
                case DOWN -> faceAttachable.setAttachedFace(FaceAttachable.AttachedFace.CEILING);
                default -> faceAttachable.setAttachedFace(FaceAttachable.AttachedFace.WALL);
            }

            if (
                    faceAttachable instanceof Directional directional
                    && faceAttachable.getAttachedFace() == FaceAttachable.AttachedFace.WALL
            ) {
                directional.setFacing(blockFace);
                this.blockAtFace.setBlockData(directional);
                return;
            }

            this.blockAtFace.setBlockData(faceAttachable);
        } else if (materialBlockData instanceof Orientable) {
            this.useOn();
            if (!(this.blockAtFace.getBlockData() instanceof Orientable orientable)) return;
            orientable.setAxis(this.getAxis());
            this.blockAtFace.setBlockData(orientable);
        } else if (
                materialBlockData instanceof Directional directionalMaterial
                && (
                        directionalMaterial.getFaces().contains(blockFace)
                        || Tag.STAIRS.isTagged(this.itemInHand.getType())
                        || Tag.TRAPDOORS.isTagged(this.itemInHand.getType())
                ) && !CustomBlockUtils.IGNORABLE_MATERIALS.contains(this.itemInHand.getType())
        ) {
            this.useOn();

            if (!(this.blockAtFace.getBlockData() instanceof Directional directional)) return;

            if (!(directional instanceof Bisected bisected)) {
                directional.setFacing(blockFace);
            } else {
                double y = this.interactionPoint.getY();

                bisected.setHalf(
                        y == 1.0d
                        || y < 0.5d
                        && y > 0.0d
                                ? Bisected.Half.BOTTOM
                                : Bisected.Half.TOP
                );
                this.blockAtFace.setBlockData(bisected);
                return;
            }

            this.blockAtFace.setBlockData(directional);
        } else if (!this.blockAtFace.getType().isSolid() && this.blockAtFace.getType() != this.itemInHand.getType()) {
            this.useOn();
        }
    }

    private void useOn() {
        ItemStack copyItem = this.itemInHand.clone();
        ServerPlayer serverPlayer = ((CraftPlayer) this.player).getHandle();
        BlockHitResult blockHitResult = new BlockHitResult(
                serverPlayer.getEyePosition(),
                serverPlayer.getDirection(),
                ((CraftBlock) this.blockAtFace).getPosition(),
                false
        );

        if (
                this.nmsItem.useOn(new UseOnContext(serverPlayer, this.interactionHand, blockHitResult)) == InteractionResult.FAIL
                || !copyItem.getType().isBlock()
        ) return;

        BlockData blockData = copyItem.getType().createBlockData();
        SoundGroup soundGroup = blockData.getSoundGroup();

        MSBlock.getCoreProtectAPI().logPlacement(this.player.getName(), this.blockAtFace.getLocation(), copyItem.getType(), blockData);
        this.blockAtFace.getWorld().playSound(
                this.blockAtFace.getLocation(),
                soundGroup.getPlaceSound(),
                SoundCategory.BLOCKS,
                soundGroup.getVolume(),
                this.random.nextFloat() * 0.1f + soundGroup.getPitch()
        );
    }

    private @NotNull Axis getAxis() {
        double x = this.interactionPoint.getX();
        double y = this.interactionPoint.getY();
        return x == 0.0d || x == 1.0d
                ? Axis.X
                : y == 0.0d || y == 1.0d
                ? Axis.Y
                : Axis.Z;
    }

    private @NotNull BlockFace getBlockFaceByEyes(@NotNull Set<BlockFace> blockFaces) {
        float pitch = this.player.getLocation().getPitch();
        return !(pitch >= -45.0f) && blockFaces.contains(BlockFace.DOWN)
                ? BlockFace.DOWN
                : !(pitch <= 45.0f) && blockFaces.contains(BlockFace.UP)
                ? BlockFace.UP
                : this.getBlockFaceByYaw();
    }

    private @NotNull BlockFace getBlockFaceByYaw() {
        return FACES[Math.round(this.player.getLocation().getYaw() / 90f) & 0x3];
    }

    private @NotNull Axis getAxisByEyes(@NotNull Set<Axis> axes) {
        float pitch = this.player.getLocation().getPitch();
        return !(pitch >= -45.0f && pitch <= 45.0f) && axes.contains(Axis.Y)
                ? Axis.Y
                : switch (this.getBlockFaceByYaw()) {
                        case NORTH, SOUTH -> Axis.X;
                        default -> Axis.Z;
                };
    }
}
