package com.minersstudios.mscustoms.registry.item;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public final class AntiRadiationTextile extends CustomItemImpl {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "anti_radiation_textile";
        ITEM_STACK = new ItemStack(Material.PAPER);
        final ItemMeta meta = ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Антирадиационная ткань"));
        meta.setCustomModelData(12002);
        ITEM_STACK.setItemMeta(meta);
    }

    public AntiRadiationTextile() {
        super(KEY, ITEM_STACK);
    }

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
        return Collections.singletonList(
                RecipeEntry.fromBuilder(
                        RecipeBuilder.shaped()
                        .namespacedKey(this.namespacedKey)
                        .result(this.itemStack)
                        .shape(
                                "WSW",
                                "SIS",
                                "WSW"
                        )
                        .ingredients(
                                ShapedRecipeBuilder.itemStack('I', CustomItemType.PLUMBUM_INGOT.getCustomItem().getItem()),
                                ShapedRecipeBuilder.material('W', Material.YELLOW_WOOL),
                                ShapedRecipeBuilder.material('S', Material.STRING)
                        ),
                        true
                )
        );
    }
}
