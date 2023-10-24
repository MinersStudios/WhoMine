package com.minersstudios.msdecor.customdecor.registry.furniture.chairs;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.customdecor.CustomDecorDataImpl;
import com.minersstudios.msdecor.customdecor.DecorHitBox;
import com.minersstudios.msdecor.customdecor.DecorParameter;
import com.minersstudios.msdecor.customdecor.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class CoolArmchair extends CustomDecorDataImpl<CoolArmchair> {

    @Override
    protected CustomDecorDataImpl<CoolArmchair>.@NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1016);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Стильный стул"));
        itemStack.setItemMeta(itemMeta);

        final ItemStack itemStackLeft = itemStack.clone();
        final ItemMeta itemMetaLeft = itemStackLeft.getItemMeta();

        itemMetaLeft.setCustomModelData(1017);
        itemStackLeft.setItemMeta(itemMetaLeft);

        final ItemStack itemStackMiddle = itemStack.clone();
        final ItemMeta itemMetaMiddle = itemStackMiddle.getItemMeta();

        itemMetaMiddle.setCustomModelData(1018);
        itemStackMiddle.setItemMeta(itemMetaMiddle);

        final ItemStack itemStackRight = itemStack.clone();
        final ItemMeta itemMetaRight = itemStackRight.getItemMeta();

        itemMetaRight.setCustomModelData(1019);
        itemStackRight.setItemMeta(itemMetaRight);

        return new Builder()
                .key("cool_armchair")
                .hitBox(new DecorHitBox(
                        1.0d,
                        1.0d,
                        1.0d,
                        DecorHitBox.Type.BARRIER
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOL)
                .itemStack(itemStack)
                .recipes(
                        builder -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .namespacedKey(builder.key())
                                .category(CraftingBookCategory.BUILDING)
                                .result(builder.itemStack())
                                .shape(
                                        "L  ",
                                        "LLL",
                                        "I I"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('I', Material.IRON_NUGGET),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                )
                                .build(),
                                true
                        )
                )
                .parameters(
                        DecorParameter.SITTABLE,
                        DecorParameter.WRENCHABLE
                )
                .sitHeight(0.55d)
                .wrenchTypes(
                        builder -> new Type(
                                builder,
                                "left",
                                itemStackLeft
                        ),
                        builder -> new Type(
                                builder,
                                "middle",
                                itemStackMiddle
                        ),
                        builder -> new Type(
                                builder,
                                "right",
                                itemStackRight
                        )
                );
    }
}
