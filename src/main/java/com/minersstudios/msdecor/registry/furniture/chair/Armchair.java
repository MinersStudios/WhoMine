package com.minersstudios.msdecor.registry.furniture.chair;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msdecor.api.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class Armchair<C extends CustomDecorData<C>> extends CustomDecorDataImpl<C> {

    protected final @NotNull Builder createBuilder(
            final @NotNull String key,
            final int customModelData,
            final @NotNull String displayName,
            final @NotNull Material planksMaterial
    ) {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(customModelData);
        itemMeta.displayName(ChatUtils.createDefaultStyledText(displayName));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key(key)
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.SOLID)
                        .size(1.0d, 1.0d, 1.0d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack)
                .recipes(
                        unused -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .group(MSDecor.NAMESPACE + ":armchair")
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "PP ",
                                        "PLP",
                                        "P P"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('P', planksMaterial),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                ),
                                true
                        )
                )
                .parameters(
                        DecorParameter.SITTABLE,
                        DecorParameter.PAINTABLE
                )
                .sitHeight(0.6d);
    }

    public static final class Acacia extends Armchair<Acacia> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "acacia_armchair",
                    1028,
                    "Акациевое кресло",
                    Material.ACACIA_PLANKS
            );
        }
    }

    public static final class Birch extends Armchair<Birch> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "birch_armchair",
                    1029,
                    "Берёзовое кресло",
                    Material.BIRCH_PLANKS
            );
        }
    }

    public static final class Cherry extends Armchair<Cherry> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "cherry_armchair",
                    1379,
                    "Вишнёвое кресло",
                    Material.CHERRY_PLANKS
            );
        }
    }

    public static final class Crimson extends Armchair<Crimson> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "crimson_armchair",
                    1030,
                    "Багровое кресло",
                    Material.CRIMSON_PLANKS
            );
        }
    }

    public static final class DarkOak extends Armchair<DarkOak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "dark_oak_armchair",
                    1031,
                    "Кресло из тёмного дуба",
                    Material.DARK_OAK_PLANKS
            );
        }
    }

    public static final class Jungle extends Armchair<Jungle> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "jungle_armchair",
                    1032,
                    "Тропическое кресло",
                    Material.JUNGLE_PLANKS
            );
        }
    }

    public static final class Mangrove extends Armchair<Mangrove> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "mangrove_armchair",
                    1196,
                    "Мангровое кресло",
                    Material.MANGROVE_PLANKS
            );
        }
    }

    public static final class Oak extends Armchair<Oak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "oak_armchair",
                    1033,
                    "Дубовое кресло",
                    Material.OAK_PLANKS
            );
        }
    }

    public static final class Spruce extends Armchair<Spruce> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "spruce_armchair",
                    1034,
                    "Еловое кресло",
                    Material.SPRUCE_PLANKS
            );
        }
    }

    public static final class Warped extends Armchair<Warped> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "warped_armchair",
                    1035,
                    "Искажённое кресло",
                    Material.WARPED_PLANKS
            );
        }
    }
}
