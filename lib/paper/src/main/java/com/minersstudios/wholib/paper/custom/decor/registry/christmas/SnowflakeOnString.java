package com.minersstudios.wholib.paper.custom.decor.registry.christmas;

import com.minersstudios.wholib.paper.WhoMine;
import com.minersstudios.wholib.paper.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.wholib.paper.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.wholib.paper.world.sound.SoundGroup;
import com.minersstudios.wholib.utility.ChatUtils;
import com.minersstudios.wholib.paper.custom.decor.CustomDecorDataImpl;
import com.minersstudios.wholib.paper.custom.decor.DecorHitBox;
import com.minersstudios.wholib.paper.custom.decor.DecorParameter;
import com.minersstudios.wholib.paper.custom.decor.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class SnowflakeOnString extends CustomDecorDataImpl<SnowflakeOnString> {

    SnowflakeOnString(final @NotNull WhoMine plugin) throws IllegalStateException {
        super(plugin);
    }

    @Override
    protected @NotNull Builder builder(final @NotNull WhoMine plugin) {
        final ItemStack ceiling = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta ceilingMeta = ceiling.getItemMeta();

        ceilingMeta.setCustomModelData(1254);
        ceilingMeta.displayName(ChatUtils.createDefaultStyledText("Снежинка на верёвке"));
        ceiling.setItemMeta(ceilingMeta);

        final ItemStack wall = ceiling.clone();
        final ItemMeta wallMeta = wall.getItemMeta();

        wallMeta.setCustomModelData(1396);
        wall.setItemMeta(wallMeta);

        final Builder builder0 = new Builder()
                .path("snowflake_on_string")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .facings(
                                Facing.CEILING,
                                Facing.WALL
                        )
                        .size(0.6875d, 0.84375d, 0.6875d)
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
                        builder -> Map.entry(Facing.CEILING, new Type(builder, "default", ceiling)),
                        builder -> Map.entry(Facing.WALL,    new Type(builder, "wall",    wall))
                );

        return plugin.getConfiguration().isChristmas()
                ? builder0.recipes(
                        unused -> RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        " S ",
                                        "BBB",
                                        " B "
                                )
                                .ingredients(
                                        RecipeChoiceEntry.material('S', Material.STRING),
                                        RecipeChoiceEntry.material('B', Material.SNOWBALL)
                                ),
                                true
                        )
                )
                : builder0;
    }
}
