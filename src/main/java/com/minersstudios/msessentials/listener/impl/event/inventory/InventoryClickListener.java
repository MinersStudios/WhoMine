package com.minersstudios.msessentials.listener.impl.event.inventory;

import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@EventListener
public final class InventoryClickListener extends AbstractEventListener<MSEssentials> {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory clickedInventory = event.getClickedInventory();
        final int slot = event.getSlot();
        final ItemStack cursorItem = event.getCursor();
        final ItemStack currentItem = event.getCurrentItem();

        if (clickedInventory == null) {
            return;
        }

        if (WorldDark.isInWorldDark(player)) {
            event.setCancelled(true);
        }

        if (
                slot == 39
                && event.getSlotType() == InventoryType.SlotType.ARMOR
                && currentItem != null
                && currentItem.getType() == Material.AIR
                && cursorItem.getType() != Material.AIR
        ) {
            player.setItemOnCursor(null);
            this.getPlugin().runTask(() -> player.getInventory().setHelmet(cursorItem));
        }

        if (currentItem != null && currentItem.getType() != Material.AIR) {
            boolean remove = currentItem.getType() == Material.BEDROCK;

            if (!remove) {
                for (final var enchantment : currentItem.getEnchantments().keySet()) {
                    remove = currentItem.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel();
                }
            }

            if (remove) {
                clickedInventory.setItem(slot, new ItemStack(Material.AIR));
                MSLogger.warning(
                        LanguageRegistry.Components.INFO_PLAYER_ITEM_REMOVED
                        .args(
                                player.name(),
                                text(currentItem.toString())
                        )
                );
                event.setCancelled(true);
            }
        }
    }
}
