package com.minersstudios.msitem.item.registry.items;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.MSBlockUtils;
import com.minersstudios.msitem.item.CustomItemImpl;
import com.minersstudios.msitem.item.CustomItemType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RawPlumbum extends CustomItemImpl {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "raw_plumbum";
        ITEM_STACK = new ItemStack(Material.PAPER);
        ItemMeta meta = ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Рудной свинец"));
        meta.setCustomModelData(12001);
        ITEM_STACK.setItemMeta(meta);
    }

    public RawPlumbum() {
        super(KEY, ITEM_STACK);
    }

    @Override
    public @NotNull List<Map.Entry<Recipe, Boolean>> initRecipes() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        " I ",
                        "BIB",
                        " I "
                ).setIngredient('I', Material.RAW_IRON)
                .setIngredient('B', Material.WATER_BUCKET);

        var rawPlumbumBlock = MSBlockUtils.getItemStack("raw_plumbum_block");

        if (rawPlumbumBlock.isEmpty()) {
            MSLogger.warning("Can't find custom block with key: raw_plumbum_block! Shaped recipe for RawPlumbum will not be registered!");
            return Collections.singletonList(Map.entry(shapedRecipe, true));
        }

        return List.of(
                Map.entry(shapedRecipe, true),
                Map.entry(
                        new ShapedRecipe(new NamespacedKey(CustomItemType.NAMESPACE, "raw_plumbum_from_block"), this.itemStack.clone().add(8))
                        .shape("I")
                        .setIngredient('I', new RecipeChoice.ExactChoice(rawPlumbumBlock.get())),
                        true
                )
        );
    }
}
