package com.minersstudios.mscustoms.registry.decor.decoration.home.plush;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.choice.RecipeChoiceEntry;
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
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .size(0.75d, 0.90625d, 0.75d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOL)
                .itemStack(itemStack)
                .recipes(
                        unused -> RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "AWA",
                                        "WAW"
                                )
                                .ingredients(
                                        RecipeChoiceEntry.material('W', Material.BROWN_WOOL),
                                        RecipeChoiceEntry.material('A', Material.AIR)
                                ),
                                true
                        )
                );
    }
}
