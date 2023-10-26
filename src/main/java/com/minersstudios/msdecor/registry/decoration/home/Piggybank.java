package com.minersstudios.msdecor.registry.decoration.home;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.api.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class Piggybank<C extends CustomDecorData<C>> extends CustomDecorDataImpl<C> {

    protected final @NotNull Builder createBuilder(
            final @NotNull String key,
            final int customModelData,
            final @NotNull String displayName,
            final @NotNull Material material
    ) {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(customModelData);
        itemMeta.displayName(ChatUtils.createDefaultStyledText(displayName));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key(key)
                .hitBox(new DecorHitBox(
                        1.0d,
                        0.675d,
                        1.0d,
                        DecorHitBox.Type.NONE
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.GLASS)
                .itemStack(itemStack)
                .recipes(
                        builder -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .namespacedKey(builder.key())
                                .group(CustomDecorType.NAMESPACE + ":piggybank")
                                .category(CraftingBookCategory.BUILDING)
                                .result(builder.itemStack())
                                .shape(
                                        "  P",
                                        "PPP",
                                        "P P"
                                )
                                .ingredients(ShapedRecipeBuilder.material('P', material))
                                .build(),
                                true
                        )
                );
    }

    public static final class Clay extends Piggybank<Clay> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "clay_piggybank",
                    1155,
                    "Керамическая копилка",
                    Material.CLAY
            );
        }
    }

    public static final class Diamond extends Piggybank<Diamond> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "diamond_piggybank",
                    1156,
                    "Алмазная копилка",
                    Material.DIAMOND_BLOCK
            );
        }
    }

    public static final class Emerald extends Piggybank<Emerald> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "emerald_piggybank",
                    1157,
                    "Изумрудная копилка",
                    Material.EMERALD_BLOCK
            );
        }
    }

    public static final class Gold extends Piggybank<Gold> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "gold_piggybank",
                    1158,
                    "Золотая копилка",
                    Material.GOLD_BLOCK
            );
        }
    }

    public static final class Iron extends Piggybank<Iron> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "iron_piggybank",
                    1159,
                    "Железная копилка",
                    Material.IRON_BLOCK
            );
        }
    }

    public static final class Netherite extends Piggybank<Netherite> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "netherite_piggybank",
                    1160,
                    "Незеритовая копилка",
                    Material.NETHERITE_BLOCK
            );
        }
    }
}
