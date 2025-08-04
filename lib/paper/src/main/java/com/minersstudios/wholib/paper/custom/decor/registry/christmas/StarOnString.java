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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class StarOnString extends CustomDecorDataImpl<StarOnString> {

    StarOnString(final @NotNull WhoMine plugin) throws IllegalStateException {
        super(plugin);
    }

    @Override
    protected @NotNull Builder builder(final @NotNull WhoMine plugin) {
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
                .path("star_on_string")
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
                                        "GGG",
                                        " G "
                                )
                                .ingredients(
                                        RecipeChoiceEntry.material('S', Material.STRING),
                                        RecipeChoiceEntry.material('G', Material.GOLD_NUGGET)
                                ),
                                true
                        )
                )
                : builder0;
    }
}
