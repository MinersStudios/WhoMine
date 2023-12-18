package com.minersstudios.msdecor.listeners.event.inventory;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.api.CustomDecor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class InventoryCreativeListener extends AbstractMSListener<MSDecor> {

    @EventHandler
    public void onInventoryCreative(final @NotNull InventoryCreativeEvent event) {
        if (event.getClick() != ClickType.CREATIVE) {
            return;
        }

        final HumanEntity player = event.getWhoClicked();
        final Block clickedBlock = player.getTargetBlockExact(5);
        final Material cursorType = event.getCursor().getType();

        if (
                clickedBlock != null
                && (
                        cursorType == Material.BARRIER
                        || cursorType == Material.STRUCTURE_VOID
                )
        ) {
            CustomDecor.fromBlock(clickedBlock)
            .ifPresent(
                    customDecor -> {
                        event.setCancelled(true);
                        player.getInventory().setItem(
                                event.getSlot(),
                                customDecor.getDisplay().getItemStack()
                        );
                    }
            );
        }
    }
}
