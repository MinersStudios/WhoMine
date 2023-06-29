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

public class SmallChair implements Sittable, Typed {
	private @NotNull NamespacedKey namespacedKey;
	private @NotNull ItemStack itemStack;
	private @NotNull SoundGroup soundGroup;
	private @NotNull HitBox hitBox;
	private @Nullable Facing facing;
	private @Nullable List<Recipe> recipes;
	private double height;

	public SmallChair() {
		this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), "small_chair");
		this.itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
		this.soundGroup = new SoundGroup(
				"custom.block.wood.place", 0.5f, 1.0f,
				"custom.block.wood.break", 0.5f, 1.0f
		);
		this.hitBox = HitBox.SOLID_FRAME;
		this.height = 0.75d;
	}

	@Override
	public @Nullable List<Recipe> initRecipes() {
		//<editor-fold desc="Recipes">
		ShapedRecipe acacia = new ShapedRecipe(Type.ACACIA.namespacedKey, this.createItemStack(Type.ACACIA))
				.shape("PLP", "PAP")
				.setIngredient('P', Material.ACACIA_PLANKS)
				.setIngredient('L', Material.LEATHER)
				.setIngredient('A', Material.AIR);
		acacia.setGroup(this.namespacedKey.getNamespace() + ":small_chair");
		ShapedRecipe birch = new ShapedRecipe(Type.BIRCH.namespacedKey, this.createItemStack(Type.BIRCH))
				.shape("PLP", "PAP")
				.setIngredient('P', Material.BIRCH_PLANKS)
				.setIngredient('L', Material.LEATHER)
				.setIngredient('A', Material.AIR);
		birch.setGroup(this.namespacedKey.getNamespace() + ":small_chair");
		ShapedRecipe crimson = new ShapedRecipe(Type.CRIMSON.namespacedKey, this.createItemStack(Type.CRIMSON))
				.shape("PLP", "PAP")
				.setIngredient('P', Material.CRIMSON_PLANKS)
				.setIngredient('L', Material.LEATHER)
				.setIngredient('A', Material.AIR);
		crimson.setGroup(this.namespacedKey.getNamespace() + ":small_chair");
		ShapedRecipe darkOak = new ShapedRecipe(Type.DARK_OAK.namespacedKey, this.createItemStack(Type.DARK_OAK))
				.shape("PLP", "PAP")
				.setIngredient('P', Material.DARK_OAK_PLANKS)
				.setIngredient('L', Material.LEATHER)
				.setIngredient('A', Material.AIR);
		darkOak.setGroup(this.namespacedKey.getNamespace() + ":small_chair");
		ShapedRecipe jungle = new ShapedRecipe(Type.JUNGLE.namespacedKey, this.createItemStack(Type.JUNGLE))
				.shape("PLP", "PAP")
				.setIngredient('P', Material.JUNGLE_PLANKS)
				.setIngredient('L', Material.LEATHER)
				.setIngredient('A', Material.AIR);
		jungle.setGroup(this.namespacedKey.getNamespace() + ":small_chair");
		ShapedRecipe oak = new ShapedRecipe(Type.OAK.namespacedKey, this.createItemStack(Type.OAK))
				.shape("PLP", "PAP")
				.setIngredient('P', Material.OAK_PLANKS)
				.setIngredient('L', Material.LEATHER)
				.setIngredient('A', Material.AIR);
		oak.setGroup(this.namespacedKey.getNamespace() + ":small_chair");
		ShapedRecipe spruce = new ShapedRecipe(Type.SPRUCE.namespacedKey, this.createItemStack(Type.SPRUCE))
				.shape("PLP", "PAP")
				.setIngredient('P', Material.SPRUCE_PLANKS)
				.setIngredient('L', Material.LEATHER)
				.setIngredient('A', Material.AIR);
		spruce.setGroup(this.namespacedKey.getNamespace() + ":small_chair");
		ShapedRecipe warped = new ShapedRecipe(Type.WARPED.namespacedKey, this.createItemStack(Type.WARPED))
				.shape("PLP", "PAP")
				.setIngredient('P', Material.WARPED_PLANKS)
				.setIngredient('L', Material.LEATHER)
				.setIngredient('A', Material.AIR);
		warped.setGroup(this.namespacedKey.getNamespace() + ":small_chair");
		ShapedRecipe mangrove = new ShapedRecipe(Type.MANGROVE.namespacedKey, this.createItemStack(Type.MANGROVE))
				.shape("PLP", "PAP")
				.setIngredient('P', Material.MANGROVE_PLANKS)
				.setIngredient('L', Material.LEATHER)
				.setIngredient('A', Material.AIR);
		mangrove.setGroup(this.namespacedKey.getNamespace() + ":small_chair");
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
		ACACIA("Акациевый стул", 1000),
		BIRCH("Берёзовый стул", 1001),
		CRIMSON( "Багровый стул", 1002),
		DARK_OAK("Стул из тёмного дуба", 1003),
		JUNGLE("Тропический стул", 1004),
		OAK("Дубовый стул", 1005),
		SPRUCE("Еловый стул", 1006),
		WARPED("Искажённый стул", 1007),
		MANGROVE("Мангровый стул", 1193);
		//</editor-fold>

		private final @NotNull NamespacedKey namespacedKey;
		private final @NotNull String itemName;
		private final int customModelData;
		private final @Nullable List<Component> lore;
		private final @NotNull HitBox hitBox;
		private final @Nullable Facing facing;

		private static final Type @NotNull [] types = values();

		Type(
				@NotNull String itemName,
				int customModelData
		) {
			this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), this.name().toLowerCase(Locale.ROOT) + "_small_chair");
			this.itemName = itemName;
			this.customModelData = customModelData;
			this.lore = Badges.PAINTABLE_LORE_LIST;
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
