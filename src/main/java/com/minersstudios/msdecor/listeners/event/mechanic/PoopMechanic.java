package com.minersstudios.msdecor.listeners.event.mechanic;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.api.CustomDecorType;
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

@MSEventListener
public final class PoopMechanic extends AbstractMSListener<MSDecor> {

    @EventHandler
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        if (
                event.getClickedBlock() == null
                || event.getHand() == null
                || event.getAction().isLeftClick()
        ) {
            return;
        }

        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock.getType() != Material.COMPOSTER) {
            return;
        }

        final Player player = event.getPlayer();
        final EquipmentSlot hand = event.getHand();
        final ItemStack itemInHand = player.getInventory().getItem(hand);
        final GameMode gameMode = player.getGameMode();
        final Material handType = itemInHand.getType();

        if (
                gameMode != GameMode.SPECTATOR
                && !player.isSneaking()
                && clickedBlock.getBlockData() instanceof final Levelled levelled
                && (
                        !handType.isBlock()
                        || handType == Material.AIR
                )
                && levelled.getLevel() < levelled.getMaximumLevel()
                && CustomDecorType.fromItemStack(itemInHand) == CustomDecorType.POOP
        ) {
            levelled.setLevel(levelled.getLevel() + 1);
            clickedBlock.setBlockData(levelled);
            player.swingHand(hand);

            if (gameMode != GameMode.CREATIVE) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            }
        }
    }
}
