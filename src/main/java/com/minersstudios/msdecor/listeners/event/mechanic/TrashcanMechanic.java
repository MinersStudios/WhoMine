package com.minersstudios.msdecor.listeners.event.mechanic;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class TrashcanMechanic extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        if (
                event.getClickedBlock() == null
                || event.getHand() == null
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) return;

        final Player player = event.getPlayer();
        final Block clickedBlock = event.getClickedBlock();
        final Location location = clickedBlock.getLocation().toCenterLocation();
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        //TODO
        /*
        if (
                MSDecorUtils.isCustomDecorMaterial(clickedBlock.getType())
                && (!itemInMainHand.getType().isBlock() || itemInMainHand.getType() == Material.AIR)
                && MSDecorUtils.getCustomDecorDataByLocation(location).orElse(null) instanceof IronTrashcan
        ) {
            event.setCancelled(true);
            player.swingMainHand();
            player.openInventory(Bukkit.createInventory(null, 4 * 9, IronTrashcan.INV_NAME));
            player.getWorld().playSound(location, Sound.BLOCK_BARREL_OPEN, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }

         */
    }

    @EventHandler
    public void onInventoryClose(final @NotNull InventoryCloseEvent event) {
        final HumanEntity player = event.getPlayer();

        //TODO
        /*
        if (event.getView().title().contains(IronTrashcan.INV_NAME)) {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
         */
    }
}
