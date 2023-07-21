package com.minersstudios.msdecor.listeners.mechanic;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.utils.MSDecorUtils;
import com.minersstudios.msdecor.customdecor.register.decorations.street.IronTrashcan;
import com.minersstudios.msdecor.utils.CustomDecorUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class TrashcanMechanic extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (
                event.getClickedBlock() == null
                || event.getHand() == null
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) return;

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Location location = clickedBlock.getLocation().toCenterLocation();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (
                MSDecorUtils.isCustomDecorMaterial(clickedBlock.getType())
                && (!itemInMainHand.getType().isBlock() || itemInMainHand.getType() == Material.AIR)
                && CustomDecorUtils.getCustomDecorDataByLocation(location).orElse(null) instanceof IronTrashcan
        ) {
            event.setCancelled(true);
            player.swingMainHand();
            player.openInventory(Bukkit.createInventory(null, 4 * 9, IronTrashcan.INV_NAME));
            player.getWorld().playSound(location, Sound.BLOCK_BARREL_OPEN, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();
        if (event.getView().title().contains(IronTrashcan.INV_NAME)) {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }
}
