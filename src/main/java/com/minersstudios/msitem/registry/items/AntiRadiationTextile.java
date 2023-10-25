package com.minersstudios.msitem.registry.items;

import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msitem.api.CustomItemImpl;
import com.minersstudios.msitem.api.CustomItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Override
    public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        return Collections.singletonList(Map.entry(
                new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        "WSW",
                        "SIS",
                        "WSW"
                )
                .setIngredient('I', CustomItemType.PLUMBUM_INGOT.getCustomItem().getItem())
                .setIngredient('W', Material.YELLOW_WOOL)
                .setIngredient('S', Material.STRING),
                true
        ));
    }
}
