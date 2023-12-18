package com.minersstudios.msblock.api;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.file.BlockSettings;
import com.minersstudios.msblock.api.file.PlacingType;
import com.minersstudios.msblock.api.file.ToolType;
import com.minersstudios.msblock.event.CustomBlockBreakEvent;
import com.minersstudios.msblock.event.CustomBlockPlaceEvent;
import com.minersstudios.mscore.utility.BlockUtils;
import com.minersstudios.mscore.utility.CoreProtectUtils;
import com.minersstudios.mscore.utility.ItemUtils;
import net.coreprotect.CoreProtectAPI;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_20_R2.block.CraftBlock;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom block which is a block with a custom block data
 *
 * @see CustomBlockData
 * @see #place(MSBlock, Player, EquipmentSlot, BlockFace, Axis)
 * @see #destroy(MSBlock, Player)
 */
public final class CustomBlock {
    private final Block block;
    private final CustomBlockData customBlockData;

    /**
     * Custom block constructor which takes the block and custom block data.
     *
     * @param block           The block which is a custom block,
     *                        or will be a custom block after placing
     * @param customBlockData The custom block data of this custom block
     */
    public CustomBlock(
            final @NotNull Block block,
            final @NotNull CustomBlockData customBlockData
    ) {
        this.block = block;
        this.customBlockData = customBlockData;
    }

    /**
     * @return The block which is a custom block,
     *         or will be a custom block after placing
     */
    public @NotNull Block getBlock() {
        return this.block;
    }

    /**
     * @return The custom block data of this custom block
     */
    public @NotNull CustomBlockData getData() {
        return this.customBlockData;
    }

    /**
     * Places the custom block and calls the {@link CustomBlockPlaceEvent},
     * if the event is cancelled the block won't be placed. Otherwise,
     * block will be placed with the specified parameters. Also, logs block
     * place in the CoreProtect and plays the place sound.
     *
     * @param plugin The plugin that owns this custom block
     * @param player The player who broke the block
     * @param hand   The hand the player used to break the block
     * @see CustomBlockPlaceEvent
     * @see #place(MSBlock, Player, EquipmentSlot, BlockFace, Axis)
     */
    public void place(
            final @NotNull MSBlock plugin,
            final @NotNull Player player,
            final @NotNull EquipmentSlot hand
    ) {
        this.place(plugin, player, hand, null, null);
    }

    /**
     * Places the custom block and calls the {@link CustomBlockPlaceEvent},
     * if the event is cancelled the block won't be placed. Otherwise, the
     * block will be placed with the specified parameters. Also, logs the
     * block place in the CoreProtect and plays the place sound.
     *
     * @param plugin    The plugin that owns this custom block
     * @param player    The player who placed the block
     * @param hand      The hand the player used to place the block
     * @param blockFace The block face the player placed the block on,
     *                  null if placing type is {@link PlacingType.Directional}
     * @param axis      The axis the player placed the block on,
     *                  null if placing type is {@link PlacingType.Orientable}
     * @see CustomBlockData
     * @see CustomBlockPlaceEvent
     * @see CoreProtectAPI#logPlacement(String, Location, Material, BlockData)
     * @see BlockUtils#removeBlocksAround(Block)
     */
    public void place(
            final @NotNull MSBlock plugin,
            final @NotNull Player player,
            final @NotNull EquipmentSlot hand,
            final @Nullable BlockFace blockFace,
            final @Nullable Axis axis
    ) throws IllegalArgumentException {
        final CustomBlockPlaceEvent event = new CustomBlockPlaceEvent(this, this.block.getState(), player, hand);
        player.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        plugin.runTask(() -> {
            this.block.setType(Material.NOTE_BLOCK);

            final String key = this.customBlockData.getKey();
            final BlockData blockData = this.block.getBlockData();
            final NoteBlock noteBlock;
            final PlacingType placingType = this.customBlockData.getBlockSettings().getPlacing().type();

            if (placingType instanceof final PlacingType.Default normal) {
                noteBlock = normal.getNoteBlockData().craftNoteBlock(blockData);
            } else if (placingType instanceof final PlacingType.Directional directional) {
                if (blockFace == null) {
                    throw new IllegalArgumentException("Block face is null, but placing type is directional! " + key);
                }

                noteBlock = directional.getNoteBlockData(blockFace).craftNoteBlock(blockData);
            } else if (placingType instanceof final PlacingType.Orientable orientable) {
                if (axis == null) {
                    throw new IllegalArgumentException("Axis is null, but placing type is orientable! " + key);
                }

                noteBlock = orientable.getNoteBlockData(axis).craftNoteBlock(blockData);
            } else {
                throw new IllegalArgumentException("Unknown placing type: " + placingType.getClass().getName());
            }

            this.block.setBlockData(noteBlock);
            this.customBlockData.getSoundGroup().playPlaceSound(this.block.getLocation().toCenterLocation());
            player.swingHand(hand);
            CoreProtectUtils.logPlacement(player.getName(), this.block.getLocation(), Material.NOTE_BLOCK, noteBlock);
            BlockUtils.removeBlocksAround(this.block);

            if (player.getGameMode() == GameMode.SURVIVAL) {
                final ItemStack itemInHand = player.getInventory().getItem(hand);
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            }
        });
    }

    /**
     * Breaks the custom block and calls the {@link CustomBlockBreakEvent},
     * if the event is cancelled, the block will not be broken. Otherwise,
     * drops the item if the player is holding the correct tool, or if
     * the custom block has force tool set to false. Also drops experience
     * if the custom block has exp to drop set to a value greater than 0.
     * Also plays the break sound and logs the break to CoreProtect.
     *
     * @param plugin The plugin that owns this custom block
     * @param player The player who broke the block
     */
    public void destroy(
            final @NotNull MSBlock plugin,
            final @NotNull Player player
    ) {
        final CustomBlockBreakEvent event = new CustomBlockBreakEvent(this, player);
        player.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        final Location blockLocation = this.block.getLocation();
        final World world = this.block.getWorld();
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        final Material mainHandMaterial = itemInMainHand.getType();

        plugin.getCache().getDiggingMap().removeAll(this.block);

        final CraftBlock craftBlock = (CraftBlock) this.block;
        final LevelAccessor levelAccessor = craftBlock.getHandle();
        final BlockState blockState = craftBlock.getNMS();

        levelAccessor.levelEvent(
                2001,
                craftBlock.getPosition(),
                net.minecraft.world.level.block.Block.getId(blockState)
        );
        this.customBlockData.getSoundGroup().playBreakSound(this.block.getLocation().toCenterLocation());
        CoreProtectUtils.logRemoval(player.getName(), blockLocation, Material.NOTE_BLOCK, this.block.getBlockData());
        this.block.setType(Material.AIR);

        final BlockSettings.Tool tool = this.customBlockData.getBlockSettings().getTool();
        final int experience = this.customBlockData.getDropSettings().getExperience();

        if (
                !tool.force()
                || tool.type() == ToolType.fromMaterial(mainHandMaterial)
        ) {
            world.dropItemNaturally(blockLocation, this.customBlockData.craftItemStack());

            if (experience != 0) {
                world.spawn(blockLocation, ExperienceOrb.class).setExperience(experience);
            }
        }

        if (ToolType.fromMaterial(mainHandMaterial) != ToolType.HAND) {
            ItemUtils.damageItem(player, itemInMainHand);
        }
    }
}
