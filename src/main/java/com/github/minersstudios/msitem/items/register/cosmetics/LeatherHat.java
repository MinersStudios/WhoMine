package com.github.minersstudios.msitem.items.register.cosmetics;

import com.github.minersstudios.mscore.utils.Badges;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.MSItemUtils;
import com.github.minersstudios.msitem.MSItem;
import com.github.minersstudios.msitem.items.CustomItem;
import com.github.minersstudios.msitem.items.Renameable;
import com.github.minersstudios.msitem.items.Wearable;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LeatherHat implements Renameable, Wearable {
	private @NotNull NamespacedKey namespacedKey;
	private @NotNull ItemStack itemStack;
	private @Nullable List<Map.Entry<Recipe, Boolean>> recipes;

	public LeatherHat() {
		this.namespacedKey = new NamespacedKey(MSItem.getInstance(), "leather_hat");
		this.itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
		ItemMeta itemMeta = this.itemStack.getItemMeta();
		itemMeta.displayName(ChatUtils.createDefaultStyledText("Кожаная шляпа"));
		itemMeta.setCustomModelData(999);
		itemMeta.lore(Badges.PAINTABLE_LORE_LIST);
		itemMeta.addAttributeModifier(
				Attribute.GENERIC_ARMOR,
				new AttributeModifier(UUID.randomUUID(), "armor", 1.0f, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD)
		);
		itemMeta.getPersistentDataContainer().set(
				MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY,
				PersistentDataType.STRING,
				this.namespacedKey.getKey()
		);
		this.itemStack.setItemMeta(itemMeta);
	}

	@Override
	public @NotNull List<Map.Entry<Recipe, Boolean>> initRecipes() {
		ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.itemStack)
				.shape(
						" L ",
						"LLL"
				).setIngredient('L', Material.LEATHER);
		return this.recipes = List.of(Map.entry(shapedRecipe, true));
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

	@Override
	public Item @NotNull [] getRenameableItems() {
		return Item.renameableItems;
	}

	public enum Item implements Renameable.Item {
		//<editor-fold desc="Hats">
		LEATHER_HAT(999, "Кожаная шляпа", Badges.PAINTABLE_LORE_LIST),
		SANTA_HAT(1258, "Колпак санты", Badges.PAINTABLE_LORE_LIST),
		GAS_MASK(1369, "Противогаз"),
		THREED_GLASSES(1211, "3Д Очки"),
		NECTAR_BEE(1213, "Пчела с пыльцой"),
		BEE(1212, "Пчела"),
		ANGRY_NECTAR_BEE(1215, "Злая пчела с пыльцой"),
		ANGRY_BEE(1214, "Злая пчела"),
		PHANTOM(1216, "Фантом"),
		BERET(1217, "Берет", Badges.PAINTABLE_LORE_LIST),
		CAP(1218, "Кепка", Badges.PAINTABLE_LORE_LIST),
		CARDBOARD_CROWN(1219, "Корона из картона", Badges.PAINTABLE_LORE_LIST),
		CHEF_HAT(1220, "Колпак шефа", Badges.PAINTABLE_LORE_LIST),
		POVAR_HAT(1221, "Колпак повара", Badges.PAINTABLE_LORE_LIST),
		ENCHANTER_HAT(1222, "Чародейная шапка"),
		GOAT_HEAD(1223, "Козья голова"),
		MEDICINE_MASK(1224, "Медицинская маска"),
		NEADER_MASK(1225, "Маска неандертальца", Badges.PAINTABLE_LORE_LIST),
		NEADER_MASK_O(1226, "Орущая маска неандертальца", Badges.PAINTABLE_LORE_LIST),
		NEADER_MASK_LEAVES(1227, "Маска с листьями неандертальца", Badges.PAINTABLE_LORE_LIST),
		NEADER_MASK_LEAVES_O(1228, "Орущая маска с листьями неандертальца", Badges.PAINTABLE_LORE_LIST),
		GOLDEN_PROTHESIS_LEFT(1229, "Левый золотой глазной протез", Badges.PAINTABLE_LORE_LIST),
		GOLDEN_PROTHESIS_RIGHT(1230, "Правый золотой глазной протез", Badges.PAINTABLE_LORE_LIST),
		SILVER_PROTHESIS_LEFT(1231, "Левый серебряный глазной протез", Badges.PAINTABLE_LORE_LIST),
		SILVER_PROTHESIS_RIGHT(1232, "Правый серебряный глазной протез", Badges.PAINTABLE_LORE_LIST),
		RACOON_HAT(1233, "Шапка из енота"),
		SAMURAI(1234, "Кабуто"),
		SAMURAI_MASK(1235, "Маска с кабуто"),
		SCULK(1236, "Усики вардена"),
		SUN_HAT(1237, "Соломенная шляпа", Badges.PAINTABLE_LORE_LIST),
		WAITER_CAP(1238, "Официантская шапочка", Badges.PAINTABLE_LORE_LIST),
		WESTERN_HAT(1239, "Ковбойская шляпа"),
		WOLF_HEAD(1240, "Волчья голова", Badges.PAINTABLE_LORE_LIST),
		//<editor-fold desc="Villager hats">
		ARMORER_HAT(1241, "Очки бронника"),
		BUTCHER_HAT(1242, "Повязка мясника"),
		CARTOGRAPHER_HAT(1243, "Монокль картографа"),
		DESERT_HAT(1244, "Пустынная шляпа"),
		FARMER_HAT(1245, "Шляпа фермера"),
		FISHERMAN_HAT(1246, "Шляпа рыбака"),
		FLETCHER_HAT(1247, "Шляпа лучника"),
		LIBRARIAN_HAT(1248, "Шляпа библиотекаря"),
		SAVANNA_HAT(1249, "Лавровый венок"),
		SHEPHERD_HAT(1250, "Шляпа пастуха"),
		SNOW_HAT(1251, "Зимняя шляпа"),
		SWAMP_HAT(1252, "Болотный лист"),
		WITCH_HAT(1253, "Шляпа ведьмы")
		//</editor-fold>
		;
		//</editor-fold>
		private final int customModelData;
		@NotNull private final String renameText;
		@Nullable private final List<Component> lore;
		private final boolean showInRenameMenu;

		private static final Item[] renameableItems = values();

		Item(
				int customModelData,
				@NotNull String renameText
		) {
			this(customModelData, renameText, null, true);
		}

		Item(
				int customModelData,
				@NotNull String renameText,
				@Nullable List<Component> lore
		) {
			this(customModelData, renameText, lore, true);
		}

		Item(
				int customModelData,
				@NotNull String renameText,
				@Nullable List<Component> lore,
				boolean showInRenameMenu
		) {
			this.customModelData = customModelData;
			this.renameText = renameText;
			this.lore = lore;
			this.showInRenameMenu = showInRenameMenu;
		}

		@Override
		public @NotNull String getKey() {
			return this.name().toLowerCase(Locale.ROOT);
		}

		@Override
		public int getCustomModelData() {
			return this.customModelData;
		}

		@Override
		public @NotNull String getRenameText() {
			return this.renameText;
		}

		@Override
		public @Nullable List<Component> getLore() {
			return this.lore;
		}

		@Override
		public boolean isShowInRenameMenu() {
			return this.showInRenameMenu;
		}
	}
}
