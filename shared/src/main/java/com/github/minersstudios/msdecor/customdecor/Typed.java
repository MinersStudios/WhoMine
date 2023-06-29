package com.github.minersstudios.msdecor.customdecor;

import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Typed extends CustomDecorData {
	Type @NotNull [] getTypes();

	@Contract("null -> null")
	default @Nullable Type getType(@Nullable ItemStack itemStack) {
		if (itemStack == null) return null;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (
				itemMeta == null
				|| !itemMeta.hasCustomModelData()
		) return null;
		for (Type type : this.getTypes()) {
			if (
					itemMeta.getCustomModelData() == type.getCustomModelData()
					&& itemStack.getType() == this.getItemStack().getType()
			) {
				return type;
			}
		}
		return null;
	}

	default @NotNull ItemStack createItemStack(@NotNull Type type) {
		ItemStack itemStack = this.getItemStack().clone();
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setCustomModelData(type.getCustomModelData());
		itemMeta.getPersistentDataContainer().set(
				MSDecorUtils.CUSTOM_DECOR_TYPE_NAMESPACED_KEY,
				PersistentDataType.STRING,
				type.getNamespacedKey().getKey()
		);
		itemMeta.displayName(ChatUtils.createDefaultStyledText(type.getItemName()));
		itemMeta.lore(type.getLore());
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	default @NotNull CustomDecorData createCustomDecorData(@NotNull Type type) {
		CustomDecorData customDecorData = (CustomDecorData) this.clone();
		customDecorData.setNamespacedKey(type.getNamespacedKey());
		customDecorData.setItemStack(this.createItemStack(type));
		customDecorData.setHitBox(type.getHitBox());
		customDecorData.setFacing(type.getFacing());
		if (
				type instanceof LightableType lightableType
				&& customDecorData instanceof Lightable lightable
		) {
			lightable.setFirstLightLevel(lightableType.getFirstLightLevel());
			lightable.setSecondLightLevel(lightableType.getSecondLightLevel());
			return lightable;
		}
		return customDecorData;
	}

	interface Type {
		@NotNull NamespacedKey getNamespacedKey();

		@NotNull String getItemName();

		int getCustomModelData();

		default @Nullable List<Component> getLore() {
			return null;
		}

		@NotNull HitBox getHitBox();

		default @Nullable Facing getFacing() {
			return null;
		}
	}

	interface LightableType extends Type {
		int getFirstLightLevel();

		int getSecondLightLevel();
	}
}
