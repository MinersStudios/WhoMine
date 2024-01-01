package com.minersstudios.mscustoms.registry.decor.christmas;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.decor.CustomDecorDataImpl;
import com.minersstudios.mscustoms.custom.decor.DecorHitBox;
import com.minersstudios.mscustoms.custom.decor.DecorParameter;
import com.minersstudios.mscustoms.custom.decor.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class StarOnString extends CustomDecorDataImpl<StarOnString> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack ceiling = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta ceilingMeta = ceiling.getItemMeta();

        ceilingMeta.setCustomModelData(1256);
        ceilingMeta.displayName(ChatUtils.createDefaultStyledText("Звезда на верёвке"));
        ceiling.setItemMeta(ceilingMeta);

        final ItemStack wall = ceiling.clone();
        final ItemMeta wallMeta = wall.getItemMeta();

        wallMeta.setCustomModelData(1398);
        wall.setItemMeta(wallMeta);

        final Builder builder0 = new Builder()
                .key("star_on_string")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .facings(
                                Facing.CEILING,
                                Facing.WALL
                        )
                        .size(0.5625d, 0.71875d, 0.5625d)
                        .build()
                )
                .facings(
                        Facing.CEILING,
                        Facing.WALL
                )
                .soundGroup(SoundGroup.GLASS)
                .itemStack(ceiling)
                .parameters(
                        DecorParameter.FACE_TYPED,
                        DecorParameter.PAINTABLE
                )
                .faceTypes(
                        builder -> Map.entry(
                                Facing.CEILING,
                                new Type(
                                        builder,
                                        "default",
                                        ceiling
                                )
                        ),
                        builder -> Map.entry(
                                Facing.WALL,
                                new Type(
                                        builder,
                                        "wall",
                                        wall
                                )
                        )
                );

        return MSPlugin.globalConfig().isChristmas()
                ? builder0.recipes(
                        unused -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        " S ",
                                        "GGG",
                                        " G "
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('S', Material.STRING),
                                        ShapedRecipeBuilder.material('G', Material.GOLD_NUGGET)
                                ),
                                Boolean.TRUE
                        )
                )
                : builder0;
    }
}
