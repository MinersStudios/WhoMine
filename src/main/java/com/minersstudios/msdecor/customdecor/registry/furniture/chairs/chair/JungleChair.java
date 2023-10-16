package com.minersstudios.msdecor.customdecor.registry.furniture.chairs.chair;

import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.customdecor.CustomDecorDataImpl;
import com.minersstudios.msdecor.customdecor.CustomDecorType;
import com.minersstudios.msdecor.customdecor.DecorHitBox;
import com.minersstudios.msdecor.customdecor.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public final class JungleChair extends CustomDecorDataImpl<JungleChair> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1012);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Тропический стул со спинкой"));
        itemStack.setItemMeta(itemMeta);

        final Builder builder = new Builder()
                .key("jungle_chair")
                .hitBox(new DecorHitBox(
                        1.0d,
                        1.0d,
                        1.0d,
                        DecorHitBox.Type.BARRIER
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack);
        final ShapedRecipe recipe = new ShapedRecipe(builder.key(), builder.itemStack())
                .shape(
                        "P  ",
                        "PLP",
                        "P P"
                )
                .setIngredient('P', Material.JUNGLE_PLANKS)
                .setIngredient('L', Material.LEATHER);

        recipe.setGroup(CustomDecorType.NAMESPACE + ":chair");

        return builder.recipes(
                Collections.singletonList(Map.entry(
                        recipe, true
                ))
        );
    }
}
