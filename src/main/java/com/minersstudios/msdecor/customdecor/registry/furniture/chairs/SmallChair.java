package com.minersstudios.msdecor.customdecor.registry.furniture.chairs;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.customdecor.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class SmallChair<C extends CustomDecorData<C>> extends CustomDecorDataImpl<C> {

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
                .hitBox(new DecorHitBox(
                        1.0d,
                        1.0d,
                        1.0d,
                        DecorHitBox.Type.BARRIER
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack)
                .recipes(
                        builder -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .namespacedKey(builder.key())
                                .group(CustomDecorType.NAMESPACE + ":small_chair")
                                .category(CraftingBookCategory.BUILDING)
                                .result(builder.itemStack())
                                .shape(
                                        "PLP",
                                        "PAP"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('P', planksMaterial),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER),
                                        ShapedRecipeBuilder.material('A', Material.AIR)
                                )
                                .build(),
                                true
                        )
                )
                .parameters(DecorParameter.SITTABLE)
                .sitHeight(0.75d);
    }

    public static final class Acacia extends SmallChair<Acacia> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "acacia_small_chair",
                    1000,
                    "Акациевый стул",
                    Material.ACACIA_PLANKS
            );
        }
    }

    public static final class Birch extends SmallChair<Birch> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "birch_small_chair",
                    1001,
                    "Берёзовый стул",
                    Material.BIRCH_PLANKS
            );
        }
    }

    public static final class Cherry extends SmallChair<Cherry> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "cherry_small_chair",
                    1376,
                    "Вишнёвый стул",
                    Material.CHERRY_PLANKS
            );
        }
    }

    public static final class Crimson extends SmallChair<Crimson> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "crimson_small_chair",
                    1002,
                    "Багровый стул",
                    Material.CRIMSON_PLANKS
            );
        }
    }

    public static final class DarkOak extends SmallChair<DarkOak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "dark_oak_small_chair",
                    1003,
                    "Стул из тёмного дуба",
                    Material.DARK_OAK_PLANKS
            );
        }
    }

    public static final class Jungle extends SmallChair<Jungle> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "jungle_small_chair",
                    1004,
                    "Тропический стул",
                    Material.JUNGLE_PLANKS
            );
        }
    }

    public static final class Mangrove extends SmallChair<Mangrove> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "mangrove_small_chair",
                    1193,
                    "Мангровый стул",
                    Material.MANGROVE_PLANKS
            );
        }
    }

    public static final class Oak extends SmallChair<Oak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "oak_small_chair",
                    1005,
                    "Дубовый стул",
                    Material.OAK_PLANKS
            );
        }
    }

    public static final class Spruce extends SmallChair<Spruce> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "spruce_small_chair",
                    1006,
                    "Еловый стул",
                    Material.SPRUCE_PLANKS
            );
        }
    }

    public static final class Warped extends SmallChair<Warped> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "warped_small_chair",
                    1007,
                    "Искажённый стул",
                    Material.WARPED_PLANKS
            );
        }
    }
}
