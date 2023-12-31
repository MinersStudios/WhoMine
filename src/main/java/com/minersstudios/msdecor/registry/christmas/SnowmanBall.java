package com.minersstudios.msdecor.registry.christmas;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.mscore.utility.ChatUtils;
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

public final class SnowmanBall extends CustomDecorDataImpl<SnowmanBall> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack ceiling = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta ceilingMeta = ceiling.getItemMeta();

        ceilingMeta.setCustomModelData(1255);
        ceilingMeta.displayName(ChatUtils.createDefaultStyledText("Новогодний шар в виде снеговика"));
        ceiling.setItemMeta(ceilingMeta);

        final ItemStack wall = ceiling.clone();
        final ItemMeta wallMeta = wall.getItemMeta();

        wallMeta.setCustomModelData(1397);
        wall.setItemMeta(wallMeta);

        final Builder builder0 = new Builder()
                .key("snowman_ball")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .facings(
                                Facing.CEILING,
                                Facing.WALL
                        )
                        .size(0.625d, 0.65625d, 0.625d)
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
                                        "BLB",
                                        "BBB"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('S', Material.STRING),
                                        ShapedRecipeBuilder.material('B', Material.SNOWBALL),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                ),
                                Boolean.TRUE
                        )
                )
                : builder0;
    }
}
