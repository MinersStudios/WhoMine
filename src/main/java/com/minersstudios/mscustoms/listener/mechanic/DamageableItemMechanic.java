package com.minersstudios.mscustoms.listener.mechanic;

import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.utility.ItemUtils;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.item.damageable.DamageableItem;
import org.bukkit.event.EventHandler;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class DamageableItemMechanic extends AbstractEventListener<MSCustoms> {

    @EventHandler
    public void onPlayerItemDamage(final @NotNull PlayerItemDamageEvent event) {
        final ItemStack itemStack = event.getItem();

        if (DamageableItem.fromItemStack(itemStack) != null) {
            event.setCancelled(true);
            ItemUtils.damageItem(event.getPlayer(), event.getItem(), event.getDamage());
        }
    }
}
