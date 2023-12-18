package com.minersstudios.msitem.listener.event.mechanic;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.api.CustomItemType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class CocaineMechanic extends AbstractEventListener<MSItem> {

    @EventHandler
    public void onInventoryClick(final @NotNull PlayerItemConsumeEvent event) {
        final ItemStack itemStack = event.getItem();

        if (
                !(itemStack.getItemMeta() instanceof PotionMeta)
                || CustomItemType.fromItemStack(itemStack) != CustomItemType.COCAINE
        ) {
            return;
        }

        this.getPlugin().runTask(
                () -> event.getPlayer().getInventory().getItem(event.getHand()).setAmount(0)
        );
    }
}
