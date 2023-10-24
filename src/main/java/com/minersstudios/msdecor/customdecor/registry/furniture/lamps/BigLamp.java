package com.minersstudios.msdecor.customdecor.registry.furniture.lamps;

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

public final class BigLamp extends CustomDecorDataImpl<BigLamp> {

    @Override
    protected CustomDecorDataImpl<BigLamp>.@NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1151);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Большая лампа"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("big_lamp")
                .hitBox(new DecorHitBox(
                        0.875d,
                        2.3125d,
                        0.875d,
                        0.0d,
                        0.375d,
                        0.0d,
                        DecorHitBox.Type.LIGHT
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack)
                .recipes(
                        builder -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .namespacedKey(builder.key())
                                .category(CraftingBookCategory.BUILDING)
                                .result(builder.itemStack())
                                .shape(
                                        "L",
                                        "S",
                                        "S"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('S', Material.STICK),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                )
                                .build(),
                                true
                        )
                )
                .parameters(DecorParameter.LIGHTABLE)
                .lightLevels(0, 15);
    }
}
