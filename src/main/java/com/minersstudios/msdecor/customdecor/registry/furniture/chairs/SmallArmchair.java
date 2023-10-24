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

public abstract class SmallArmchair<C extends CustomDecorData<C>> extends CustomDecorDataImpl<C> {

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
                                .group(CustomDecorType.NAMESPACE + ":small_armchair")
                                .category(CraftingBookCategory.BUILDING)
                                .result(builder.itemStack())
                                .shape(
                                        "LP ",
                                        "PLP",
                                        "P P"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('P', planksMaterial),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                )
                                .build(),
                                true
                        )
                )
                .parameters(DecorParameter.SITTABLE)
                .sitHeight(0.6d);
    }

    public static final class Acacia extends SmallArmchair<Acacia> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "acacia_small_armchair",
                    1020,
                    "Акациевое маленькое кресло",
                    Material.ACACIA_PLANKS
            );
        }
    }

    public static final class Birch extends SmallArmchair<Birch> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "birch_small_armchair",
                    1021,
                    "Берёзовое маленькое кресло",
                    Material.BIRCH_PLANKS
            );
        }
    }

    public static final class Cherry extends SmallArmchair<Cherry> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "cherry_small_armchair",
                    1378,
                    "Вишнёвое маленькое кресло",
                    Material.CHERRY_PLANKS
            );
        }
    }

    public static final class Crimson extends SmallArmchair<Crimson> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "crimson_small_armchair",
                    1022,
                    "Багровое маленькое кресло",
                    Material.CRIMSON_PLANKS
            );
        }
    }

    public static final class DarkOak extends SmallArmchair<DarkOak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "dark_oak_small_armchair",
                    1023,
                    "Маленькое кресло из тёмного дуба",
                    Material.DARK_OAK_PLANKS
            );
        }
    }

    public static final class Jungle extends SmallArmchair<Jungle> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "jungle_small_armchair",
                    1024,
                    "Тропическое маленькое кресло",
                    Material.JUNGLE_PLANKS
            );
        }
    }

    public static final class Mangrove extends SmallArmchair<Mangrove> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "mangrove_small_armchair",
                    1195,
                    "Мангровое маленькое кресло",
                    Material.MANGROVE_PLANKS
            );
        }
    }

    public static final class Oak extends SmallArmchair<Oak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "oak_small_armchair",
                    1025,
                    "Дубовое маленькое кресло",
                    Material.OAK_PLANKS
            );
        }
    }

    public static final class Spruce extends SmallArmchair<Spruce> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "spruce_small_armchair",
                    1026,
                    "Еловое маленькое кресло",
                    Material.SPRUCE_PLANKS
            );
        }
    }

    public static final class Warped extends SmallArmchair<Warped> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "warped_small_armchair",
                    1027,
                    "Искажённое маленькое кресло",
                    Material.WARPED_PLANKS
            );
        }
    }
}
