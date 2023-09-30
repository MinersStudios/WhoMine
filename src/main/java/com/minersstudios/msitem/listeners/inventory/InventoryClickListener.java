package com.minersstudios.msitem.listeners.inventory;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msitem.item.CustomItemType;
import com.minersstudios.msitem.item.Wearable;
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

@MSListener
public class InventoryClickListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final PlayerInventory inventory = player.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final ItemStack cursorItem = event.getCursor();
        final ItemStack currentItem = event.getCurrentItem();

        if (
                event.getSlot() == 39
                && event.getSlotType() == InventoryType.SlotType.ARMOR
                && !cursorItem.getType().isAir()
        ) {
            CustomItemType.fromItemStack(currentItem, Wearable.class)
            .ifPresent(w -> {
                assert currentItem != null;
                if (currentItem.getEnchantments().containsKey(Enchantment.BINDING_CURSE)) return;

                this.getPlugin().runTask(() -> {
                    inventory.setHelmet(cursorItem);
                    player.setItemOnCursor(currentItem);
                });
            });
        }

        if (
                clickedInventory != null
                && currentItem != null
                && event.isShiftClick()
                && clickedInventory.getType() == InventoryType.PLAYER
                && player.getOpenInventory().getType() == InventoryType.CRAFTING
                && inventory.getHelmet() == null
        ) {
            CustomItemType.fromItemStack(currentItem, Wearable.class)
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
