package com.minersstudios.wholib.paper.custom.decor.registry.decoration.home.plush;

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

public final class WhocintoshMini extends CustomDecorDataImpl<WhocintoshMini> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1370);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Мини-Whocintosh"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .path("whocintosh_mini")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.NONE)
                        .facings(
                                Facing.FLOOR,
                                Facing.WALL
                        )
                        .wallDirected(true)
                        .size(0.5d, 0.5625d, 0.5d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOL)
                .itemStack(itemStack)
                .recipes(
                        unused -> RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "WWW",
                                        "WGW"
                                )
                                .ingredients(
                                        RecipeChoiceEntry.material('W', Material.GRAY_WOOL),
                                        RecipeChoiceEntry.material('G', Material.GLASS_PANE)
                                ),
                                true
                        )
                );
    }
}
