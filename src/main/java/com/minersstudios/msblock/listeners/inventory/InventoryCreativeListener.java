package com.minersstudios.msblock.listeners.inventory;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.PlayerUtils;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryCreativeListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryCreative(@NotNull InventoryCreativeEvent event) {
        if (!event.getClick().isCreativeAction()) return;

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
