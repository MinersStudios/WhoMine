package com.minersstudios.msitem.registry.item;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.MSBlockUtils;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.api.CustomItemImpl;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class RawPlumbum extends CustomItemImpl {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "raw_plumbum";
        ITEM_STACK = new ItemStack(Material.PAPER);
        final ItemMeta meta = ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Рудной свинец"));
        meta.setCustomModelData(12001);
        ITEM_STACK.setItemMeta(meta);
    }

    public RawPlumbum() {
        super(KEY, ITEM_STACK);
    }

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        final ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.itemStack)
                .shape(
                        " I ",
                        "BIB",
                        " I "
                ).setIngredient('I', Material.RAW_IRON)
                .setIngredient('B', Material.WATER_BUCKET);

        final var rawPlumbumBlock = MSBlockUtils.getItemStack("raw_plumbum_block");

        if (rawPlumbumBlock.isEmpty()) {
            MSLogger.warning(
                    "Can't find custom block with key: raw_plumbum_block! Shaped recipe for RawPlumbum will not be registered!"
            );

            return Collections.singletonList(Map.entry(shapedRecipe, Boolean.TRUE));
        }

        return Arrays.asList(
                Map.entry(shapedRecipe, Boolean.TRUE),
                Map.entry(
                        new ShapedRecipe(
                                new NamespacedKey(MSItem.NAMESPACE, "raw_plumbum_from_block"),
                                this.itemStack.clone().add(8)
                        )
                        .shape("I")
                        .setIngredient('I', new RecipeChoice.ExactChoice(rawPlumbumBlock.get())),
                        Boolean.TRUE
                )
        );
    }
}
