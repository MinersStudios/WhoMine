package com.minersstudios.mscustoms.registry.item;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.utility.MSBlockUtils;
import com.minersstudios.mscore.utility.SharedConstants;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;

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
    public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
        final ItemStack input = CustomItemType.RAW_PLUMBUM.getCustomItem().getItem();
        final var furnaceBuilder =
                RecipeBuilder.furnaceBuilder()
                .namespacedKey(new NamespacedKey(SharedConstants.MSITEMS_NAMESPACE, "plumbum_ingot_furnace"))
                .result(this.itemStack)
                .ingredient(new RecipeChoice.ExactChoice(input))
                .experience(0.7f)
                .cookingTime(200);
        final var blastingBuilder =
                RecipeBuilder.blastingBuilder()
                .namespacedKey(new NamespacedKey(SharedConstants.MSITEMS_NAMESPACE, "plumbum_ingot_blast"))
                .result(this.itemStack)
                .ingredient(new RecipeChoice.ExactChoice(input))
                .experience(0.7f)
                .cookingTime(100);

        final var plumbumBlock = MSBlockUtils.getItemStack("plumbum_block");

        if (plumbumBlock.isEmpty()) {
            MSCustoms.logger().warning(
                    "Can't find custom block with key: plumbum_block! Shaped recipe will not be registered!"
            );

            return Arrays.asList(
                    RecipeEntry.of(furnaceBuilder),
                    RecipeEntry.of(blastingBuilder)
            );
        }

        return Arrays.asList(
                RecipeEntry.of(furnaceBuilder),
                RecipeEntry.of(blastingBuilder),
                RecipeEntry.of(
                        RecipeBuilder.shapedBuilder()
                        .namespacedKey(new NamespacedKey(SharedConstants.MSITEMS_NAMESPACE, "plumbum_ingot_from_block"))
                        .result(this.itemStack.clone().add(8))
                        .shape("I")
                        .ingredients(
                                ShapedRecipeBuilder.itemStack('I', plumbumBlock.get())
                        ),
                        true
                )
        );
    }
}
