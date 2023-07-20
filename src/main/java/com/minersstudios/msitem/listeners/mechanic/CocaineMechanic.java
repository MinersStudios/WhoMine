package com.minersstudios.msitem.listeners.mechanic;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.MSItemUtils;
import com.minersstudios.msitem.items.register.items.Cocaine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

@MSListener
public class CocaineMechanic extends AbstractMSListener {

    @EventHandler
    public void onInventoryClick(@NotNull PlayerItemConsumeEvent event) {
        ItemStack itemStack = event.getItem();

        if (
                !(itemStack.getItemMeta() instanceof PotionMeta)
                || !(MSItemUtils.getCustomItem(itemStack) instanceof Cocaine)
        ) return;

        this.getPlugin().runTask(
                () -> event.getPlayer().getInventory().getItem(event.getHand()).setAmount(0)
        );
    }
}
