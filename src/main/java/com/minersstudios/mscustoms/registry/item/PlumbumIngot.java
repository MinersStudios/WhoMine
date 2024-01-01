package com.minersstudios.mscustoms.registry.item;

import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.MSBlockUtils;
import com.minersstudios.mscore.utility.SharedConstants;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
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
                new NamespacedKey(SharedConstants.MSITEMS_NAMESPACE, "plumbum_ingot_furnace"),
                this.itemStack,
                new RecipeChoice.ExactChoice(input),
                0.7f,
                200
        );
        final BlastingRecipe blastingRecipe = new BlastingRecipe(
                new NamespacedKey(SharedConstants.MSITEMS_NAMESPACE, "plumbum_ingot_blast"),
                this.itemStack,
                new RecipeChoice.ExactChoice(input),
                0.7f,
                100
        );

        final var plumbumBlock = MSBlockUtils.getItemStack("plumbum_block");

        if (plumbumBlock.isEmpty()) {
            MSCustoms.logger().warning(
                    "Can't find custom block with key: plumbum_block! Shaped recipe will not be registered!"
            );

            return Arrays.asList(
                    Map.entry(furnaceRecipe, Boolean.FALSE),
                    Map.entry(blastingRecipe, Boolean.FALSE)
            );
        }

        return Arrays.asList(
                Map.entry(furnaceRecipe, Boolean.FALSE),
                Map.entry(blastingRecipe, Boolean.FALSE),
                Map.entry(
                        new ShapedRecipe(
                                new NamespacedKey(SharedConstants.MSITEMS_NAMESPACE, "plumbum_ingot_from_block"),
                                this.itemStack.clone().add(8)
                        )
                        .shape("I")
                        .setIngredient('I', new RecipeChoice.ExactChoice(plumbumBlock.get())),
                        Boolean.TRUE
                )
        );
    }
}
