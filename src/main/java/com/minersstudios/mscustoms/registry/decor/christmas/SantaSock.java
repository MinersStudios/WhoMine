package com.minersstudios.mscustoms.registry.decor.christmas;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscustoms.sound.SoundGroup;
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

public final class SantaSock extends CustomDecorDataImpl<SantaSock> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1186);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Носок санты"));
        itemStack.setItemMeta(itemMeta);

        final Builder builder = new Builder()
                .key("santa_sock")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .facings(Facing.WALL)
                        .size(0.7875d, 0.7875d, 0.7875d)
                        .build()
                )
                .facings(Facing.WALL)
                .soundGroup(SoundGroup.WOOL)
                .itemStack(itemStack)
                .parameters(DecorParameter.PAINTABLE);

        return MSPlugin.globalConfig().isChristmas()
                ? builder.recipes(
                        unused -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "LA",
                                        "LA",
                                        "LL"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('L', Material.LEATHER),
                                        ShapedRecipeBuilder.material('A', Material.AIR)
                                ),
                                Boolean.TRUE
                        )
                )
                : builder;
    }
}
