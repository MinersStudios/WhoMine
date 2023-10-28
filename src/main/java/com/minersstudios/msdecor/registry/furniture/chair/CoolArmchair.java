package com.minersstudios.msdecor.registry.furniture.chair;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.api.CustomDecorDataImpl;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msdecor.api.DecorParameter;
import com.minersstudios.msdecor.api.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class CoolArmchair extends CustomDecorDataImpl<CoolArmchair> {

    @Override
    protected @NotNull Builder builder() {
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
                        DecorHitBox.Type.SOLID
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOL)
                .itemStack(itemStack)
                .recipes(
                        Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "L  ",
                                        "LLL",
                                        "I I"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('I', Material.IRON_NUGGET),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                ),
                                true
                        )
                )
                .parameters(
                        DecorParameter.SITTABLE,
                        DecorParameter.WRENCHABLE,
                        DecorParameter.PAINTABLE
                )
                .sitHeight(0.6d)
                .types(
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
