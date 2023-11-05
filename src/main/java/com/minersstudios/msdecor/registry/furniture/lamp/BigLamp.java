package com.minersstudios.msdecor.registry.furniture.lamp;

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

public final class BigLamp extends CustomDecorDataImpl<BigLamp> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1151);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Большая лампа"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("big_lamp")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.LIGHT)
                        .size(0.875d, 2.3125d, 0.875d)
                        .modelOffset(0.0d, 0.375d, 0.0d)
                        .build()
                )
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack)
                .recipes(
                        Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "L",
                                        "S",
                                        "S"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('S', Material.STICK),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                ),
                                true
                        )
                )
                .parameters(
                        DecorParameter.LIGHTABLE,
                        DecorParameter.PAINTABLE
                )
                .lightLevels(0, 15);
    }
}
