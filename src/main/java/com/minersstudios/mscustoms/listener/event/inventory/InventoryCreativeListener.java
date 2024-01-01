package com.minersstudios.mscustoms.listener.event.inventory;

import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlockData;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.utility.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class InventoryCreativeListener extends AbstractEventListener<MSCustoms> {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryCreative(final @NotNull InventoryCreativeEvent event) {
        if (!event.getClick().isCreativeAction()) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        final Block targetBlock = PlayerUtils.getTargetBlock(player);

        if (
                targetBlock == null
                || event.getCursor().getType() != Material.NOTE_BLOCK
                || !(targetBlock.getBlockData() instanceof final NoteBlock noteBlock)
        ) {
            return;
        }

        event.setCancelled(true);
        this.getPlugin().runTask(() ->
                player.getInventory().setItem(
                        event.getSlot(),
                        CustomBlockRegistry
                        .fromNoteBlock(noteBlock)
                        .orElse(CustomBlockData.defaultData())
                        .craftItemStack()
                )
        );
    }
}
