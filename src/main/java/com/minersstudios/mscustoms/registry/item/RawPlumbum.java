package com.minersstudios.mscustoms.registry.item;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.utility.MSBlockUtils;
import com.minersstudios.mscore.utility.SharedConstants;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
        final var shapedBuilder =
                RecipeBuilder.shapedBuilder()
                .namespacedKey(this.namespacedKey)
                .result(this.itemStack)
                .shape(
                        " I ",
                        "BIB",
                        " I "
                )
                .ingredients(
                        ShapedRecipeBuilder.material('I', Material.RAW_IRON),
                        ShapedRecipeBuilder.material('B', Material.WATER_BUCKET)
                );

        final var rawPlumbumBlock = MSBlockUtils.getItemStack("raw_plumbum_block");

        if (rawPlumbumBlock.isEmpty()) {
            MSLogger.warning(
                    "Can't find custom block with key: raw_plumbum_block! Shaped recipe for RawPlumbum will not be registered!"
            );

            return Collections.singletonList(RecipeEntry.of(shapedBuilder, true));
        }

        return Arrays.asList(
                RecipeEntry.of(shapedBuilder, true),
                RecipeEntry.of(
                        RecipeBuilder.shapedBuilder()
                        .namespacedKey(new NamespacedKey(SharedConstants.MSITEMS_NAMESPACE, "raw_plumbum_from_block"))
                        .result(this.itemStack.clone().add(8))
                        .shape("I")
                        .ingredients(
                                ShapedRecipeBuilder.itemStack('I', rawPlumbumBlock.get())
                        ),
                        true
                )
        );
    }
}
