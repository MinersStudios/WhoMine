package com.minersstudios.wholib.paper.custom.item.registry;

import com.minersstudios.wholib.key.Key;
import com.minersstudios.wholib.key.Resource;
import com.minersstudios.wholib.paper.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.wholib.paper.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.wholib.paper.utility.MSLogger;
import com.minersstudios.wholib.utility.ChatUtils;
import com.minersstudios.wholib.paper.custom.item.CustomItemImpl;
import com.minersstudios.wholib.paper.utility.MSBlockUtils;
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
    private static final @Key String KEY;
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
                RecipeBuilder.shaped()
                .namespacedKey(this.namespacedKey)
                .result(this.itemStack)
                .shape(
                        " I ",
                        "BIB",
                        " I "
                )
                .ingredients(
                        RecipeChoiceEntry.material('I', Material.RAW_IRON),
                        RecipeChoiceEntry.material('B', Material.WATER_BUCKET)
                );

        final var rawPlumbumBlock = MSBlockUtils.getItemStack("raw_plumbum_block");

        if (rawPlumbumBlock.isEmpty()) {
            MSLogger.warning(
                    "Can't find custom block with key: raw_plumbum_block! Shaped recipe for RawPlumbum will not be registered!"
            );

            return Collections.singletonList(RecipeEntry.fromBuilder(shapedBuilder, true));
        }

        return Arrays.asList(
                RecipeEntry.fromBuilder(shapedBuilder, true),
                RecipeEntry.fromBuilder(
                        RecipeBuilder.shaped()
                        .namespacedKey(new NamespacedKey(Resource.WMITEM, "raw_plumbum_from_block"))
                        .result(this.itemStack.clone().add(8))
                        .shape("I")
                        .ingredients(
                                RecipeChoiceEntry.itemStack('I', rawPlumbumBlock.get())
                        ),
                        true
                )
        );
    }
}
