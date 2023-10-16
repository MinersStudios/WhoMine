package com.minersstudios.msdecor.listeners.event.mechanic;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class BrazierMechanic extends AbstractMSListener {

    @EventHandler
    public void onPlayerInteractAtEntity(final @NotNull PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof final ArmorStand armorStand)) return;

        final Player player = event.getPlayer();
        final EquipmentSlot equipmentSlot = event.getHand();
        final ItemStack heldItem = player.getInventory().getItem(equipmentSlot);
        final ItemStack helmet = armorStand.getEquipment().getHelmet();
        final Block block = armorStand.getLocation().getBlock();

        //TODO
        /*
        if (
                (heldItem.getType() != Material.FLINT_AND_STEEL
                && !heldItem.getType().toString().endsWith("_SHOVEL"))
                || !(MSDecorUtils.getCustomDecorData(helmet).orElse(null) instanceof final Brazier brazier)
                || !(block.getBlockData() instanceof final Levelled levelled)
                || !(heldItem.getItemMeta() instanceof final Damageable itemMeta)
        ) return;

        final Typed.Type type = brazier.getType(helmet);

        if (
                type == Brazier.Type.DEFAULT
                && heldItem.getType() == Material.FLINT_AND_STEEL
        ) {
            armorStand.getEquipment().setHelmet(brazier.createItemStack(Brazier.Type.FIRED));
            block.getWorld().playSound(armorStand.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        } else if (
                type == Brazier.Type.FIRED
                && heldItem.getType().toString().endsWith("_SHOVEL")
        ) {
            armorStand.getEquipment().setHelmet(brazier.createItemStack(Brazier.Type.DEFAULT));
            player.getWorld().playSound(armorStand.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
        } else {
            return;
        }

        levelled.setLevel(((Typed.LightableType) type).getSecondLightLevel());
        block.setType(Material.LIGHT);
        block.setBlockData(levelled, true);
        heldItem.setItemMeta(itemMeta);
        player.swingHand(equipmentSlot);

        if (player.getGameMode() == GameMode.SURVIVAL) {
            itemMeta.setDamage(itemMeta.getDamage() + 1);
        }
         */
    }
}
