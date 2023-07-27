package com.minersstudios.msblock.listeners.inventory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSBlockUtils;
import com.minersstudios.mscore.util.PlayerUtils;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryClickListener extends AbstractMSListener {
    private static final ImmutableSet<InventoryType> IGNORABLE_INVENTORY_TYPES = Sets.immutableEnumSet(
            //<editor-fold desc="Ignorable inventory types">
            InventoryType.CARTOGRAPHY,
            InventoryType.BREWING,
            InventoryType.BEACON,
            InventoryType.BLAST_FURNACE,
            InventoryType.FURNACE,
            InventoryType.SMOKER,
            InventoryType.GRINDSTONE,
            InventoryType.STONECUTTER,
            InventoryType.SMITHING,
            InventoryType.LOOM,
            InventoryType.MERCHANT,
            InventoryType.ENCHANTING
            //</editor-fold>
    );

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        ClickType clickType = event.getClick();

        if (
                IGNORABLE_INVENTORY_TYPES.contains(event.getInventory().getType())
                && clickType.isShiftClick()
                && MSBlockUtils.isCustomBlock(event.getCurrentItem())
        ) {
            event.setCancelled(true);
        }

        if (!clickType.isCreativeAction()) return;

        Player player = (Player) event.getWhoClicked();
        Block targetBlock = PlayerUtils.getTargetBlock(player);

        if (
                targetBlock == null
                || !(targetBlock.getBlockData() instanceof NoteBlock noteBlock)
        ) return;

        event.setCancelled(true);
        this.getPlugin().runTask(() -> player.getInventory().setItem(
                event.getSlot(),
                CustomBlockData.fromNoteBlock(noteBlock).craftItemStack()
        ));
    }
}
