package com.minersstudios.msdecor.registry.decoration.home;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.api.CustomDecorDataImpl;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msdecor.api.DecorParameter;
import com.minersstudios.msdecor.api.Facing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class CookingPot extends CustomDecorDataImpl<CookingPot> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1164);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Кастрюля"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("cooking_pot")
                .hitBox(new DecorHitBox(
                        0.75d,
                        0.625d,
                        0.75d,
                        DecorHitBox.Type.NONE
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.ANVIL)
                .itemStack(itemStack)
                .recipes(
                        Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "ISI",
                                        "III"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('I', Material.IRON_INGOT),
                                        ShapedRecipeBuilder.material('S', Material.STICK)
                                ),
                                true
                        )
                )
                .parameters(DecorParameter.TYPED)
                .types(
                        builder -> new Type(
                                builder,
                                "honey",
                                createItem(itemStack, 1165)
                        ),
                        builder -> new Type(
                                builder,
                                "honey_1",
                                createItem(itemStack, 1166)
                        ),
                        builder -> new Type(
                                builder,
                                "honey_2",
                                createItem(itemStack, 1167)
                        ),
                        builder -> new Type(
                                builder,
                                "portal",
                                createItem(itemStack, 1168)
                        ),
                        builder -> new Type(
                                builder,
                                "lava",
                                createItem(itemStack, 1169)
                        ),
                        builder -> new Type(
                                builder,
                                "snow",
                                createItem(itemStack, 1170)
                        ),
                        builder -> new Type(
                                builder,
                                "water",
                                createItem(itemStack, 1171)
                        ),
                        builder -> new Type(
                                builder,
                                "water_1",
                                createItem(itemStack, 1172)
                        ),
                        builder -> new Type(
                                builder,
                                "water_2",
                                createItem(itemStack, 1173)
                        )
                );
    }

    private static @NotNull ItemStack createItem(
            final @NotNull ItemStack parent,
            final int customModelData
    ) {
        final ItemStack itemStack = parent.clone();
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
