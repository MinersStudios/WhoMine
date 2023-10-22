package com.minersstudios.msdecor.listeners.event.mechanic;

import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.customdecor.CustomDecorType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PoopMechanic extends AbstractMSListener<MSDecor> {

    @EventHandler
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        if (
                event.getClickedBlock() == null
                || event.getHand() == null
                || event.getAction().isLeftClick()
        ) return;

        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock.getType() != Material.COMPOSTER) return;

        final Player player = event.getPlayer();
        final GameMode gameMode = player.getGameMode();
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        EquipmentSlot hand = event.getHand();

        if (CustomBlockRegistry.isCustomBlock(itemInMainHand)) return;
        if (hand != EquipmentSlot.HAND && MSDecorUtils.isCustomDecor(itemInMainHand)) {
            hand = EquipmentSlot.HAND;
        }

        final ItemStack itemInHand = player.getInventory().getItem(hand);

        if (
                event.getHand() == EquipmentSlot.HAND
                && gameMode != GameMode.SPECTATOR
                && !player.isSneaking()
                && clickedBlock.getBlockData() instanceof final Levelled levelled
                && (!itemInHand.getType().isBlock() || itemInHand.getType() == Material.AIR)
                && levelled.getLevel() < levelled.getMaximumLevel()
        ) {

            final var optional = CustomDecorType.fromItemStack(itemInHand);

            //TODO
            /*
            if (
                    optional.isEmpty()
                    || !(optional.get() instanceof Poop)
            ) return;

            levelled.setLevel(levelled.getLevel() + 1);
            clickedBlock.setBlockData(levelled);
            player.swingHand(hand);

            if (gameMode != GameMode.CREATIVE) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            }
             */
        }
    }
}
