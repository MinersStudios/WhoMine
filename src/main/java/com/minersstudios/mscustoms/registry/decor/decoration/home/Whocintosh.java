package com.minersstudios.mscustoms.registry.decor.decoration.home;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscustoms.sound.SoundGroup;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.decor.CustomDecorDataImpl;
import com.minersstudios.mscustoms.custom.decor.DecorHitBox;
import com.minersstudios.mscustoms.custom.decor.Facing;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class Whocintosh extends CustomDecorDataImpl<Whocintosh> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1371);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Whocintosh"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("whocintosh")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.SOLID)
                        .size(1.0d, 1.0d, 1.0d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.ANVIL)
                .itemStack(itemStack)
                .recipes(
                        unused -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "III",
                                        "IGI",
                                        "ITI"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.itemStack('I', CustomItemType.PLUMBUM_INGOT.getCustomItem().getItem()),
                                        ShapedRecipeBuilder.material('G', Material.GLASS_PANE),
                                        ShapedRecipeBuilder.material('T', Material.REDSTONE_TORCH)
                                ),
                                Boolean.TRUE
                        )
                );
    }
}
