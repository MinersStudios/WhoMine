package com.github.minersstudios.msdecor.customdecor.register.furniture.tables;

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

public class SmallTable implements Typed {
	private @NotNull NamespacedKey namespacedKey;
	private @NotNull ItemStack itemStack;
	private @NotNull SoundGroup soundGroup;
	private @NotNull HitBox hitBox;
	private @Nullable Facing facing;
	private @Nullable List<Recipe> recipes;

	public SmallTable() {
		this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), "small_table");
		this.itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
		this.soundGroup = new SoundGroup(
				"custom.block.wood.place", 0.5f, 1.0f,
				"custom.block.wood.break", 0.5f, 1.0f
		);
		this.hitBox = HitBox.SOLID_FRAME;
	}

	@Override
	public @Nullable List<Recipe> initRecipes() {
		//<editor-fold desc="Recipes">
		ShapedRecipe acacia = new ShapedRecipe(Type.ACACIA.namespacedKey, this.createItemStack(Type.ACACIA))
				.shape("PPP", "PAP")
				.setIngredient('P', Material.ACACIA_PLANKS)
				.setIngredient('A', Material.AIR);
		acacia.setGroup(this.namespacedKey.getNamespace() + ":small_table");
		ShapedRecipe birch = new ShapedRecipe(Type.BIRCH.namespacedKey, this.createItemStack(Type.BIRCH))
				.shape("PPP", "PAP")
				.setIngredient('P', Material.BIRCH_PLANKS)
				.setIngredient('A', Material.AIR);
		birch.setGroup(this.namespacedKey.getNamespace() + ":small_table");
		ShapedRecipe crimson = new ShapedRecipe(Type.CRIMSON.namespacedKey, this.createItemStack(Type.CRIMSON))
				.shape("PPP", "PAP")
				.setIngredient('P', Material.CRIMSON_PLANKS)
				.setIngredient('A', Material.AIR);
		crimson.setGroup(this.namespacedKey.getNamespace() + ":small_table");
		ShapedRecipe darkOak = new ShapedRecipe(Type.DARK_OAK.namespacedKey, this.createItemStack(Type.DARK_OAK))
				.shape("PPP", "PAP")
				.setIngredient('P', Material.DARK_OAK_PLANKS)
				.setIngredient('A', Material.AIR);
		darkOak.setGroup(this.namespacedKey.getNamespace() + ":small_table");
		ShapedRecipe jungle = new ShapedRecipe(Type.JUNGLE.namespacedKey, this.createItemStack(Type.JUNGLE))
				.shape("PPP", "PAP")
				.setIngredient('P', Material.JUNGLE_PLANKS)
				.setIngredient('A', Material.AIR);
		jungle.setGroup(this.namespacedKey.getNamespace() + ":small_table");
		ShapedRecipe oak = new ShapedRecipe(Type.OAK.namespacedKey, this.createItemStack(Type.OAK))
				.shape("PPP", "PAP")
				.setIngredient('P', Material.OAK_PLANKS)
				.setIngredient('A', Material.AIR);
		oak.setGroup(this.namespacedKey.getNamespace() + ":small_table");
		ShapedRecipe spruce = new ShapedRecipe(Type.SPRUCE.namespacedKey, this.createItemStack(Type.SPRUCE))
				.shape("PPP", "PAP")
				.setIngredient('P', Material.SPRUCE_PLANKS)
				.setIngredient('A', Material.AIR);
		spruce.setGroup(this.namespacedKey.getNamespace() + ":small_table");
		ShapedRecipe warped = new ShapedRecipe(Type.WARPED.namespacedKey, this.createItemStack(Type.WARPED))
				.shape("PPP", "PAP")
				.setIngredient('P', Material.WARPED_PLANKS)
				.setIngredient('A', Material.AIR);
		warped.setGroup(this.namespacedKey.getNamespace() + ":small_table");
		ShapedRecipe mangrove = new ShapedRecipe(Type.MANGROVE.namespacedKey, this.createItemStack(Type.MANGROVE))
				.shape("PPP", "PAP")
				.setIngredient('P', Material.MANGROVE_PLANKS)
				.setIngredient('A', Material.AIR);
		mangrove.setGroup(this.namespacedKey.getNamespace() + ":small_table");
		//</editor-fold>
		this.recipes = Lists.newArrayList(acacia, birch, crimson, darkOak, jungle, oak, spruce, warped, mangrove);
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
		ACACIA("Маленький акациевый стол", 1070),
		BIRCH("Маленький берёзовый стол", 1072),
		CRIMSON( "Маленький багровый стол", 1074),
		DARK_OAK("Маленький стол из тёмного дуба", 1076),
		JUNGLE("Маленький тропический стол", 1078),
		OAK("Маленький дубовый стол", 1080),
		SPRUCE("Маленький еловый стол", 1082),
		WARPED("Маленький искажённый стол", 1084),
		MANGROVE("Маленький мангровый стол", 1201);
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
			this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), this.name().toLowerCase(Locale.ROOT) + "_small_table");
			this.itemName = itemName;
			this.customModelData = customModelData;
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
		public @NotNull HitBox getHitBox() {
			return this.hitBox;
		}

		@Override
		public @Nullable Facing getFacing() {
			return this.facing;
		}
	}
}
