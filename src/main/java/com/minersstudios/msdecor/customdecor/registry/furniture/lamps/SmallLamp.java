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

public final class SmallLamp extends CustomDecorDataImpl<SmallLamp> {

    @Override
    protected CustomDecorDataImpl<SmallLamp>.@NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1144);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Маленькая лампа"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("small_lamp")
                .hitBox(new DecorHitBox(
                        0.625d,
                        1.0625d,
                        0.625d,
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
