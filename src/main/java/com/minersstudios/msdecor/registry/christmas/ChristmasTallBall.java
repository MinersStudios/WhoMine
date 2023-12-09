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

public final class ChristmasTallBall extends CustomDecorDataImpl<ChristmasTallBall> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack ceiling = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta ceilingMeta = ceiling.getItemMeta();

        ceilingMeta.setCustomModelData(1257);
        ceilingMeta.displayName(ChatUtils.createDefaultStyledText("Новогодний шар-сосулька"));
        ceiling.setItemMeta(ceilingMeta);

        final ItemStack wall = ceiling.clone();
        final ItemMeta wallMeta = wall.getItemMeta();

        wallMeta.setCustomModelData(1399);
        wall.setItemMeta(wallMeta);

        final Builder builder0 = new Builder()
                .key("christmas_tall_ball")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .facings(
                                Facing.CEILING,
                                Facing.WALL
                        )
                        .size(0.4125d, 0.9375d, 0.4125d)
                        .build()
                )
                .facings(
                        Facing.CEILING,
                        Facing.WALL
                )
                .soundGroup(SoundGroup.GLASS)
                .itemStack(ceiling)
                .parameters(DecorParameter.FACE_TYPED)
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

        return MSDecor.config().isChristmas
                ? builder0.recipes(
                        Map.entry(
                                RecipeBuilder.shapedBuilder()
                                        .category(CraftingBookCategory.BUILDING)
                                        .shape(
                                                " S ",
                                                "CCC",
                                                " C "
                                        )
                                        .ingredients(
                                                ShapedRecipeBuilder.material('S', Material.STRING),
                                                ShapedRecipeBuilder.material('C', Material.CLAY_BALL)
                                        ),
                                true
                        )
                )
                : builder0;
    }
}
