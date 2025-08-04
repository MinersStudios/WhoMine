package com.minersstudios.wholib.paper.custom.decor.registry.decoration.home;

import com.minersstudios.wholib.paper.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.wholib.paper.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.wholib.utility.ChatUtils;
import com.minersstudios.wholib.paper.custom.decor.CustomDecorDataImpl;
import com.minersstudios.wholib.paper.custom.decor.DecorHitBox;
import com.minersstudios.wholib.paper.custom.decor.Facing;
import com.minersstudios.wholib.paper.world.sound.SoundGroup;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

public final class Patefon extends CustomDecorDataImpl<Patefon> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1147);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Патефон"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .path("patefon")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .size(0.75d, 0.5d, 0.75d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.ANVIL)
                .itemStack(itemStack)
                .recipes(
                        unused -> RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .category(CraftingBookCategory.BUILDING)
                                .shape("PJP")
                                .ingredients(
                                        RecipeChoiceEntry.material('J', Material.JUKEBOX),
                                        RecipeChoiceEntry.material('P', Material.SPRUCE_PLANKS)
                                ),
                                true
                        )
                );
    }
}
