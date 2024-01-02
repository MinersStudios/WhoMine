package com.minersstudios.mscustoms.registry.decor.furniture.chair;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.sound.SoundGroup;
import com.minersstudios.mscustoms.custom.decor.CustomDecorDataImpl;
import com.minersstudios.mscustoms.custom.decor.DecorHitBox;
import com.minersstudios.mscustoms.custom.decor.DecorParameter;
import com.minersstudios.mscustoms.custom.decor.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class CoolChair extends CustomDecorDataImpl<CoolChair> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1037);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Стильный стул"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("cool_chair")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.SOLID)
                        .size(1.0d, 1.0d, 1.0d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOL)
                .itemStack(itemStack)
                .recipes(
                        unused -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "LLL",
                                        "IAI"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('I', Material.IRON_NUGGET),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER),
                                        ShapedRecipeBuilder.material('A', Material.AIR)
                                ),
                                Boolean.TRUE
                        )
                )
                .parameters(
                        DecorParameter.SITTABLE,
                        DecorParameter.PAINTABLE
                )
                .sitHeight(0.6d);
    }
}
