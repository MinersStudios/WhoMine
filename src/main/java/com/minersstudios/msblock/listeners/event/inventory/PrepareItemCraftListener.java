package com.minersstudios.msblock.listeners.event.inventory;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.CustomBlockRegistry;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSEventListener;
import com.minersstudios.mscore.util.ItemUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
@MSEventListener
public final class PrepareItemCraftListener extends AbstractMSListener<MSBlock> {

    @EventHandler
    public void onPrepareItemCraft(final @NotNull PrepareItemCraftEvent event) {
        final Recipe recipe = event.getRecipe();

        if (recipe == null) {
            return;
        }

        final ItemStack result = recipe.getResult();

        for (final var itemStack : event.getInventory().getMatrix()) {
            if (
                    CustomBlockRegistry.isCustomBlock(itemStack)
                    && (
                            (
                                    recipe instanceof final ShapedRecipe shapedRecipe
                                    && shapedRecipe.getIngredientMap().values().stream()
                                            .noneMatch(item -> ItemUtils.isSimilarItemStacks(item, itemStack))
                            )
                            || (
                                    !result.hasItemMeta()
                                    || !result.getItemMeta().hasCustomModelData()
                            )
                    )
            ) {
                if (!MSDecorUtils.isCustomDecor(event.getInventory().getResult())) {
                    event.getInventory().setResult(ItemStack.empty());
                }
            }
        }
    }
}
