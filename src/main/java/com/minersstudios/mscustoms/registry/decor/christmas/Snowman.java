package com.minersstudios.mscustoms.registry.decor.christmas;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.plugin.MSPlugin;
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

public final class Snowman extends CustomDecorDataImpl<Snowman> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1187);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Снеговик"));
        itemStack.setItemMeta(itemMeta);

        final ItemStack broken = itemStack.clone();
        final ItemMeta brokenMeta = broken.getItemMeta();

        brokenMeta.setCustomModelData(1188);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Сломанный снеговик"));
        broken.setItemMeta(brokenMeta);

        final Builder builder0 = new Builder()
                .key("snowman")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .size(0.925d, 2.203125d, 0.925d)
                        .modelOffsetY(0.075d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.SNOW)
                .itemStack(itemStack)
                .parameters(
                        DecorParameter.WRENCHABLE,
                        DecorParameter.PAINTABLE
                )
                .types(
                        builder -> new Type(builder, "broken", broken)
                )
                .dropsType(true);

        return MSPlugin.globalConfig().isChristmas()
                ? builder0.recipes(
                        unused -> RecipeEntry.of(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        " L ",
                                        "SBS",
                                        " B "
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('L', Material.LEATHER),
                                        ShapedRecipeBuilder.material('S', Material.STICK),
                                        ShapedRecipeBuilder.material('B', Material.SNOW_BLOCK)
                                ),
                                true
                        )
                )
                : builder0;
    }
}
