package com.minersstudios.msdecor.registry.furniture;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.msdecor.api.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class Nightstand<C extends CustomDecorData<C>> extends CustomDecorDataImpl<C> {

    protected final @NotNull Builder createBuilder(
            final @NotNull String key,
            final int customModelData,
            final int cmdSecondLeft,
            final int cmdSecondMiddle,
            final int cmdSecondRight,
            final int cmdThirdLeft,
            final int cmdThirdMiddle,
            final int cmdThirdRight,
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
                        Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .group(CustomDecorType.NAMESPACE + ":nightstand")
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "PPP",
                                        "PPP",
                                        "P P"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('P', planksMaterial)
                                ),
                                true
                        )
                )
                .parameters(DecorParameter.WRENCHABLE)
                .types(
                        builder -> new Type(builder, "second_left",   createItem(itemStack, cmdSecondLeft)),
                        builder -> new Type(builder, "second_middle", createItem(itemStack, cmdSecondMiddle)),
                        builder -> new Type(builder, "second_right",  createItem(itemStack, cmdSecondRight)),
                        builder -> new Type(builder, "third_left",    createItem(itemStack, cmdThirdLeft)),
                        builder -> new Type(builder, "third_middle",  createItem(itemStack, cmdThirdMiddle)),
                        builder -> new Type(builder, "third_right",   createItem(itemStack, cmdThirdRight))
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

    public static final class Acacia extends Nightstand<Acacia> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "acacia_nightstand",
                    1086,
                    1087, 1088, 1089,
                    1090, 1091, 1092,
                    "Акациевая тумбочка",
                    Material.ACACIA_PLANKS
            );
        }
    }

    public static final class Birch extends Nightstand<Birch> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "birch_nightstand",
                    1093,
                    1094, 1095, 1096,
                    1097, 1098, 1099,
                    "Берёзовая тумбочка",
                    Material.BIRCH_PLANKS
            );
        }
    }

    public static final class Cherry extends Nightstand<Cherry> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "cherry_nightstand",
                    1386,
                    1387, 1388, 1389,
                    1390, 1391, 1392,
                    "Вишнёвая тумбочка",
                    Material.CHERRY_PLANKS
            );
        }
    }

    public static final class Crimson extends Nightstand<Crimson> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "crimson_nightstand",
                    1100,
                    1101, 1102, 1103,
                    1104, 1105, 1106,
                    "Багровая тумбочка",
                    Material.CRIMSON_PLANKS
            );
        }
    }

    public static final class DarkOak extends Nightstand<DarkOak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "dark_oak_nightstand",
                    1107,
                    1108, 1109, 1110,
                    1111, 1112, 1113,
                    "Тумбочка из тёмного дуба",
                    Material.DARK_OAK_PLANKS
            );
        }
    }

    public static final class Jungle extends Nightstand<Jungle> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "jungle_nightstand",
                    1114,
                    1115, 1116, 1117,
                    1118, 1119, 1120,
                    "Тропическая тумбочка",
                    Material.JUNGLE_PLANKS
            );
        }
    }

    public static final class Mangrove extends Nightstand<Mangrove> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "mangrove_nightstand",
                    1203,
                    1204, 1205, 1206,
                    1207, 1208, 1209,
                    "Мангровая тумбочка",
                    Material.MANGROVE_PLANKS
            );
        }
    }

    public static final class Oak extends Nightstand<Oak> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "oak_nightstand",
                    1121,
                    1122, 1123, 1124,
                    1125, 1126, 1127,
                    "Дубовая тумбочка",
                    Material.OAK_PLANKS
            );
        }
    }

    public static final class Spruce extends Nightstand<Spruce> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "spruce_nightstand",
                    1128,
                    1129, 1130, 1131,
                    1132, 1133, 1134,
                    "Еловая тумбочка",
                    Material.SPRUCE_PLANKS
            );
        }
    }

    public static final class Warped extends Nightstand<Warped> {

        @Override
        protected @NotNull Builder builder() {
            return this.createBuilder(
                    "warped_nightstand",
                    1135,
                    1136, 1137, 1138,
                    1139, 1140, 1141,
                    "Искажённая тумбочка",
                    Material.WARPED_PLANKS
            );
        }
    }
}
