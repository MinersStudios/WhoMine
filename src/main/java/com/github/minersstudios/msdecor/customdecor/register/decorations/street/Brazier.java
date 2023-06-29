package com.github.minersstudios.msdecor.customdecor.register.decorations.street;

import com.github.minersstudios.msdecor.MSDecor;
import com.github.minersstudios.msdecor.customdecor.*;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class Brazier implements Lightable, Typed {
	private @NotNull NamespacedKey namespacedKey;
	private @NotNull ItemStack itemStack;
	private @NotNull SoundGroup soundGroup;
	private @NotNull HitBox hitBox;
	private @Nullable Facing facing;
	private @Nullable List<Recipe> recipes;
	private int firstLightLevel;
	private int secondLightLevel;

	public Brazier() {
		this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), "brazier");
		this.itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
		this.itemStack.setItemMeta(this.createItemStack(Type.DEFAULT).getItemMeta());
		this.soundGroup = new SoundGroup(
				"block.chain.place", 1.0f, 1.0f,
				"block.chain.break", 1.0f, 1.0f
		);
		this.hitBox = HitBox.SMALL_ARMOR_STAND;
	}

	@Override
	public @Nullable List<Recipe> initRecipes() {
		ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.createItemStack(Type.DEFAULT))
				.shape(
						"B B",
						"BBB",
						" I "
				)
				.setIngredient('B', Material.IRON_BARS)
				.setIngredient('I', Material.IRON_INGOT);
		this.recipes = Lists.newArrayList(shapedRecipe);
		return this.recipes;
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
	public @NotNull SoundGroup getSoundGroup() {
		return this.soundGroup;
	}

	@Override
	public void setSoundGroup(@NotNull SoundGroup soundGroup) {
		this.soundGroup = soundGroup;
	}

	@Override
	public @NotNull HitBox getHitBox() {
		return this.hitBox;
	}

	@Override
	public void setHitBox(@NotNull HitBox hitBox) {
		this.hitBox = hitBox;
	}

	@Override
	public @Nullable Facing getFacing() {
		return this.facing;
	}

	@Override
	public void setFacing(@Nullable Facing facing) {
		this.facing = facing;
	}

	@Override
	public @Nullable List<Recipe> getRecipes() {
		return this.recipes;
	}

	@Override
	public void setRecipes(@Nullable List<Recipe> recipes) {
		this.recipes = recipes;
	}

	@Override
	public @NotNull CustomDecorData clone() {
		try {
			return (CustomDecorData) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getFirstLightLevel() {
		return this.firstLightLevel;
	}

	@Override
	public void setFirstLightLevel(int level) {
		this.firstLightLevel = level;
	}

	@Override
	public int getSecondLightLevel() {
		return this.secondLightLevel;
	}

	@Override
	public void setSecondLightLevel(int level) {
		this.secondLightLevel = level;
	}

	@Override
	public Type @NotNull [] getTypes() {
		return Type.types;
	}

	public enum Type implements Typed.LightableType {
		//<editor-fold desc="Types">
		DEFAULT( 1183, 0, 15),
		FIRED(1184, 15, 0);
		//</editor-fold>

		private final @NotNull NamespacedKey namespacedKey;
		private final @NotNull String itemName;
		private final int customModelData;
		private final @NotNull HitBox hitBox;
		private final @Nullable Facing facing;
		private final int firstLightLevel;
		private final int secondLightLevel;

		private static final Type @NotNull [] types = values();

		Type(
				int customModelData,
				int firstLightLevel,
				int secondLightLevel
		) {
			this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), this.name().toLowerCase(Locale.ROOT) + "_brazier");
			this.itemName = "Мангал";
			this.customModelData = customModelData;
			this.hitBox = HitBox.SMALL_ARMOR_STAND;
			this.facing = Facing.FLOOR;
			this.firstLightLevel = firstLightLevel;
			this.secondLightLevel = secondLightLevel;
		}

		@Override
		public @NotNull NamespacedKey getNamespacedKey() {
			return this.namespacedKey;
		}

		@Override
		public @NotNull String getItemName() {
			return this.itemName;
		}

		@Override
		public int getCustomModelData() {
			return this.customModelData;
		}

		@Override
		public @NotNull HitBox getHitBox() {
			return this.hitBox;
		}

		@Override
		public @Nullable Facing getFacing() {
			return this.facing;
		}

		@Override
		public int getFirstLightLevel() {
			return this.firstLightLevel;
		}

		@Override
		public int getSecondLightLevel() {
			return this.secondLightLevel;
		}
	}
}
