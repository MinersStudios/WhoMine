package com.minersstudios.wholib.paper.custom.decor.registry.christmas;

import com.minersstudios.wholib.paper.WhoMine;
import com.minersstudios.wholib.recipe.builder.RecipeBuilder;
import com.minersstudios.wholib.recipe.category.CraftingRecipeCategory;
import com.minersstudios.wholib.recipe.choice.RecipeChoiceEntry;
import com.minersstudios.wholib.recipe.entry.RecipeEntry;
import com.minersstudios.wholib.utility.ChatUtils;
import com.minersstudios.wholib.paper.custom.decor.CustomDecorDataImpl;
import com.minersstudios.wholib.paper.custom.decor.DecorHitBox;
import com.minersstudios.wholib.paper.custom.decor.DecorParameter;
import com.minersstudios.wholib.paper.custom.decor.Facing;
import com.minersstudios.wholib.paper.world.sound.SoundGroup;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ChristmasBall extends CustomDecorDataImpl<ChristmasBall> {

    ChristmasBall(final @NotNull WhoMine plugin) throws IllegalStateException {
        super(plugin);
    }

    @Override
    protected @NotNull Builder builder(final @NotNull WhoMine plugin) {
        final ItemStack ceiling = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta ceilingMeta = ceiling.getItemMeta();

        ceilingMeta.setCustomModelData(1185);
        ceilingMeta.displayName(ChatUtils.createDefaultStyledText("Новогодний шар"));
        ceiling.setItemMeta(ceilingMeta);

        final ItemStack wall = ceiling.clone();
        final ItemMeta wallMeta = wall.getItemMeta();

        wallMeta.setCustomModelData(1400);
        wall.setItemMeta(wallMeta);

        final Builder builder0 = new Builder()
                .path("christmas_ball")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .facings(
                                Facing.CEILING,
                                Facing.WALL
                        )
                        .size(0.4125d, 0.64375d, 0.4125d)
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
                        builder -> Map.entry(Facing.CEILING, new Type(builder, "default", ceiling)),
                        builder -> Map.entry(Facing.WALL,    new Type(builder, "wall",    wall))
                );

        return plugin.getConfiguration().isChristmas()
                ? builder0.recipes(
                        unused -> RecipeEntry.fromBuilder(
                                RecipeBuilder
                                .shaped()
                                .category(CraftingRecipeCategory.BUILDING)
                                .shape(
                                        " S ",
                                        "CCC",
                                        "CCC"
                                )
                                .ingredients(
                                        RecipeChoiceEntry.ofEnum('S', Material.STRING),
                                        RecipeChoiceEntry.ofEnum('C', Material.CLAY_BALL)
                                ),
                                true
                        )
                )
                : builder0;
    }
}
