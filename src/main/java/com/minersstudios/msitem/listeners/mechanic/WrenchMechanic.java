package com.minersstudios.msitem.listeners.mechanic;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msitem.item.CustomItemType;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MSListener
public class WrenchMechanic extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        if (
                event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getClickedBlock() == null
                || event.getHand() == null
        ) return;

        final Block clickedBlock = event.getClickedBlock();
        final Player player = event.getPlayer();
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        final Location location = clickedBlock.getLocation().toCenterLocation();

        if (
                event.getHand() == EquipmentSlot.HAND
                && CustomItemType.fromItemStack(itemInMainHand) == CustomItemType.WRENCH
        ) {
            event.setCancelled(Tag.DIRT.isTagged(clickedBlock.getType()));

            //TODO
            /*
            if (
                    MSDecorUtils.isCustomDecorMaterial(clickedBlock.getType())
                    && MSDecorUtils.getCustomDecorDataByLocation(location).orElse(null) instanceof final Wrenchable wrenchable
            ) {
                if (wrenchable instanceof SittableDecorData && !player.isSneaking()) return;

                for (final var nearbyEntity : clickedBlock.getWorld().getNearbyEntities(location, 0.5d, 0.5d, 0.5d)) {
                    if (nearbyEntity instanceof final ItemFrame itemFrame) {
                        use(player, itemInMainHand, location, itemFrame, wrenchable);
                        break;
                    }
                }

                for (final var nearbyEntity : clickedBlock.getWorld().getNearbyEntities(clickedBlock.getLocation().clone().add(0.5d, 0.0d, 0.5d), 0.2d, 0.3d, 0.2d)) {
                    if (nearbyEntity instanceof final ArmorStand armorStand) {
                        use(player, itemInMainHand, location, armorStand, wrenchable);
                        break;
                    }
                }
            }
             */
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(final @NotNull PlayerInteractAtEntityEvent event) {
        final Entity entity = event.getRightClicked();

        if (!MSDecorUtils.isCustomDecor(entity)) return;

        final Player player = event.getPlayer();
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        //TODO
        /*
        if (
                event.getHand() == EquipmentSlot.HAND
                && CustomItemType.typeOf(itemInMainHand) == CustomItemType.WRENCH
                && MSDecorUtils.getCustomDecorDataByEntity(entity).orElse(null) instanceof final Wrenchable wrenchable
        ) {
            use(player, itemInMainHand, entity.getLocation(), entity, wrenchable);
        }
         */
    }

    //TODO
    /*
    private static void use(
            final @NotNull Player player,
            final @NotNull ItemStack itemInMainHand,
            final @NotNull Location location,
            final @Nullable Entity entity,
            final @NotNull CustomDecorData<?> decorData
    ) {
        final Typed.Type type = wrenchable.getNextType(wrenchable.getType(wrenchable.getItem()));
        if (type == null) return;
        final ItemStack customItem = wrenchable.createItemStack(type);

        if (entity instanceof final ItemFrame itemFrame) {
            final ItemStack itemStack = itemFrame.getItem();
            final ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setCustomModelData(customItem.getItemMeta().getCustomModelData());
            itemMeta.getPersistentDataContainer().set(
                    MSDecorUtils.CUSTOM_DECOR_TYPE_NAMESPACED_KEY,
                    PersistentDataType.STRING,
                    type.getNamespacedKey().getKey()
            );

            itemStack.setItemMeta(itemMeta);
            itemFrame.setItem(itemStack);
            itemFrame.customName(ChatUtils.createDefaultStyledText(type.getItemName()));
        } else if (entity instanceof final ArmorStand armorStand) {
            final ItemStack itemStack = armorStand.getEquipment().getHelmet();
            final ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setCustomModelData(customItem.getItemMeta().getCustomModelData());
            itemMeta.getPersistentDataContainer().set(
                    MSDecorUtils.CUSTOM_DECOR_TYPE_NAMESPACED_KEY,
                    PersistentDataType.STRING,
                    type.getNamespacedKey().getKey()
            );
            itemMeta.displayName(ChatUtils.createDefaultStyledText(type.getItemName()));

            itemStack.setItemMeta(itemMeta);
            armorStand.getEquipment().setHelmet(itemStack);
        }

        if (player.getGameMode() == GameMode.SURVIVAL) {
            ItemUtils.damageItem(player, itemInMainHand);
        }

        player.swingMainHand();
        player.getWorld().playSound(location, Sound.ITEM_SPYGLASS_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }
     */
}
