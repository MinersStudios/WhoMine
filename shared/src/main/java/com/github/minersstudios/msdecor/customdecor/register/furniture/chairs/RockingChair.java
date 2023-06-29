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

public class RockingChair implements Sittable, Typed {
	private @NotNull NamespacedKey namespacedKey;
	private @NotNull ItemStack itemStack;
	private @NotNull SoundGroup soundGroup;
	private @NotNull HitBox hitBox;
	private @Nullable Facing facing;
	private @Nullable List<Recipe> recipes;
	private double height;

	public RockingChair() {
		this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), "rocking_chair");
		this.itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
		this.soundGroup = new SoundGroup(
				"custom.block.wood.place", 0.5f, 1.0f,
				"custom.block.wood.break", 0.5f, 1.0f
		);
		this.hitBox = HitBox.SOLID_FRAME;
		this.height = 0.5d;
	}

	@Override
	public @Nullable List<Recipe> initRecipes() {
		//<editor-fold desc="Recipes">
		ShapedRecipe acacia = new ShapedRecipe(Type.ACACIA.namespacedKey, this.createItemStack(Type.ACACIA))
				.shape(
						"P  ",
						"PPP",
						"PPP"
				)
				.setIngredient('P', Material.ACACIA_PLANKS);
		acacia.setGroup(this.namespacedKey.getNamespace() + ":rocking_chair");
		ShapedRecipe birch = new ShapedRecipe(Type.BIRCH.namespacedKey, this.createItemStack(Type.BIRCH))
				.shape(
						"P  ",
						"PPP",
						"PPP"
				)
				.setIngredient('P', Material.BIRCH_PLANKS);
		birch.setGroup(this.namespacedKey.getNamespace() + ":rocking_chair");
		ShapedRecipe crimson = new ShapedRecipe(Type.CRIMSON.namespacedKey, this.createItemStack(Type.CRIMSON))
				.shape(
						"P  ",
						"PPP",
						"PPP"
				)
				.setIngredient('P', Material.CRIMSON_PLANKS);
		crimson.setGroup(this.namespacedKey.getNamespace() + ":rocking_chair");
		ShapedRecipe darkOak = new ShapedRecipe(Type.DARK_OAK.namespacedKey, this.createItemStack(Type.DARK_OAK))
				.shape(
						"P  ",
						"PPP",
						"PPP"
				)
				.setIngredient('P', Material.DARK_OAK_PLANKS);
		darkOak.setGroup(this.namespacedKey.getNamespace() + ":rocking_chair");
		ShapedRecipe jungle = new ShapedRecipe(Type.JUNGLE.namespacedKey, this.createItemStack(Type.JUNGLE))
				.shape(
						"P  ",
						"PPP",
						"PPP"
				)
				.setIngredient('P', Material.JUNGLE_PLANKS);
		jungle.setGroup(this.namespacedKey.getNamespace() + ":rocking_chair");
		ShapedRecipe oak = new ShapedRecipe(Type.OAK.namespacedKey, this.createItemStack(Type.OAK))
				.shape(
						"P  ",
						"PPP",
						"PPP"
				)
				.setIngredient('P', Material.OAK_PLANKS);
		oak.setGroup(this.namespacedKey.getNamespace() + ":rocking_chair");
		ShapedRecipe spruce = new ShapedRecipe(Type.SPRUCE.namespacedKey, this.createItemStack(Type.SPRUCE))
				.shape(
						"P  ",
						"PPP",
						"PPP"
				)
				.setIngredient('P', Material.SPRUCE_PLANKS);
		spruce.setGroup(this.namespacedKey.getNamespace() + ":rocking_chair");
		ShapedRecipe warped = new ShapedRecipe(Type.WARPED.namespacedKey, this.createItemStack(Type.WARPED))
				.shape(
						"P  ",
						"PPP",
						"PPP"
				)
				.setIngredient('P', Material.WARPED_PLANKS);
		warped.setGroup(this.namespacedKey.getNamespace() + ":rocking_chair");
		ShapedRecipe mangrove = new ShapedRecipe(Type.MANGROVE.namespacedKey, this.createItemStack(Type.MANGROVE))
				.shape(
						"P  ",
						"PPP",
						"PPP"
				)
				.setIngredient('P', Material.MANGROVE_PLANKS);
		mangrove.setGroup(this.namespacedKey.getNamespace() + ":rocking_chair");
		ShapedRecipe acaciaPaintable = new ShapedRecipe(Type.PAINTABLE_ACACIA.namespacedKey, this.createItemStack(Type.PAINTABLE_ACACIA))
				.shape(
						"P  ",
						"PLP",
						"PPP"
				)
				.setIngredient('P', Material.ACACIA_PLANKS)
				.setIngredient('L', Material.LEATHER);
		acacia.setGroup(this.namespacedKey.getNamespace() + ":paintable_rocking_chair");
		ShapedRecipe birchPaintable = new ShapedRecipe(Type.PAINTABLE_BIRCH.namespacedKey, this.createItemStack(Type.PAINTABLE_BIRCH))
				.shape(
						"P  ",
						"PLP",
						"PPP"
				)
				.setIngredient('P', Material.BIRCH_PLANKS)
				.setIngredient('L', Material.LEATHER);
		birch.setGroup(this.namespacedKey.getNamespace() + ":paintable_rocking_chair");
		ShapedRecipe crimsonPaintable = new ShapedRecipe(Type.PAINTABLE_CRIMSON.namespacedKey, this.createItemStack(Type.PAINTABLE_CRIMSON))
				.shape(
						"P  ",
						"PLP",
						"PPP"
				)
				.setIngredient('P', Material.CRIMSON_PLANKS)
				.setIngredient('L', Material.LEATHER);
		crimson.setGroup(this.namespacedKey.getNamespace() + ":paintable_rocking_chair");
		ShapedRecipe darkOakPaintable = new ShapedRecipe(Type.PAINTABLE_DARK_OAK.namespacedKey, this.createItemStack(Type.PAINTABLE_DARK_OAK))
				.shape(
						"P  ",
						"PLP",
						"PPP"
				)
				.setIngredient('P', Material.DARK_OAK_PLANKS)
				.setIngredient('L', Material.LEATHER);
		darkOak.setGroup(this.namespacedKey.getNamespace() + ":paintable_rocking_chair");
		ShapedRecipe junglePaintable = new ShapedRecipe(Type.PAINTABLE_JUNGLE.namespacedKey, this.createItemStack(Type.PAINTABLE_JUNGLE))
				.shape(
						"P  ",
						"PLP",
						"PPP"
				)
				.setIngredient('P', Material.JUNGLE_PLANKS)
				.setIngredient('L', Material.LEATHER);
		jungle.setGroup(this.namespacedKey.getNamespace() + ":paintable_rocking_chair");
		ShapedRecipe oakPaintable = new ShapedRecipe(Type.PAINTABLE_OAK.namespacedKey, this.createItemStack(Type.PAINTABLE_OAK))
				.shape(
						"P  ",
						"PLP",
						"PPP"
				)
				.setIngredient('P', Material.OAK_PLANKS)
				.setIngredient('L', Material.LEATHER);
		oak.setGroup(this.namespacedKey.getNamespace() + ":paintable_rocking_chair");
		ShapedRecipe sprucePaintable = new ShapedRecipe(Type.PAINTABLE_SPRUCE.namespacedKey, this.createItemStack(Type.PAINTABLE_SPRUCE))
				.shape(
						"P  ",
						"PLP",
						"PPP"
				)
				.setIngredient('P', Material.SPRUCE_PLANKS)
				.setIngredient('L', Material.LEATHER);
		spruce.setGroup(this.namespacedKey.getNamespace() + ":paintable_rocking_chair");
		ShapedRecipe warpedPaintable = new ShapedRecipe(Type.PAINTABLE_WARPED.namespacedKey, this.createItemStack(Type.PAINTABLE_WARPED))
				.shape(
						"P  ",
						"PLP",
						"PPP"
				)
				.setIngredient('P', Material.WARPED_PLANKS)
				.setIngredient('L', Material.LEATHER);
		warped.setGroup(this.namespacedKey.getNamespace() + ":paintable_rocking_chair");
		ShapedRecipe mangrovePaintable = new ShapedRecipe(Type.PAINTABLE_MANGROVE.namespacedKey, this.createItemStack(Type.PAINTABLE_MANGROVE))
				.shape(
						"P  ",
						"PLP",
						"PPP"
				)
				.setIngredient('P', Material.MANGROVE_PLANKS)
				.setIngredient('L', Material.LEATHER);
		mangrove.setGroup(this.namespacedKey.getNamespace() + ":paintable_rocking_chair");
		//</editor-fold>
		this.recipes = Lists.newArrayList(
				acacia, birch, crimson, darkOak, jungle, oak, spruce, warped, mangrove,
				acaciaPaintable, birchPaintable, crimsonPaintable, darkOakPaintable, junglePaintable, oakPaintable, sprucePaintable, warpedPaintable, mangrovePaintable
		);
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
		ACACIA("Акациевое кресло-качалка", 1038, null),
		PAINTABLE_ACACIA("Акациевое кресло-качалка", 1039, Badges.PAINTABLE_LORE_LIST),
		BIRCH("Берёзовое кресло-качалка", 1040, null),
		PAINTABLE_BIRCH("Берёзовое кресло-качалка", 1041, Badges.PAINTABLE_LORE_LIST),
		CRIMSON( "Багровое кресло-качалка", 1042, null),
		PAINTABLE_CRIMSON( "Багровое кресло-качалка", 1043, Badges.PAINTABLE_LORE_LIST),
		DARK_OAK("Кресло-качалка из тёмного дуба", 1044, null),
		PAINTABLE_DARK_OAK("Кресло-качалка из тёмного дуба", 1045, Badges.PAINTABLE_LORE_LIST),
		JUNGLE("Тропическое кресло-качалка", 1046, null),
		PAINTABLE_JUNGLE("Тропическое кресло-качалка", 1047, Badges.PAINTABLE_LORE_LIST),
		OAK("Дубовое кресло-качалка", 1048, null),
		PAINTABLE_OAK("Дубовое кресло-качалка", 1049, Badges.PAINTABLE_LORE_LIST),
		SPRUCE("Еловое кресло-качалка", 1050, null),
		PAINTABLE_SPRUCE("Еловое кресло-качалка", 1051, Badges.PAINTABLE_LORE_LIST),
		WARPED("Искажённое кресло-качалка", 1052, null),
		PAINTABLE_WARPED("Искажённое кресло-качалка", 1053, Badges.PAINTABLE_LORE_LIST),
		MANGROVE("Мангровое кресло-качалка", 1197, null),
		PAINTABLE_MANGROVE("Мангровое кресло-качалка", 1198, Badges.PAINTABLE_LORE_LIST);
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
				int customModelData,
				@Nullable List<Component> lore
		) {
			this.namespacedKey = new NamespacedKey(MSDecor.getInstance(), this.name().toLowerCase(Locale.ROOT) + "_rocking_chair");
			this.itemName = itemName;
			this.customModelData = customModelData;
			this.lore = lore;
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
