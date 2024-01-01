package com.minersstudios.mscustoms.listener.event.inventory;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.mscustoms.custom.item.CustomItem;
import com.minersstudios.mscustoms.custom.item.Wearable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

@EventListener
public final class InventoryClickListener extends AbstractEventListener<MSCustoms> {
    private static final Set<InventoryType> IGNORABLE_INVENTORY_TYPES = EnumSet.of(
            //<editor-fold desc="Ignorable inventory types" defaultstate="collapsed">
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
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final ItemStack currentItem = event.getCurrentItem();
        final boolean isShiftClick = event.isShiftClick();

        if (
                IGNORABLE_INVENTORY_TYPES.contains(event.getInventory().getType())
                && isShiftClick
                && CustomBlockRegistry.isCustomBlock(currentItem)
        ) {
            event.setCancelled(true);
        }

        final Player player = (Player) event.getWhoClicked();
        final PlayerInventory inventory = player.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final ItemStack cursorItem = event.getCursor();

        if (
                event.getSlot() == 39
                && event.getSlotType() == InventoryType.SlotType.ARMOR
                && !cursorItem.getType().isAir()
        ) {
            CustomItem.fromItemStack(currentItem, Wearable.class)
            .ifPresent(w -> {
                assert currentItem != null;

                if (currentItem.getEnchantments().containsKey(Enchantment.BINDING_CURSE)) {
                    return;
                }

                this.getPlugin().runTask(() -> {
                    inventory.setHelmet(cursorItem);
                    player.setItemOnCursor(currentItem);
                });
            });
        }

        if (
                clickedInventory != null
                && currentItem != null
                && isShiftClick
                && clickedInventory.getType() == InventoryType.PLAYER
                && player.getOpenInventory().getType() == InventoryType.CRAFTING
                && inventory.getHelmet() == null
        ) {
            CustomItem.fromItemStack(currentItem, Wearable.class)
            .ifPresent(w -> {
                event.setCancelled(true);
                this.getPlugin().runTask(() -> {
                    inventory.setHelmet(currentItem);
                    currentItem.setAmount(0);
                });
            });
        }
    }
}
