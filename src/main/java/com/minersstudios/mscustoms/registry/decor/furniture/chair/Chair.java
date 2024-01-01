package com.minersstudios.mscustoms.registry.decor.furniture.chair;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.mscore.utility.SharedConstants;
import com.minersstudios.mscustoms.custom.decor.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class Chair<C extends CustomDecorData<C>> extends CustomDecorDataImpl<C> {

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
                                .group(SharedConstants.MSDECOR_NAMESPACE + ":chair")
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "P  ",
                                        "PLP",
                                        "P P"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('P', planksMaterial),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                ),
                                Boolean.TRUE
                        )
                )
                .parameters(
                        DecorParameter.SITTABLE,
                        DecorParameter.PAINTABLE
                )
                .sitHeight(0.65d);
    }

    public static final class Acacia extends Chair<Acacia> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "acacia_chair",
                    1008,
                    "Акациевый стул со спинкой",
                    Material.ACACIA_PLANKS
            );
        }
    }

    public static final class Birch extends Chair<Birch> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "birch_chair",
                    1009,
                    "Берёзовый стул со спинкой",
                    Material.BIRCH_PLANKS
            );
        }
    }

    public static final class Cherry extends Chair<Cherry> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "cherry_chair",
                    1377,
                    "Вишнёвый стул со спинкой",
                    Material.CHERRY_PLANKS
            );
        }
    }

    public static final class Crimson extends Chair<Crimson> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "crimson_chair",
                    1010,
                    "Багровый стул со спинкой",
                    Material.CRIMSON_PLANKS
            );
        }
    }

    public static final class DarkOak extends Chair<DarkOak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "dark_oak_chair",
                    1011,
                    "Стул из тёмного дуба со спинкой",
                    Material.DARK_OAK_PLANKS
            );
        }
    }

    public static final class Jungle extends Chair<Jungle> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "jungle_chair",
                    1012,
                    "Тропический стул со спинкой",
                    Material.JUNGLE_PLANKS
            );
        }
    }

    public static final class Mangrove extends Chair<Mangrove> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "mangrove_chair",
                    1194,
                    "Мангровый стул со спинкой",
                    Material.MANGROVE_PLANKS
            );
        }
    }

    public static final class Oak extends Chair<Oak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "oak_chair",
                    1013,
                    "Дубовый стул со спинкой",
                    Material.OAK_PLANKS
            );
        }
    }

    public static final class Spruce extends Chair<Spruce> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "spruce_chair",
                    1014,
                    "Еловый стул со спинкой",
                    Material.SPRUCE_PLANKS
            );
        }
    }

    public static final class Warped extends Chair<Warped> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "warped_chair",
                    1015,
                    "Искажённый стул со спинкой",
                    Material.WARPED_PLANKS
            );
        }
    }
}
