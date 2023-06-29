package com.github.minersstudios.msitem.items.register.items.cards;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.MSItemUtils;
import com.github.minersstudios.msitem.MSItem;
import com.github.minersstudios.msitem.items.CustomItem;
import com.github.minersstudios.msitem.items.Typed;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CardsBicycle implements Typed {
	private @NotNull NamespacedKey namespacedKey;
	private @NotNull ItemStack itemStack;
	private @Nullable List<Map.Entry<Recipe, Boolean>> recipes;

	public static final List<ItemStack> BLUE_CARD_ITEMS = Lists.newArrayList(
			//<editor-fold desc="Blue cards">
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

	public static final List<ItemStack> RED_CARD_ITEMS = Lists.newArrayList(
			//<editor-fold desc="Red cards">
			createCardItem(1260, "Туз треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1261, "2 треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1262, "3 треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1263, "4 треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1264, "5 треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1265, "6 треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1266, "7 треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1267, "8 треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1268, "9 треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1269, "10 треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1270, "Валет треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1271, "Дама треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1272, "Король треф", "Красная колода карт \"Bicycle\""),
			createCardItem(1273, "Туз червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1274, "2 червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1275, "3 червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1276, "4 червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1277, "5 червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1278, "6 червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1279, "7 червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1280, "8 червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1281, "9 червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1282, "10 червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1283, "Валет червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1284, "Дама червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1285, "Король червей", "Красная колода карт \"Bicycle\""),
			createCardItem(1286, "Туз пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1287, "2 пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1288, "3 пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1289, "4 пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1290, "5 пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1291, "6 пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1292, "7 пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1293, "8 пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1294, "9 пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1295, "10 пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1296, "Валет пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1297, "Дама пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1298, "Король пики", "Красная колода карт \"Bicycle\""),
			createCardItem(1299, "Туз бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1300, "2 бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1301, "3 бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1302, "4 бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1303, "5 бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1304, "6 бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1305, "7 бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1306, "8 бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1307, "9 бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1308, "10 бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1309, "Валет бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1310, "Дама бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1311, "Король бубен", "Красная колода карт \"Bicycle\""),
			createCardItem(1312, "Чёрный джокер", "Красная колода карт \"Bicycle\""),
			createCardItem(1313, "Красный джокер", "Красная колода карт \"Bicycle\"")
			//</editor-fold>
	);

	public CardsBicycle() {
		this.namespacedKey = new NamespacedKey(MSItem.getInstance(), "card_box_bicycle");
		this.itemStack = Boxes.BOX_BLUE_1.getItemStack();

	}

	@Override
	public @NotNull List<Map.Entry<Recipe, Boolean>> initRecipes() {
		//<editor-fold desc="Recipes">
		ShapedRecipe blue1 = new ShapedRecipe(new NamespacedKey(MSItem.getInstance(), "card_box_bicycle_blue_1"), Boxes.BOX_BLUE_1.getItemStack())
				.shape(
						"DSD",
						"SPS",
						"DSD"
				).setIngredient('D', Material.BLUE_DYE)
				.setIngredient('S', Material.STRING)
				.setIngredient('P', Material.PAPER);
		ShapedRecipe blue2 = new ShapedRecipe(new NamespacedKey(MSItem.getInstance(), "card_box_bicycle_blue_2"), Boxes.BOX_BLUE_2.getItemStack())
				.shape(
						"DSD",
						"SPS",
						"ISI"
				).setIngredient('D', Material.BLUE_DYE)
				.setIngredient('S', Material.STRING)
				.setIngredient('P', Material.PAPER)
				.setIngredient('I', Material.IRON_INGOT);
		ShapedRecipe red1 = new ShapedRecipe(new NamespacedKey(MSItem.getInstance(), "card_box_bicycle_red_1"), Boxes.BOX_RED_1.getItemStack())
				.shape(
						"DSD",
						"SPS",
						"DSD"
				).setIngredient('D', Material.RED_DYE)
				.setIngredient('S', Material.STRING)
				.setIngredient('P', Material.PAPER);
		ShapedRecipe red2 = new ShapedRecipe(new NamespacedKey(MSItem.getInstance(), "card_box_bicycle_red_2"), Boxes.BOX_RED_2.getItemStack())
				.shape(
						"DSD",
						"SPS",
						"ISI"
				).setIngredient('D', Material.RED_DYE)
				.setIngredient('S', Material.STRING)
				.setIngredient('P', Material.PAPER)
				.setIngredient('I', Material.IRON_INGOT);
		//</editor-fold>
		return this.recipes = List.of(
				Map.entry(blue1, true),
				Map.entry(blue2, true),
				Map.entry(red1, true),
				Map.entry(red2, true)
		);
	}

	@Override
	public @NotNull NamespacedKey getNamespacedKey() {
		return this.namespacedKey;
	}

	@Override
	public void setNamespacedKey(@NotNull NamespacedKey namespacedKey) {
		this.namespacedKey = namespacedKey;
	}

	@Override
	public @NotNull ItemStack getItemStack() {
		return this.itemStack;
	}

	@Override
	public void setItemStack(@NotNull ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public @Nullable List<Map.Entry<Recipe, Boolean>> getRecipes() {
		return this.recipes;
	}

	@Override
	public void setRecipes(@Nullable List<Map.Entry<Recipe, Boolean>> recipes) {
		this.recipes = recipes;
	}

	@Override
	public @NotNull CustomItem clone() {
		try {
			return (CustomItem) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	private static @NotNull ItemStack createCardItem(
			int customModelData,
			@NotNull String name,
			@NotNull String lore
	) {
		ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.displayName(ChatUtils.createDefaultStyledText(name));
		itemMeta.setCustomModelData(customModelData);
		itemMeta.lore(ChatUtils.convertStringsToComponents(ChatUtils.COLORLESS_DEFAULT_STYLE.color(NamedTextColor.GRAY), lore));
		itemMeta.getPersistentDataContainer().set(
				MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY,
				PersistentDataType.STRING,
				"card_bicycle"
		);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	@Override
	public Type @NotNull [] getTypes() {
		return Boxes.values();
	}

	public enum Boxes implements Type {
		//<editor-fold desc="Boxes">
		BOX_BLUE_1(
				1,
				ChatUtils.createDefaultStyledText("Синяя колода карт ")
						.append(Component.text("\"Bicycle\"")
						.style(ChatUtils.DEFAULT_STYLE))
						.decorate(TextDecoration.BOLD),
				BLUE_CARD_ITEMS
		),
		BOX_BLUE_2(
				2,
				ChatUtils.createDefaultStyledText("Синяя колода карт ")
						.append(Component.text("\"Bicycle\"")
						.style(ChatUtils.DEFAULT_STYLE))
						.decorate(TextDecoration.BOLD),
				BLUE_CARD_ITEMS
		),
		BOX_RED_1(
				3,
				ChatUtils.createDefaultStyledText("Красная колода карт ")
						.append(Component.text("\"Bicycle\"")
						.style(ChatUtils.DEFAULT_STYLE))
						.decorate(TextDecoration.BOLD),
				RED_CARD_ITEMS
		),
		BOX_RED_2(
				4,
				ChatUtils.createDefaultStyledText("Красная колода карт ")
						.append(Component.text("\"Bicycle\"")
						.style(ChatUtils.DEFAULT_STYLE))
						.decorate(TextDecoration.BOLD),
				RED_CARD_ITEMS
		);
		//</editor-fold>

		private final @NotNull NamespacedKey namespacedKey;
		private final int customModelData;
		private final @NotNull Component itemName;
		private final @NotNull ItemStack itemStack;

		Boxes(
				int customModelData,
				@NotNull Component itemName,
				@NotNull List<ItemStack> cardItems
		) {
			this.namespacedKey = new NamespacedKey(MSItem.getInstance(), this.name().toLowerCase(Locale.ROOT) + "_card_box_bicycle");
			this.customModelData = customModelData;
			this.itemName = itemName;
			this.itemStack = new ItemStack(Material.BUNDLE);
			BundleMeta bundleMeta = (BundleMeta) this.itemStack.getItemMeta();
			bundleMeta.displayName(itemName);
			bundleMeta.setCustomModelData(customModelData);
			bundleMeta.setItems(cardItems);
			bundleMeta.getPersistentDataContainer().set(
					MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY,
					PersistentDataType.STRING,
					this.getNamespacedKey().getKey()
			);
			this.itemStack.setItemMeta(bundleMeta);
		}

		public @NotNull ItemStack getItemStack() {
			return this.itemStack;
		}

		@Override
		public @NotNull NamespacedKey getNamespacedKey() {
			return this.namespacedKey;
		}

		@Override
		public @NotNull Component getItemName() {
			return this.itemName;
		}

		@Override
		public int getCustomModelData() {
			return this.customModelData;
		}
	}
}
