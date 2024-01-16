package com.minersstudios.mscustoms.registry.decor.decoration.home;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
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

public final class SmallGlobe extends CustomDecorDataImpl<SmallGlobe> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1145);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Маленький глобус"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("small_globe")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .size(0.390725d, 0.515625d, 0.390725d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack)
                .recipes(
                        unused -> RecipeEntry.of(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "M",
                                        "S"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('M', Material.MAP),
                                        ShapedRecipeBuilder.material('S', Material.STICK)
                                ),
                                true
                        )
                );
    }
}
