package com.github.minersstudios.msblock.listeners.player;

import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.MSBlockUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.github.minersstudios.mscore.listener.AbstractMSListener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PlayerBucketEmptyListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketEmpty(@NotNull PlayerBucketEmptyEvent event) {
        if (
                event.getBlock().getType() == Material.NOTE_BLOCK
                || MSBlockUtils.isCustomBlock(event.getPlayer().getInventory().getItemInMainHand())
        ) {
            event.setCancelled(true);
        }
    }
}
