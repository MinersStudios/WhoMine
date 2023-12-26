package com.minersstudios.msdecor.registry.christmas;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msdecor.api.CustomDecorDataImpl;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msdecor.api.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class TreeStar extends CustomDecorDataImpl<TreeStar> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1259);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Новогодняя звезда"));
        itemStack.setItemMeta(itemMeta);

        final Builder builder = new Builder()
                .key("tree_star")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .size(1.0d, 2.20625d, 1.0d)
                        .modelOffsetY(0.23125d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.STONE)
                .itemStack(itemStack);

        return MSPlugin.globalConfig().isChristmas()
                ? builder.recipes(
                        unused -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        " I ",
                                        "III",
                                        " B "
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('I', Material.GOLD_INGOT),
                                        ShapedRecipeBuilder.material('B', Material.GOLD_BLOCK)
                                ),
                                Boolean.TRUE
                        )
                )
                : builder;
    }
}
