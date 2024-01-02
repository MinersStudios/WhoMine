package com.minersstudios.mscustoms.registry.decor.decoration.street;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.sound.SoundGroup;
import com.minersstudios.mscustoms.custom.decor.CustomDecorDataImpl;
import com.minersstudios.mscustoms.custom.decor.DecorHitBox;
import com.minersstudios.mscustoms.custom.decor.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class Wheelbarrow extends CustomDecorDataImpl<Wheelbarrow> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1142);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Тачка"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("wheelbarrow")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.SOLID)
                        .size(1.0d, 1.0d, 1.0d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.ANVIL)
                .itemStack(itemStack)
                .recipes(
                        unused -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "S S",
                                        " C ",
                                        " I "
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('S', Material.STICK),
                                        ShapedRecipeBuilder.material('C', Material.CAULDRON),
                                        ShapedRecipeBuilder.material('I', Material.IRON_INGOT)
                                ),
                                Boolean.TRUE
                        )
                );
    }
}
