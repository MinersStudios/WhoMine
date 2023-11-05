package com.minersstudios.msdecor.registry.other;

import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.api.CustomDecorDataImpl;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msdecor.api.Facing;
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
                .key("poop")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .size(0.5d, 0.5d, 0.5d)
                        .build()
                )
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.MUD)
                .itemStack(itemStack);
    }
}
