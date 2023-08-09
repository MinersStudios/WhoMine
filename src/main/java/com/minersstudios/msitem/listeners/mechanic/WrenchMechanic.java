package com.minersstudios.msitem.listeners.mechanic;

import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.ItemUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msdecor.customdecor.Sittable;
import com.minersstudios.msdecor.customdecor.Typed;
import com.minersstudios.msdecor.customdecor.Wrenchable;
import com.minersstudios.msdecor.utils.CustomDecorUtils;
import com.minersstudios.msitem.item.CustomItemType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MSListener
public class WrenchMechanic extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (
                event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getClickedBlock() == null
                || event.getHand() == null
        ) return;

        Block clickedBlock = event.getClickedBlock();
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        Location location = clickedBlock.getLocation().toCenterLocation();

        if (
                event.getHand() == EquipmentSlot.HAND
                && CustomItemType.typeOf(itemInMainHand) == CustomItemType.WRENCH
        ) {
            event.setCancelled(Tag.DIRT.isTagged(clickedBlock.getType()));

            if (
                    MSDecorUtils.isCustomDecorMaterial(clickedBlock.getType())
                    && CustomDecorUtils.getCustomDecorDataByLocation(location).orElse(null) instanceof Wrenchable wrenchable
            ) {
                if (wrenchable instanceof Sittable && !player.isSneaking()) return;

                for (var nearbyEntity : clickedBlock.getWorld().getNearbyEntities(location, 0.5d, 0.5d, 0.5d)) {
                    if (nearbyEntity instanceof ItemFrame itemFrame) {
                        use(player, itemInMainHand, location, itemFrame, wrenchable);
                        break;
                    }
                }

                for (var nearbyEntity : clickedBlock.getWorld().getNearbyEntities(clickedBlock.getLocation().clone().add(0.5d, 0.0d, 0.5d), 0.2d, 0.3d, 0.2d)) {
                    if (nearbyEntity instanceof ArmorStand armorStand) {
                        use(player, itemInMainHand, location, armorStand, wrenchable);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(@NotNull PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();

        if (!MSDecorUtils.isCustomDecorEntity(entity)) return;

        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (
                event.getHand() == EquipmentSlot.HAND
                && CustomItemType.typeOf(itemInMainHand) == CustomItemType.WRENCH
                && CustomDecorUtils.getCustomDecorDataByEntity(entity).orElse(null) instanceof Wrenchable wrenchable
        ) {
            use(player, itemInMainHand, entity.getLocation(), entity, wrenchable);
        }
    }

    private static void use(
            @NotNull Player player,
            @NotNull ItemStack itemInMainHand,
            @NotNull Location location,
            @Nullable Entity entity,
            @NotNull Wrenchable wrenchable
    ) {
        Typed.Type type = wrenchable.getNextType(wrenchable.getType(wrenchable.getItemStack()));
        if (type == null) return;
        ItemStack customItem = wrenchable.createItemStack(type);

        if (entity instanceof ItemFrame itemFrame) {
            ItemStack itemStack = itemFrame.getItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setCustomModelData(customItem.getItemMeta().getCustomModelData());
            itemMeta.getPersistentDataContainer().set(
                    MSDecorUtils.CUSTOM_DECOR_TYPE_NAMESPACED_KEY,
                    PersistentDataType.STRING,
                    type.getNamespacedKey().getKey()
            );
            itemStack.setItemMeta(itemMeta);
            itemFrame.setItem(itemStack);
            itemFrame.customName(ChatUtils.createDefaultStyledText(type.getItemName()));
        } else if (entity instanceof ArmorStand armorStand) {
            ItemStack itemStack = armorStand.getEquipment().getHelmet();
            ItemMeta itemMeta = itemStack.getItemMeta();
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
}
