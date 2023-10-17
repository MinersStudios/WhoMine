package com.minersstudios.msdecor.customdecor.registry.furniture.chairs.chair;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.customdecor.CustomDecorDataImpl;
import com.minersstudios.msdecor.customdecor.CustomDecorType;
import com.minersstudios.msdecor.customdecor.DecorHitBox;
import com.minersstudios.msdecor.customdecor.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class CherryChair extends CustomDecorDataImpl<CherryChair> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1377);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Вишнёвый стул со спинкой"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("cherry_chair")
                .hitBox(new DecorHitBox(
                        1.0d,
                        1.0d,
                        1.0d,
                        DecorHitBox.Type.BARRIER
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack)
                .recipes(
                        builder -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .namespacedKey(builder.key())
                                .group(CustomDecorType.NAMESPACE + ":chair")
                                .category(CraftingBookCategory.BUILDING)
                                .result(builder.itemStack())
                                .shape(
                                        "P  ",
                                        "PLP",
                                        "P P"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('P', Material.CHERRY_PLANKS),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                )
                                .build(),
                                true
                        )
                );
    }
}
