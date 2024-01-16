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

public final class Patefon extends CustomDecorDataImpl<Patefon> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1147);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Патефон"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("patefon")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .size(0.75d, 0.5d, 0.75d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.ANVIL)
                .itemStack(itemStack)
                .recipes(
                        unused -> RecipeEntry.of(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape("PJP")
                                .ingredients(
                                        ShapedRecipeBuilder.material('J', Material.JUKEBOX),
                                        ShapedRecipeBuilder.material('P', Material.SPRUCE_PLANKS)
                                ),
                                true
                        )
                );
    }
}
