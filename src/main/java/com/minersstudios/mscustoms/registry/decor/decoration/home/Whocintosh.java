package com.minersstudios.mscustoms.registry.decor.decoration.home;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.choice.RecipeChoiceEntry;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.decor.CustomDecorDataImpl;
import com.minersstudios.mscustoms.custom.decor.DecorHitBox;
import com.minersstudios.mscustoms.custom.decor.Facing;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import com.minersstudios.mscustoms.sound.SoundGroup;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

public final class Whocintosh extends CustomDecorDataImpl<Whocintosh> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1371);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Whocintosh"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("whocintosh")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.SOLID)
                        .size(1.0d, 1.0d, 1.0d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.ANVIL)
                .itemStack(itemStack)
                .recipes(
                        unused -> RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "III",
                                        "IGI",
                                        "ITI"
                                )
                                .ingredients(
                                        RecipeChoiceEntry.itemStack('I', CustomItemType.PLUMBUM_INGOT.getCustomItem().getItem()),
                                        RecipeChoiceEntry.material('G', Material.GLASS_PANE),
                                        RecipeChoiceEntry.material('T', Material.REDSTONE_TORCH)
                                ),
                                true
                        )
                );
    }
}
