package com.minersstudios.mscustoms.registry.decor.decoration.home.head;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.mscustoms.custom.decor.CustomDecorDataImpl;
import com.minersstudios.mscustoms.custom.decor.DecorHitBox;
import com.minersstudios.mscustoms.custom.decor.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ZoglinHead extends CustomDecorDataImpl<ZoglinHead> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1163);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Голова зомбу борова"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("zoglin_head")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.SOLID)
                        .facings(Facing.WALL)
                        .size(1.0d, 1.0d, 1.0d)
                        .build()
                )
                .facings(Facing.WALL)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack)
                .recipes(
                        unused -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        " FS",
                                        "BBS",
                                        "  S"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('F', Material.ROTTEN_FLESH),
                                        ShapedRecipeBuilder.material('B', Material.BONE),
                                        ShapedRecipeBuilder.material('S', Material.SPRUCE_LOG)
                                ),
                                Boolean.TRUE
                        )
                );
    }
}
