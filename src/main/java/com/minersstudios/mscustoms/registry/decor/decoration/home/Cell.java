package com.minersstudios.mscustoms.registry.decor.decoration.home;

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

public final class Cell extends CustomDecorDataImpl<Cell> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1174);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Клетка"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("cell")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.SOLID)
                        .facings(
                                Facing.FLOOR,
                                Facing.CEILING,
                                Facing.WALL
                        )
                        .size(1.0d, 1.0d, 1.0d)
                        .build()
                )
                .facings(
                        Facing.FLOOR,
                        Facing.CEILING,
                        Facing.WALL
                )
                .soundGroup(SoundGroup.CHAIN)
                .itemStack(itemStack)
                .recipes(
                        unused -> RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        " C ",
                                        "BBB",
                                        "BBB"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('B', Material.IRON_BARS),
                                        ShapedRecipeBuilder.material('C', Material.CHAIN)
                                ),
                                true
                        )
                )
                .parameters(DecorParameter.TYPED)
                .types(
                        builder -> new Type(builder, "bee",                createItem(itemStack, 1175)),
                        builder -> new Type(builder, "chicken",            createItem(itemStack, 1176)),
                        builder -> new Type(builder, "parrot_blue",        createItem(itemStack, 1177)),
                        builder -> new Type(builder, "parrot_green",       createItem(itemStack, 1178)),
                        builder -> new Type(builder, "parrot_gray",        createItem(itemStack, 1179)),
                        builder -> new Type(builder, "parrot_red_blue",    createItem(itemStack, 1180)),
                        builder -> new Type(builder, "parrot_yellow_blue", createItem(itemStack, 1181)),
                        builder -> new Type(builder, "slime",              createItem(itemStack, 1182))
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
