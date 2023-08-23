package com.minersstudios.msblock.listeners.event.inventory;

import com.minersstudios.msblock.customblock.CustomBlockRegistry;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.mscore.util.ItemUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
@MSListener
public class PrepareItemCraftListener extends AbstractMSListener {

    @EventHandler
    public void onPrepareItemCraft(final @NotNull PrepareItemCraftEvent event) {
        final Recipe recipe = event.getRecipe();
        if (recipe == null) return;
        final ItemStack result = recipe.getResult();

        for (final var itemStack : event.getInventory().getMatrix()) {
            if (
                    CustomBlockRegistry.isCustomBlock(itemStack)
                    && ((recipe instanceof final ShapedRecipe shapedRecipe
                    && shapedRecipe.getIngredientMap().values().stream().noneMatch(item -> ItemUtils.isSimilarItemStacks(item, itemStack)))
                    || (!result.hasItemMeta() || !result.getItemMeta().hasCustomModelData()))
            ) {
                if (MSDecorUtils.isCustomDecor(event.getInventory().getResult())) return;
                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }
}
