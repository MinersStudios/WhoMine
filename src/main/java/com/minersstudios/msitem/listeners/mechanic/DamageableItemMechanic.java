package com.minersstudios.msitem.listeners.mechanic;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.ItemUtils;
import com.minersstudios.msitem.item.damageable.DamageableItem;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class DamageableItemMechanic extends AbstractMSListener {

    @EventHandler
    public void onPlayerItemDamage(final @NotNull PlayerItemDamageEvent event) {
        final ItemStack itemStack = event.getItem();

        if (DamageableItem.fromItemStack(itemStack) != null) {
            event.setCancelled(true);
            ItemUtils.damageItem(event.getPlayer(), event.getItem(), event.getDamage());
        }
    }
}
