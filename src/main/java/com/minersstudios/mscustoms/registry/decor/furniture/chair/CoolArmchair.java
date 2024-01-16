package com.minersstudios.mscustoms.registry.decor.furniture.chair;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.decor.CustomDecorDataImpl;
import com.minersstudios.mscustoms.custom.decor.DecorHitBox;
import com.minersstudios.mscustoms.custom.decor.DecorParameter;
import com.minersstudios.mscustoms.custom.decor.Facing;
import com.minersstudios.mscustoms.sound.SoundGroup;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

public final class CoolArmchair extends CustomDecorDataImpl<CoolArmchair> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1016);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Стильный стул"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("cool_armchair")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.SOLID)
                        .size(1.0d, 1.0d, 1.0d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOL)
                .itemStack(itemStack)
                .recipes(
                        unused -> RecipeEntry.of(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "L  ",
                                        "LLL",
                                        "I I"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('I', Material.IRON_NUGGET),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                ),
                                true
                        )
                )
                .parameters(
                        DecorParameter.SITTABLE,
                        DecorParameter.WRENCHABLE,
                        DecorParameter.PAINTABLE
                )
                .sitHeight(0.6d)
                .types(
                        builder -> new Type(builder, "left",   createItem(itemStack, 1017)),
                        builder -> new Type(builder, "middle", createItem(itemStack, 1018)),
                        builder -> new Type(builder, "right",  createItem(itemStack, 1019))
                );
    }

    private static @NotNull ItemStack createItem(
            final @NotNull ItemStack parent,
            final int customModelData
    ) {
        final ItemStack itemStack = parent.clone();
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
