package com.minersstudios.msitem.registry.item.cards;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msitem.api.CustomItemImpl;
import com.minersstudios.msitem.api.CustomItemType;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.minersstudios.mscore.utility.ChatUtils.createDefaultStyledText;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
public interface CardsBicycle {
    List<ItemStack> BLUE_CARD_ITEMS = ImmutableList.of(
            //<editor-fold desc="Blue cards" defaultstate="collapsed">
            createCardItem(1260, "Туз треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1261, "2 треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1262, "3 треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1263, "4 треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1264, "5 треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1265, "6 треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1266, "7 треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1267, "8 треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1268, "9 треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1269, "10 треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1270, "Валет треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1271, "Дама треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1272, "Король треф", "Синяя колода карт \"Bicycle\""),
            createCardItem(1273, "Туз червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1274, "2 червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1275, "3 червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1276, "4 червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1277, "5 червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1278, "6 червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1279, "7 червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1280, "8 червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1281, "9 червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1282, "10 червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1283, "Валет червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1284, "Дама червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1285, "Король червей", "Синяя колода карт \"Bicycle\""),
            createCardItem(1286, "Туз пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1287, "2 пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1288, "3 пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1289, "4 пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1290, "5 пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1291, "6 пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1292, "7 пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1293, "8 пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1294, "9 пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1295, "10 пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1296, "Валет пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1297, "Дама пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1298, "Король пики", "Синяя колода карт \"Bicycle\""),
            createCardItem(1299, "Туз бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1300, "2 бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1301, "3 бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1302, "4 бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1303, "5 бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1304, "6 бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1305, "7 бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1306, "8 бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1307, "9 бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1308, "10 бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1309, "Валет бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1310, "Дама бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1311, "Король бубен", "Синяя колода карт \"Bicycle\""),
            createCardItem(1312, "Чёрный джокер", "Синяя колода карт \"Bicycle\""),
            createCardItem(1313, "Красный джокер", "Синяя колода карт \"Bicycle\"")
            //</editor-fold>
    );

    List<ItemStack> RED_CARD_ITEMS = ImmutableList.of(
            //<editor-fold desc="Red cards" defaultstate="collapsed">
            createCardItem(1314, "Туз треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1315, "2 треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1316, "3 треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1318, "4 треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1319, "5 треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1320, "6 треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1321, "7 треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1322, "8 треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1323, "9 треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1324, "10 треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1325, "Валет треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1326, "Дама треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1327, "Король треф", "Красная колода карт \"Bicycle\""),
            createCardItem(1328, "Туз червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1329, "2 червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1330, "3 червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1331, "4 червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1332, "5 червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1333, "6 червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1334, "7 червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1335, "8 червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1336, "9 червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1337, "10 червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1338, "Валет червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1339, "Дама червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1340, "Король червей", "Красная колода карт \"Bicycle\""),
            createCardItem(1341, "Туз пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1342, "2 пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1343, "3 пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1344, "4 пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1345, "5 пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1346, "6 пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1347, "7 пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1348, "8 пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1349, "9 пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1350, "10 пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1351, "Валет пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1352, "Дама пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1353, "Король пики", "Красная колода карт \"Bicycle\""),
            createCardItem(1354, "Туз бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1355, "2 бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1356, "3 бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1357, "4 бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1358, "5 бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1359, "6 бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1360, "7 бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1361, "8 бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1362, "9 бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1363, "10 бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1364, "Валет бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1365, "Дама бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1366, "Король бубен", "Красная колода карт \"Bicycle\""),
            createCardItem(1367, "Чёрный джокер", "Красная колода карт \"Bicycle\""),
            createCardItem(1368, "Красный джокер", "Красная колода карт \"Bicycle\"")
            //</editor-fold>
    );

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

    final class Blue1 extends CustomItemImpl implements CardsBicycle {
        private static final String KEY;
        private static final ItemStack ITEM_STACK;

        static {
            KEY = "box_blue_1_card_box_bicycle";
            ITEM_STACK = new ItemStack(Material.BUNDLE);
            final BundleMeta bundleMeta = (BundleMeta) ITEM_STACK.getItemMeta();

            bundleMeta.displayName(
                    createDefaultStyledText("Синяя колода карт ")
                    .append(text("\"Bicycle\"")
                    .style(ChatUtils.DEFAULT_STYLE))
                    .decorate(TextDecoration.BOLD)
            );
            bundleMeta.setCustomModelData(1);
            bundleMeta.setItems(BLUE_CARD_ITEMS);
            ITEM_STACK.setItemMeta(bundleMeta);
        }

        public Blue1() {
            super(KEY, ITEM_STACK);
        }

        @Contract(" -> new")
        @Override
        public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
            return Collections.singletonList(
                    Map.entry(
                            new ShapedRecipe(this.namespacedKey, this.itemStack)
                            .shape(
                                    "DSD",
                                    "SPS",
                                    "DSD"
                            ).setIngredient('D', Material.BLUE_DYE)
                            .setIngredient('S', Material.STRING)
                            .setIngredient('P', Material.PAPER),
                            Boolean.TRUE
                    )
            );
        }
    }

    final class Blue2 extends CustomItemImpl implements CardsBicycle {
        private static final String KEY;
        private static final ItemStack ITEM_STACK;

        static {
            KEY = "box_blue_2_card_box_bicycle";
            ITEM_STACK = new ItemStack(Material.BUNDLE);
            final BundleMeta bundleMeta = (BundleMeta) ITEM_STACK.getItemMeta();

            bundleMeta.displayName(
                    createDefaultStyledText("Синяя колода карт ")
                    .append(text("\"Bicycle\"")
                    .style(ChatUtils.DEFAULT_STYLE))
                    .decorate(TextDecoration.BOLD)
            );
            bundleMeta.setCustomModelData(2);
            bundleMeta.setItems(BLUE_CARD_ITEMS);
            ITEM_STACK.setItemMeta(bundleMeta);
        }

        public Blue2() {
            super(KEY, ITEM_STACK);
        }

        @Contract(" -> new")
        @Override
        public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
            return Collections.singletonList(
                    Map.entry(
                            new ShapedRecipe(this.namespacedKey, this.itemStack)
                            .shape(
                                    "DSD",
                                    "SPS",
                                    "ISI"
                            ).setIngredient('D', Material.BLUE_DYE)
                            .setIngredient('S', Material.STRING)
                            .setIngredient('P', Material.PAPER)
                            .setIngredient('I', Material.IRON_INGOT),
                            Boolean.TRUE
                    )
            );
        }
    }

    final class Red1 extends CustomItemImpl implements CardsBicycle {
        private static final String KEY;
        private static final ItemStack ITEM_STACK;

        static {
            KEY = "box_red_1_card_box_bicycle";
            ITEM_STACK = new ItemStack(Material.BUNDLE);
            final BundleMeta bundleMeta = (BundleMeta) ITEM_STACK.getItemMeta();

            bundleMeta.displayName(
                    createDefaultStyledText("Красная колода карт ")
                    .append(text("\"Bicycle\"")
                    .style(ChatUtils.DEFAULT_STYLE))
                    .decorate(TextDecoration.BOLD)
            );
            bundleMeta.setCustomModelData(3);
            bundleMeta.setItems(RED_CARD_ITEMS);
            ITEM_STACK.setItemMeta(bundleMeta);
        }

        public Red1() {
            super(KEY, ITEM_STACK);
        }

        @Contract(" -> new")
        @Override
        public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
            return Collections.singletonList(
                    Map.entry(
                            new ShapedRecipe(this.namespacedKey, this.itemStack)
                            .shape(
                                    "DSD",
                                    "SPS",
                                    "DSD"
                            ).setIngredient('D', Material.RED_DYE)
                            .setIngredient('S', Material.STRING)
                            .setIngredient('P', Material.PAPER),
                            Boolean.TRUE
                    )
            );
        }
    }

    final class Red2 extends CustomItemImpl implements CardsBicycle {
        private static final String KEY;
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
            bundleMeta.setItems(RED_CARD_ITEMS);
            ITEM_STACK.setItemMeta(bundleMeta);
        }

        public Red2() {
            super(KEY, ITEM_STACK);
        }

        @Contract(" -> new")
        @Override
        public @NotNull @Unmodifiable List<Map.Entry<Recipe, Boolean>> initRecipes() {
            return Collections.singletonList(
                    Map.entry(
                            new ShapedRecipe(this.namespacedKey, this.itemStack)
                            .shape(
                                    "DSD",
                                    "SPS",
                                    "ISI"
                            ).setIngredient('D', Material.RED_DYE)
                            .setIngredient('S', Material.STRING)
                            .setIngredient('P', Material.PAPER)
                            .setIngredient('I', Material.IRON_INGOT),
                            Boolean.TRUE
                    )
            );
        }
    }
}
