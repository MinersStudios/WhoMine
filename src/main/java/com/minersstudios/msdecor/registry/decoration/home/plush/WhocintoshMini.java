package com.minersstudios.msdecor.registry.decoration.home.plush;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.msdecor.api.CustomDecorDataImpl;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msdecor.api.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class WhocintoshMini extends CustomDecorDataImpl<WhocintoshMini> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1370);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Мини-Whocintosh"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("whocintosh_mini")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .facings(
                                Facing.FLOOR,
                                Facing.WALL
                        )
                        .wallDirected(true)
                        .size(0.5d, 0.5625d, 0.5d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOL)
                .itemStack(itemStack)
                .recipes(
                        Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "WWW",
                                        "WGW"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('W', Material.GRAY_WOOL),
                                        ShapedRecipeBuilder.material('G', Material.GLASS_PANE)
                                ),
                                true
                        )
                );
    }
}
