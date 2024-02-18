package com.minersstudios.mscustoms.registry.item.cards;

import com.minersstudios.mscore.annotation.Key;
import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.choice.RecipeChoiceEntry;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.minersstudios.mscore.utility.ChatUtils.createDefaultStyledText;
import static net.kyori.adventure.text.Component.text;

public interface CardsBicycle {

    private static @NotNull ItemStack createCardItem(
            final int customModelData,
            final @NotNull String name,
            final @NotNull String lore
    ) {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(createDefaultStyledText(name));
        itemMeta.setCustomModelData(customModelData);
        itemMeta.lore(ChatUtils.convertStringsToComponents(ChatUtils.COLORLESS_DEFAULT_STYLE.color(NamedTextColor.GRAY), lore));
        itemMeta.getPersistentDataContainer().set(
                CustomItemType.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                "card_bicycle"
        );
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @SuppressWarnings("UnstableApiUsage")
    final class Blue {
        private static final List<ItemStack> CARD_ITEMS = Arrays.asList(
                //<editor-fold desc="Blue cards" defaultstate="collapsed">
                createCardItem(1260, "Туз треф"),
                createCardItem(1261, "2 треф"),
                createCardItem(1262, "3 треф"),
                createCardItem(1263, "4 треф"),
                createCardItem(1264, "5 треф"),
                createCardItem(1265, "6 треф"),
                createCardItem(1266, "7 треф"),
                createCardItem(1267, "8 треф"),
                createCardItem(1268, "9 треф"),
                createCardItem(1269, "10 треф"),
                createCardItem(1270, "Валет треф"),
                createCardItem(1271, "Дама треф"),
                createCardItem(1272, "Король треф"),
                createCardItem(1273, "Туз червей"),
                createCardItem(1274, "2 червей"),
                createCardItem(1275, "3 червей"),
                createCardItem(1276, "4 червей"),
                createCardItem(1277, "5 червей"),
                createCardItem(1278, "6 червей"),
                createCardItem(1279, "7 червей"),
                createCardItem(1280, "8 червей"),
                createCardItem(1281, "9 червей"),
                createCardItem(1282, "10 червей"),
                createCardItem(1283, "Валет червей"),
                createCardItem(1284, "Дама червей"),
                createCardItem(1285, "Король червей"),
                createCardItem(1286, "Туз пики"),
                createCardItem(1287, "2 пики"),
                createCardItem(1288, "3 пики"),
                createCardItem(1289, "4 пики"),
                createCardItem(1290, "5 пики"),
                createCardItem(1291, "6 пики"),
                createCardItem(1292, "7 пики"),
                createCardItem(1293, "8 пики"),
                createCardItem(1294, "9 пики"),
                createCardItem(1295, "10 пики"),
                createCardItem(1296, "Валет пики"),
                createCardItem(1297, "Дама пики"),
                createCardItem(1298, "Король пики"),
                createCardItem(1299, "Туз бубен"),
                createCardItem(1300, "2 бубен"),
                createCardItem(1301, "3 бубен"),
                createCardItem(1302, "4 бубен"),
                createCardItem(1303, "5 бубен"),
                createCardItem(1304, "6 бубен"),
                createCardItem(1305, "7 бубен"),
                createCardItem(1306, "8 бубен"),
                createCardItem(1307, "9 бубен"),
                createCardItem(1308, "10 бубен"),
                createCardItem(1309, "Валет бубен"),
                createCardItem(1310, "Дама бубен"),
                createCardItem(1311, "Король бубен"),
                createCardItem(1312, "Чёрный джокер"),
                createCardItem(1313, "Красный джокер")
                //</editor-fold>
        );

        public static @NotNull @Unmodifiable List<ItemStack> cardItems() {
            return Collections.unmodifiableList(CARD_ITEMS);
        }
        
        private static @NotNull ItemStack createCardItem(
                final int customModelData,
                final @NotNull String name
        ) {
            return CardsBicycle.createCardItem(
                    customModelData, 
                    name, 
                    "Синяя колода карт \"Bicycle\""
            );
        }

        public static final class First extends CustomItemImpl implements CardsBicycle {
            private static final @Key String KEY;
            private static final ItemStack ITEM_STACK;

            static {
                KEY = "box_blue_1_card_box_bicycle";
                ITEM_STACK = new ItemStack(Material.BUNDLE);
                final BundleMeta bundleMeta = (BundleMeta) ITEM_STACK.getItemMeta();

                bundleMeta.displayName(
                        createDefaultStyledText("Синяя колода карт ")
                        .append(
                                text("\"Bicycle\"")
                                .style(ChatUtils.DEFAULT_STYLE)
                        )
                        .decorate(TextDecoration.BOLD)
                );
                bundleMeta.setCustomModelData(1);
                bundleMeta.setItems(CARD_ITEMS);
                ITEM_STACK.setItemMeta(bundleMeta);
            }

            public First() {
                super(KEY, ITEM_STACK);
            }

            @Contract(" -> new")
            @Override
            public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
                return Collections.singletonList(
                        RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .namespacedKey(this.namespacedKey)
                                .result(this.itemStack)
                                .shape(
                                        "DSD",
                                        "SPS",
                                        "DSD"
                                )
                                .ingredients(
                                        RecipeChoiceEntry.material('D', Material.BLUE_DYE),
                                        RecipeChoiceEntry.material('S', Material.STRING),
                                        RecipeChoiceEntry.material('P', Material.PAPER)
                                ),
                                true
                        )
                );
            }
        }

        public static final class Second extends CustomItemImpl implements CardsBicycle {
            private static final @Key String KEY;
            private static final ItemStack ITEM_STACK;

            static {
                KEY = "box_blue_2_card_box_bicycle";
                ITEM_STACK = new ItemStack(Material.BUNDLE);
                final BundleMeta bundleMeta = (BundleMeta) ITEM_STACK.getItemMeta();

                bundleMeta.displayName(
                        createDefaultStyledText("Синяя колода карт ")
                        .append(
                                text("\"Bicycle\"")
                                .style(ChatUtils.DEFAULT_STYLE)
                        )
                        .decorate(TextDecoration.BOLD)
                );
                bundleMeta.setCustomModelData(2);
                bundleMeta.setItems(CARD_ITEMS);
                ITEM_STACK.setItemMeta(bundleMeta);
            }

            public Second() {
                super(KEY, ITEM_STACK);
            }

            @Contract(" -> new")
            @Override
            public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
                return Collections.singletonList(
                        RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .namespacedKey(this.namespacedKey)
                                .result(this.itemStack)
                                .shape(
                                        "DSD",
                                        "SPS",
                                        "ISI"
                                )
                                .ingredients(
                                        RecipeChoiceEntry.material('D', Material.BLUE_DYE),
                                        RecipeChoiceEntry.material('S', Material.STRING),
                                        RecipeChoiceEntry.material('P', Material.PAPER),
                                        RecipeChoiceEntry.material('I', Material.IRON_INGOT)
                                ),
                                true
                        )
                );
            }
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    final class Red {
        private static final List<ItemStack> CARD_ITEMS = Arrays.asList(
                //<editor-fold desc="Red cards" defaultstate="collapsed">
                createCardItem(1314, "Туз треф"),
                createCardItem(1315, "2 треф"),
                createCardItem(1316, "3 треф"),
                createCardItem(1318, "4 треф"),
                createCardItem(1319, "5 треф"),
                createCardItem(1320, "6 треф"),
                createCardItem(1321, "7 треф"),
                createCardItem(1322, "8 треф"),
                createCardItem(1323, "9 треф"),
                createCardItem(1324, "10 треф"),
                createCardItem(1325, "Валет треф"),
                createCardItem(1326, "Дама треф"),
                createCardItem(1327, "Король треф"),
                createCardItem(1328, "Туз червей"),
                createCardItem(1329, "2 червей"),
                createCardItem(1330, "3 червей"),
                createCardItem(1331, "4 червей"),
                createCardItem(1332, "5 червей"),
                createCardItem(1333, "6 червей"),
                createCardItem(1334, "7 червей"),
                createCardItem(1335, "8 червей"),
                createCardItem(1336, "9 червей"),
                createCardItem(1337, "10 червей"),
                createCardItem(1338, "Валет червей"),
                createCardItem(1339, "Дама червей"),
                createCardItem(1340, "Король червей"),
                createCardItem(1341, "Туз пики"),
                createCardItem(1342, "2 пики"),
                createCardItem(1343, "3 пики"),
                createCardItem(1344, "4 пики"),
                createCardItem(1345, "5 пики"),
                createCardItem(1346, "6 пики"),
                createCardItem(1347, "7 пики"),
                createCardItem(1348, "8 пики"),
                createCardItem(1349, "9 пики"),
                createCardItem(1350, "10 пики"),
                createCardItem(1351, "Валет пики"),
                createCardItem(1352, "Дама пики"),
                createCardItem(1353, "Король пики"),
                createCardItem(1354, "Туз бубен"),
                createCardItem(1355, "2 бубен"),
                createCardItem(1356, "3 бубен"),
                createCardItem(1357, "4 бубен"),
                createCardItem(1358, "5 бубен"),
                createCardItem(1359, "6 бубен"),
                createCardItem(1360, "7 бубен"),
                createCardItem(1361, "8 бубен"),
                createCardItem(1362, "9 бубен"),
                createCardItem(1363, "10 бубен"),
                createCardItem(1364, "Валет бубен"),
                createCardItem(1365, "Дама бубен"),
                createCardItem(1366, "Король бубен"),
                createCardItem(1367, "Чёрный джокер"),
                createCardItem(1368, "Красный джокер")
                //</editor-fold>
        );

        public static @NotNull @Unmodifiable List<ItemStack> cardItems() {
            return Collections.unmodifiableList(CARD_ITEMS);
        }

        private static @NotNull ItemStack createCardItem(
                final int customModelData,
                final @NotNull String name
        ) {
            return CardsBicycle.createCardItem(
                    customModelData,
                    name,
                    "Красная колода карт \"Bicycle\""
            );
        }
        
        public static final class First extends CustomItemImpl implements CardsBicycle {
            private static final @Key String KEY;
            private static final ItemStack ITEM_STACK;

            static {
                KEY = "box_red_1_card_box_bicycle";
                ITEM_STACK = new ItemStack(Material.BUNDLE);
                final BundleMeta bundleMeta = (BundleMeta) ITEM_STACK.getItemMeta();

                bundleMeta.displayName(
                        createDefaultStyledText("Красная колода карт ")
                        .append(
                                text("\"Bicycle\"")
                                .style(ChatUtils.DEFAULT_STYLE)
                        )
                        .decorate(TextDecoration.BOLD)
                );
                bundleMeta.setCustomModelData(3);
                bundleMeta.setItems(CARD_ITEMS);
                ITEM_STACK.setItemMeta(bundleMeta);
            }

            public First() {
                super(KEY, ITEM_STACK);
            }

            @Contract(" -> new")
            @Override
            public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
                return Collections.singletonList(
                        RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .namespacedKey(this.namespacedKey)
                                .result(this.itemStack)
                                .shape(
                                        "DSD",
                                        "SPS",
                                        "DSD"
                                )
                                .ingredients(
                                        RecipeChoiceEntry.material('D', Material.RED_DYE),
                                        RecipeChoiceEntry.material('S', Material.STRING),
                                        RecipeChoiceEntry.material('P', Material.PAPER)
                                ),
                                true
                        )
                );
            }
        }

        public static final class Second extends CustomItemImpl implements CardsBicycle {
            private static final @Key String KEY;
            private static final ItemStack ITEM_STACK;

            static {
                KEY = "box_red_2_card_box_bicycle";
                ITEM_STACK = new ItemStack(Material.BUNDLE);
                final BundleMeta bundleMeta = (BundleMeta) ITEM_STACK.getItemMeta();

                bundleMeta.displayName(
                        createDefaultStyledText("Красная колода карт ")
                                .append(text("\"Bicycle\"")
                                        .style(ChatUtils.DEFAULT_STYLE))
                                .decorate(TextDecoration.BOLD)
                );
                bundleMeta.setCustomModelData(4);
                bundleMeta.setItems(CARD_ITEMS);
                ITEM_STACK.setItemMeta(bundleMeta);
            }

            public Second() {
                super(KEY, ITEM_STACK);
            }

            @Contract(" -> new")
            @Override
            public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
                return Collections.singletonList(
                        RecipeEntry.fromBuilder(
                                RecipeBuilder.shaped()
                                .namespacedKey(this.namespacedKey)
                                .result(this.itemStack)
                                .shape(
                                        "DSD",
                                        "SPS",
                                        "ISI"
                                )
                                .ingredients(
                                        RecipeChoiceEntry.material('D', Material.RED_DYE),
                                        RecipeChoiceEntry.material('S', Material.STRING),
                                        RecipeChoiceEntry.material('P', Material.PAPER),
                                        RecipeChoiceEntry.material('I', Material.IRON_INGOT)
                                ),
                                true
                        )
                );
            }
        }
    }
}
