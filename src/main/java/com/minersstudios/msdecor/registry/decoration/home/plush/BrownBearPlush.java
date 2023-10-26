package com.minersstudios.msdecor.registry.decoration.home.plush;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.api.CustomDecorDataImpl;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msdecor.api.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class BrownBearPlush extends CustomDecorDataImpl<BrownBearPlush> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1154);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Плюшевый мишка"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("brown_bear_plush")
                .hitBox(new DecorHitBox(
                        0.5d,
                        0.90625d,
                        0.5d,
                        DecorHitBox.Type.NONE
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
                                        "AWA",
                                        "WAW"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('W', Material.BROWN_WOOL),
                                        ShapedRecipeBuilder.material('A', Material.AIR)
                                )
                                .build(),
                                true
                        )
                );
    }
}
