package com.github.minersstudios.msitem.items;

import com.github.minersstudios.mscore.utils.MSItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Typed extends CustomItem {
	Typed.Type @NotNull [] getTypes();

	@Contract("null -> null")
	default @Nullable Typed.Type getType(@Nullable ItemStack itemStack) {
		if (itemStack == null) return null;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (
				itemMeta == null
				|| !itemMeta.hasCustomModelData()
		) return null;
		for (Typed.Type type : this.getTypes()) {
			if (
					itemMeta.getCustomModelData() == type.getCustomModelData()
					&& itemStack.getType() == this.getItemStack().getType()
			) {
				return type;
			}
		}
		return null;
	}

	default @NotNull ItemStack createItemStack(@NotNull Typed.Type type) {
		ItemStack itemStack = this.getItemStack().clone();
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setCustomModelData(type.getCustomModelData());
		itemMeta.displayName(type.getItemName());
		itemMeta.lore(type.getLore());
		itemMeta.getPersistentDataContainer().set(
				MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY,
				PersistentDataType.STRING,
				type.getNamespacedKey().getKey()
		);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	default @NotNull CustomItem createCustomItem(@NotNull Typed.Type type) {
		CustomItem customItem = (CustomItem) this.clone();
		customItem.setNamespacedKey(type.getNamespacedKey());
		customItem.setItemStack(this.createItemStack(type));
		return customItem;
	}

	interface Type {
		@NotNull
		NamespacedKey getNamespacedKey();

		@NotNull
		Component getItemName();

		int getCustomModelData();

		default @Nullable List<Component> getLore() {
			return null;
		}
	}
}
