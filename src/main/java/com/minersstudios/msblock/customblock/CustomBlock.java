package com.minersstudios.msblock.customblock;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.events.CustomBlockBreakEvent;
import com.minersstudios.msblock.events.CustomBlockPlaceEvent;
import com.minersstudios.mscore.util.BlockUtils;
import com.minersstudios.mscore.util.ItemUtils;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomBlock implements Cloneable {
    private final @NotNull Block block;
    private final @NotNull CustomBlockData customBlockData;

    public CustomBlock(
            @NotNull Block block,
            @NotNull CustomBlockData customBlockData
    ) {
        this.block = block;
        this.customBlockData = customBlockData;
    }

    public void setCustomBlock(
            @NotNull Player player,
            @NotNull EquipmentSlot hand
    ) {
        this.setCustomBlock(player, hand, null, null);
    }

    public void setCustomBlock(
            @NotNull Player player,
            @NotNull EquipmentSlot hand,
            @Nullable BlockFace blockFace,
            @Nullable Axis axis
    ) {
        CustomBlockPlaceEvent event = new CustomBlockPlaceEvent(this, this.block.getState(), player, hand);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        var blockFaceMap = this.customBlockData.getBlockFaceMap();
        var blockAxisMap = this.customBlockData.getBlockAxisMap();

        if (blockFace != null && blockFaceMap != null) {
            this.customBlockData.setNoteBlockData(blockFaceMap.get(blockFace));
        } else if (axis != null && blockAxisMap != null) {
            this.customBlockData.setNoteBlockData(blockAxisMap.get(axis));
        }

        if (this.customBlockData.getNoteBlockData() == null) return;

        MSBlock.getInstance().runTask(() -> {
            this.block.setType(Material.NOTE_BLOCK);

            NoteBlock noteBlock = this.customBlockData.getNoteBlockData().craftNoteBlock(this.block.getBlockData());

            this.block.setBlockData(noteBlock);
            this.customBlockData.getSoundGroup().playPlaceSound(this.block.getLocation().toCenterLocation());
            player.swingHand(hand);
            MSBlock.getCoreProtectAPI().logPlacement(player.getName(), this.block.getLocation(), Material.NOTE_BLOCK, noteBlock);
            BlockUtils.removeBlocksAround(this.block);

            if (player.getGameMode() == GameMode.SURVIVAL) {
                ItemStack itemInHand = player.getInventory().getItem(hand);
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            }
        });
    }

    public void breakCustomBlock(@NotNull Player player) {
        CustomBlockBreakEvent event = new CustomBlockBreakEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        Location blockLocation = this.block.getLocation();
        World world = this.block.getWorld();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        Material mainHandMaterial = itemInMainHand.getType();

        MSBlock.getCache().diggingMap.removeAll(this.block);

        CraftBlock craftBlock = (CraftBlock) this.block;
        LevelAccessor levelAccessor = craftBlock.getHandle();
        BlockState blockState = craftBlock.getNMS();

        levelAccessor.levelEvent(
                2001,
                craftBlock.getPosition(),
                net.minecraft.world.level.block.Block.getId(blockState)
        );
        this.customBlockData.getSoundGroup().playBreakSound(this.block.getLocation().toCenterLocation());
        MSBlock.getCoreProtectAPI().logRemoval(player.getName(), blockLocation, Material.NOTE_BLOCK, this.block.getBlockData());
        this.block.setType(Material.AIR);

        if (
                (!this.customBlockData.isForceTool() || this.customBlockData.getToolType() == ToolType.fromMaterial(mainHandMaterial))
                 && this.customBlockData != CustomBlockData.DEFAULT
        ) {
            if (this.customBlockData.isDropsDefaultItem()) {
                world.dropItemNaturally(blockLocation, this.customBlockData.craftItemStack());
            }

            if (this.customBlockData.getExpToDrop() != 0) {
                world.spawn(blockLocation, ExperienceOrb.class).setExperience(this.customBlockData.getExpToDrop());
            }
        } else if (this.customBlockData == CustomBlockData.DEFAULT) {
            world.dropItemNaturally(blockLocation, new ItemStack(Material.NOTE_BLOCK));
        }

        if (ToolType.fromMaterial(mainHandMaterial) != ToolType.HAND) {
            ItemUtils.damageItem(player, itemInMainHand);
        }
    }

    public @NotNull Block getReplacedBlock() {
        return this.block;
    }

    public @NotNull CustomBlockData getCustomBlockData() {
        return this.customBlockData;
    }

    @Override
    public @NotNull CustomBlock clone() {
        try {
            return (CustomBlock) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
