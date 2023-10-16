package com.minersstudios.msdecor.listeners.event.inventory;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msdecor.customdecor.DecorHitBox;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryCreativeListener extends AbstractMSListener {

    @EventHandler
    public void onInventoryCreative(final @NotNull InventoryCreativeEvent event) {
        if (event.getClick() != ClickType.CREATIVE) return;

        final HumanEntity player = event.getWhoClicked();
        final Block clickedBlock = player.getTargetBlockExact(5);

        if (
                clickedBlock != null
                && (event.getCursor().getType() == Material.BARRIER || event.getCursor().getType() == Material.STRUCTURE_VOID)
        ) {
            DecorHitBox.Elements.fromBlock(clickedBlock)
            .ifPresent(
                    elements -> {
                        event.setCancelled(true);
                        player.getInventory().setItem(event.getSlot(), elements.getDisplay().getItemStack());
                    }
            );
        }
    }
}
