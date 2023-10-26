package com.minersstudios.msdecor.registry.decoration.home.plush;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
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
                .hitBox(new DecorHitBox(
                        1.0d,
                        0.5625d,
                        1.0d,
                        DecorHitBox.Type.NONE
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOL)
                .itemStack(itemStack)
                .recipes(
                        builder -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .namespacedKey(builder.key())
                                .category(CraftingBookCategory.BUILDING)
                                .result(builder.itemStack())
                                .shape(
                                        "WWW",
                                        "WGW"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('W', Material.GRAY_WOOL),
                                        ShapedRecipeBuilder.material('G', Material.GLASS_PANE)
                                )
                                .build(),
                                true
                        )
                );
    }
}
