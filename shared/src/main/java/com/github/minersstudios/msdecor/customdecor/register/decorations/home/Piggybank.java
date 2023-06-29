package com.github.minersstudios.msdecor.customdecor.register.decorations.home;

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

public class Piggybank implements Typed {
	private @NotNull NamespacedKey namespacedKey;
	private @NotNull ItemStack itemStack;
	private @NotNull SoundGroup soundGroup;
	private @NotNull HitBox hitBox;
	private @Nullable Facing facing;
	private @Nullable List<Recipe> recipes;

	public Piggybank() {
		this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), "piggybank");
		this.itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
		this.soundGroup = new SoundGroup(
				"block.glass.place", 1.5f, 1.0f,
				"block.glass.break", 1.5f, 1.0f
		);
		this.hitBox = HitBox.SMALL_ARMOR_STAND;
	}

	@Override
	public @Nullable List<Recipe> initRecipes() {
		//<editor-fold desc="Recipes">
		ShapedRecipe clay = new ShapedRecipe(Type.CLAY.namespacedKey, this.createItemStack(Type.CLAY))
				.shape(
						"  P",
						"PPP",
						"P P"
				).setIngredient('P', Material.CLAY);
		clay.setGroup(this.namespacedKey.getNamespace() + ":piggybank");
		ShapedRecipe diamond = new ShapedRecipe(Type.DIAMOND.namespacedKey, this.createItemStack(Type.DIAMOND))
				.shape(
						"  P",
						"PPP",
						"P P"
				).setIngredient('P', Material.DIAMOND_BLOCK);
		diamond.setGroup(this.namespacedKey.getNamespace() + ":piggybank");
		ShapedRecipe gold = new ShapedRecipe(Type.GOLD.namespacedKey, this.createItemStack(Type.GOLD))
				.shape(
						"  P",
						"PPP",
						"P P"
				).setIngredient('P', Material.GOLD_BLOCK);
		gold.setGroup(this.namespacedKey.getNamespace() + ":piggybank");
		ShapedRecipe emerald = new ShapedRecipe(Type.EMERALD.namespacedKey, this.createItemStack(Type.EMERALD))
				.shape(
						"  P",
						"PPP",
						"P P"
				).setIngredient('P', Material.EMERALD_BLOCK);
		emerald.setGroup(this.namespacedKey.getNamespace() + ":piggybank");
		ShapedRecipe iron = new ShapedRecipe(Type.IRON.namespacedKey, this.createItemStack(Type.IRON))
				.shape(
						"  P",
						"PPP",
						"P P"
				).setIngredient('P', Material.IRON_BLOCK);
		iron.setGroup(this.namespacedKey.getNamespace() + ":piggybank");
		ShapedRecipe netherite = new ShapedRecipe(Type.NETHERITE.namespacedKey, this.createItemStack(Type.NETHERITE))
				.shape(
						"  P",
						"PPP",
						"P P"
				).setIngredient('P', Material.NETHERITE_BLOCK);
		netherite.setGroup(this.namespacedKey.getNamespace() + ":piggybank");
		//</editor-fold>
		this.recipes = Lists.newArrayList(clay, diamond, gold, emerald, iron, netherite);
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
	public Type @NotNull [] getTypes() {
		return Type.types;
	}

	public enum Type implements Typed.Type {
		//<editor-fold desc="Types">
		CLAY("Копилка", 1155),
		DIAMOND("Алмазная копилка", 1156),
		EMERALD("Изумрудная копилка", 1157),
		GOLD("Золотая копилка", 1158),
		IRON("Железная копилка", 1159),
		NETHERITE("Незеритовая копилка", 1160);
		//</editor-fold>

		private final @NotNull NamespacedKey namespacedKey;
		private final @NotNull String itemName;
		private final int customModelData;
		private final @NotNull HitBox hitBox;
		private final @Nullable Facing facing;

		private static final Type @NotNull [] types = values();

		Type(
				@NotNull String itemName,
				int customModelData
		) {
			this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), this.name().toLowerCase(Locale.ROOT) + "_piggybank");
			this.itemName = itemName;
			this.customModelData = customModelData;
			this.hitBox = HitBox.SMALL_ARMOR_STAND;
			this.facing = Facing.FLOOR;
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
	}
}
