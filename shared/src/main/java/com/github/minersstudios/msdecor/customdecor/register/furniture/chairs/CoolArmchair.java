package com.github.minersstudios.msdecor.customdecor.register.furniture.chairs;

import com.github.minersstudios.mscore.utils.Badges;
import com.github.minersstudios.msdecor.MSDecor;
import com.github.minersstudios.msdecor.customdecor.*;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class CoolArmchair implements Sittable, Wrenchable {
	private @NotNull NamespacedKey namespacedKey;
	private @NotNull ItemStack itemStack;
	private @NotNull SoundGroup soundGroup;
	private @NotNull HitBox hitBox;
	private @Nullable Facing facing;
	private @Nullable List<Recipe> recipes;
	private double height;

	public CoolArmchair() {
		this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), "cool_armchair");
		this.itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
		this.itemStack.setItemMeta(this.createItemStack(Type.DEFAULT).getItemMeta());
		this.soundGroup = new SoundGroup(
				"block.wool.place", 0.75f, 1.0f,
				"block.wool.break", 0.75f, 1.0f
		);
		this.hitBox = HitBox.SOLID_FRAME;
		this.height = 0.65d;
	}

	@Override
	public @Nullable List<Recipe> initRecipes() {
		ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, this.createItemStack(Type.DEFAULT))
				.shape(
						"L  ",
						"LLL",
						"I I"
				)
				.setIngredient('I', Material.IRON_NUGGET)
				.setIngredient('L', Material.LEATHER);
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
	public double getHeight() {
		return this.height;
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public Type @NotNull [] getTypes() {
		return Type.types;
	}

	public enum Type implements Typed.Type {
		//<editor-fold desc="Types">
		DEFAULT(1016),
		LEFT(1017),
		MIDDLE(1018),
		RIGHT(1019);
		//</editor-fold>

		private final @NotNull NamespacedKey namespacedKey;
		private final @NotNull String itemName;
		private final int customModelData;
		private final @Nullable List<Component> lore;
		private final @NotNull HitBox hitBox;
		private final @Nullable Facing facing;

		private static final Type @NotNull [] types = values();

		Type(int customModelData) {
			this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), this.name().toLowerCase(Locale.ROOT) + "_cool_armchair");
			this.itemName = "Стильное кресло";
			this.customModelData = customModelData;
			this.lore = Lists.newArrayList(
					Badges.PAINTABLE_LORE_LIST.get(0),
					Badges.WRENCHABLE_LORE_LIST.get(0)
			);
			this.hitBox = HitBox.SOLID_FRAME;
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
		public @Nullable List<Component> getLore() {
			return this.lore;
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
