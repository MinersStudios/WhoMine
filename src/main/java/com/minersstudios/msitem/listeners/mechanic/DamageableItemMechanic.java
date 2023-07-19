package com.minersstudios.msitem.listeners.mechanic;

import com.minersstudios.mscore.listener.MSListener;
import com.minersstudios.mscore.utils.ItemUtils;
import com.minersstudios.msitem.items.DamageableItem;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class DamageableItemMechanic extends AbstractMSListener {

    @EventHandler
    public void onPlayerItemDamage(@NotNull PlayerItemDamageEvent event) {
        ItemStack itemStack = event.getItem();

        if (DamageableItem.fromItemStack(itemStack) != null) {
            event.setCancelled(true);
            ItemUtils.damageItem(event.getPlayer(), event.getItem(), event.getDamage());
        }
    }
}
