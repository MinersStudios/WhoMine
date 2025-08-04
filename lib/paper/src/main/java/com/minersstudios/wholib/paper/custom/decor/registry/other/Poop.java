package com.minersstudios.wholib.paper.custom.decor.registry.other;

import com.minersstudios.wholib.utility.ChatUtils;
import com.minersstudios.wholib.paper.world.sound.SoundGroup;
import com.minersstudios.wholib.paper.custom.decor.CustomDecorDataImpl;
import com.minersstudios.wholib.paper.custom.decor.DecorHitBox;
import com.minersstudios.wholib.paper.custom.decor.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class Poop extends CustomDecorDataImpl<Poop> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1210);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Какашка"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .path("poop")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .size(0.5d, 0.5d, 0.5d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.MUD)
                .itemStack(itemStack);
    }
}
