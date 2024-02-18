package com.minersstudios.mscustoms.registry.decor.christmas;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.choice.RecipeChoiceEntry;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.decor.CustomDecorDataImpl;
import com.minersstudios.mscustoms.custom.decor.DecorHitBox;
import com.minersstudios.mscustoms.custom.decor.Facing;
import com.minersstudios.mscustoms.sound.SoundGroup;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

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
                        unused -> RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        " I ",
                                        "III",
                                        " B "
                                )
                                .ingredients(
                                        RecipeChoiceEntry.material('I', Material.GOLD_INGOT),
                                        RecipeChoiceEntry.material('B', Material.GOLD_BLOCK)
                                ),
                                true
                        )
                )
                : builder;
    }
}
