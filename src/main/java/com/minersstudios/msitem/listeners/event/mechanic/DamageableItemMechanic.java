package com.minersstudios.msitem.listeners.event.mechanic;

import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.mscore.util.ItemUtils;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.api.damageable.DamageableItem;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSEventListener
public final class DamageableItemMechanic extends AbstractMSListener<MSItem> {

    @EventHandler
    public void onPlayerItemDamage(final @NotNull PlayerItemDamageEvent event) {
        final ItemStack itemStack = event.getItem();

        if (DamageableItem.fromItemStack(itemStack) != null) {
            event.setCancelled(true);
            ItemUtils.damageItem(event.getPlayer(), event.getItem(), event.getDamage());
        }
    }
}
