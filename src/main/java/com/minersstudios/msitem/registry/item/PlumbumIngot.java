package com.minersstudios.msitem.registry.item;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.MSBlockUtils;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.api.CustomItemImpl;
import com.minersstudios.msitem.api.CustomItemType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

public final class PlumbumIngot extends CustomItemImpl {
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

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
        final ItemStack input = CustomItemType.RAW_PLUMBUM.getCustomItem().getItem();
        final FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                new NamespacedKey(MSItem.NAMESPACE, "plumbum_ingot_furnace"),
                this.itemStack,
                new RecipeChoice.ExactChoice(input),
                0.7f,
                200
        );
        final BlastingRecipe blastingRecipe = new BlastingRecipe(
                new NamespacedKey(MSItem.NAMESPACE, "plumbum_ingot_blast"),
                this.itemStack,
                new RecipeChoice.ExactChoice(input),
                0.7f,
                100
        );

        final var plumbumBlock = MSBlockUtils.getItemStack("plumbum_block");

        if (plumbumBlock.isEmpty()) {
            MSItem.logger().warning(
                    "Can't find custom block with key: plumbum_block! Shaped recipe will not be registered!"
            );

            return ImmutableList.of(
                    Map.entry(furnaceRecipe, Boolean.FALSE),
                    Map.entry(blastingRecipe, Boolean.FALSE)
            );
        }

        return ImmutableList.of(
                Map.entry(furnaceRecipe, false),
                Map.entry(blastingRecipe, false),
                Map.entry(
                        new ShapedRecipe(
                                new NamespacedKey(MSItem.NAMESPACE, "plumbum_ingot_from_block"),
                                this.itemStack.clone().add(8)
                        )
                        .shape("I")
                        .setIngredient('I', new RecipeChoice.ExactChoice(plumbumBlock.get())),
                        Boolean.TRUE
                )
        );
    }
}
