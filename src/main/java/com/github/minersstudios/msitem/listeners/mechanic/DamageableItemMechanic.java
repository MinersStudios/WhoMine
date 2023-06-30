package com.github.minersstudios.msitem.listeners.mechanic;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.ItemUtils;
import com.github.minersstudios.msitem.items.DamageableItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class DamageableItemMechanic implements Listener {

    @EventHandler
    public void onPlayerItemDamage(@NotNull PlayerItemDamageEvent event) {
        ItemStack itemStack = event.getItem();

        if (DamageableItem.fromItemStack(itemStack) != null) {
            event.setCancelled(true);
            ItemUtils.damageItem(event.getPlayer(), event.getItem(), event.getDamage());
        }
    }
}
