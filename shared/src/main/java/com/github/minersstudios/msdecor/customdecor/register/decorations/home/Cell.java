package com.github.minersstudios.msdecor.customdecor.register.decorations.home;

import com.github.minersstudios.msdecor.MSDecor;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.customdecor.SoundGroup;
import com.github.minersstudios.msdecor.customdecor.Typed;
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

public class Cell implements Typed {
	private @NotNull NamespacedKey namespacedKey;
	private @NotNull ItemStack itemStack;
	private @NotNull SoundGroup soundGroup;
	private @NotNull HitBox hitBox;
	private @Nullable Facing facing;
	private @Nullable List<Recipe> recipes;

	public Cell() {
		this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), "cell");
		this.itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
		this.itemStack.setItemMeta(this.createItemStack(Type.DEFAULT).getItemMeta());
		this.soundGroup = new SoundGroup(
				"block.chain.place", 1.0f, 1.0f,
				"block.chain.break", 1.0f, 1.0f
		);
		this.hitBox = HitBox.SOLID_FRAME;
	}

	@Override
	public @Nullable List<Recipe> initRecipes() {
		ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.createItemStack(Type.DEFAULT))
				.shape(
						" C ",
						"BBB",
						"BBB"
				).setIngredient('B', Material.IRON_BARS)
				.setIngredient('C', Material.CHAIN);
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
	public Type @NotNull [] getTypes() {
		return Type.types;
	}

	public enum Type implements Typed.Type {
		//<editor-fold desc="Types">
		DEFAULT(1174),
		BEE(1175),
		CHICKEN(1176),
		PARROT_BLUE(1177),
		PARROT_GREEN(1178),
		PARROT_GRAY(1179),
		PARROT_RED_BLUE(1180),
		PARROT_YELLOW_BLUE(1181),
		SLIME(1182);
		//</editor-fold>

		private final @NotNull NamespacedKey namespacedKey;
		private final @NotNull String itemName;
		private final int customModelData;
		private final @NotNull HitBox hitBox;

		private static final Type @NotNull [] types = values();

		Type(int customModelData) {
			this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), this.name().toLowerCase(Locale.ROOT) + "_cell");
			this.itemName = "Клетка";
			this.customModelData = customModelData;
			this.hitBox = HitBox.SOLID_FRAME;
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
	}
}
