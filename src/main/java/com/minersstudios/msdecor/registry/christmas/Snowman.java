package com.minersstudios.msdecor.registry.christmas;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.api.CustomDecorDataImpl;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msdecor.api.DecorParameter;
import com.minersstudios.msdecor.api.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
                        builder -> new Type(
                                builder,
                                "broken",
                                broken
                        )
                )
                .dropsType(true);

        return MSDecor.config().isChristmas
                ? builder0.recipes(
                        Map.entry(
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
