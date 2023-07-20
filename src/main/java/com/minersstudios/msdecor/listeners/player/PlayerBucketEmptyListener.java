package com.minersstudios.msdecor.listeners.player;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.MSDecorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerBucketEmptyListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketEmpty(@NotNull PlayerBucketEmptyEvent event) {
        if (
                MSDecorUtils.isCustomDecorMaterial(event.getBlock().getType())
                || MSDecorUtils.isCustomDecor(event.getPlayer().getInventory().getItemInMainHand())
        ) {
            event.setCancelled(true);
        }
    }
}
