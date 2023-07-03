package com.github.minersstudios.msessentials.listeners.inventory;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@MSListener
public class InventoryClickListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        int slot = event.getSlot();
        ItemStack cursorItem = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();

        if (clickedInventory == null) return;

        if (player.getWorld().equals(MSEssentials.getWorldDark())) {
            event.setCancelled(true);
        }

        if (
                slot == 39
                && event.getSlotType() == InventoryType.SlotType.ARMOR
                && cursorItem != null
                && currentItem != null
                && currentItem.getType() == Material.AIR
                && cursorItem.getType() != Material.AIR
        ) {
            player.setItemOnCursor(null);
            Bukkit.getScheduler().runTask(MSEssentials.getInstance(), () -> player.getInventory().setHelmet(cursorItem));
        }

        if (currentItem != null && currentItem.getType() != Material.AIR) {
            boolean remove = currentItem.getType() == Material.BEDROCK;

            if (!remove) {
                for (var enchantment : currentItem.getEnchantments().keySet()) {
                    remove = currentItem.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel();
                }
            }

            if (remove) {
                clickedInventory.setItem(slot, new ItemStack(Material.AIR));
                ChatUtils.sendWarning(
                        Component.translatable(
                                "ms.info.player_item_removed",
                                text(player.getName()),
                                text(currentItem.toString())
                        )
                );
                event.setCancelled(true);
            }
        }
    }
}
