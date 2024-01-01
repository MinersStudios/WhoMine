package com.minersstudios.mscustoms.listener.event.inventory;

import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlockRegistry;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.mscore.utility.ItemUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
@EventListener
public final class PrepareItemCraftListener extends AbstractEventListener<MSCustoms> {

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
                event.getInventory().setResult(ItemStack.empty());
            }
        }
    }
}
