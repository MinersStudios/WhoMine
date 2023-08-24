package com.minersstudios.msitem.item.registry.items;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.MSBlockUtils;
import com.minersstudios.msitem.item.CustomItemImpl;
import com.minersstudios.msitem.item.CustomItemType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

public class PlumbumIngot extends CustomItemImpl {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "plumbum_ingot";
        ITEM_STACK = new ItemStack(Material.PAPER);
        final ItemMeta meta = ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Свинцовый слиток"));
        meta.setCustomModelData(12000);
        ITEM_STACK.setItemMeta(meta);
    }

    public PlumbumIngot() {
        super(KEY, ITEM_STACK);
    }

    @Override
    public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        final ItemStack input = CustomItemType.RAW_PLUMBUM.getCustomItem().getItem();
        final FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                new NamespacedKey(CustomItemType.NAMESPACE, "plumbum_ingot_furnace"),
                this.itemStack,
                new RecipeChoice.ExactChoice(input),
                0.7f,
                200
        );
        final BlastingRecipe blastingRecipe = new BlastingRecipe(
                new NamespacedKey(CustomItemType.NAMESPACE, "plumbum_ingot_blast"),
                this.itemStack,
                new RecipeChoice.ExactChoice(input),
                0.7f,
                100
        );

        final var plumbumBlock = MSBlockUtils.getItemStack("plumbum_block");

        if (plumbumBlock.isEmpty()) {
            MSLogger.warning("Can't find custom block with key: plumbum_block! Shaped recipe will not be registered!");
            return ImmutableList.of(
                    Map.entry(furnaceRecipe, false),
                    Map.entry(blastingRecipe, false)
            );
        }

        return ImmutableList.of(
                Map.entry(furnaceRecipe, false),
                Map.entry(blastingRecipe, false),
                Map.entry(
                        new ShapedRecipe(new NamespacedKey(CustomItemType.NAMESPACE, "plumbum_ingot_from_block"), this.itemStack.clone().add(8))
                        .shape("I")
                        .setIngredient('I', new RecipeChoice.ExactChoice(plumbumBlock.get())),
                        true
                )
        );
    }
}
