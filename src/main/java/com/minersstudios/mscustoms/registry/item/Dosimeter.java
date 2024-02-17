package com.minersstudios.mscustoms.registry.item;

import com.minersstudios.mscore.inventory.recipe.builder.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.builder.ShapedRecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.custom.item.CustomItemImpl;
import com.minersstudios.mscustoms.custom.item.CustomItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Dosimeter extends CustomItemImpl {
    private static final String KEY;
    private static final ItemStack ITEM_STACK;

    static {
        KEY = "dosimeter";
        ITEM_STACK = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta meta = ITEM_STACK.getItemMeta();

        meta.displayName(ChatUtils.createDefaultStyledText("Дозиметр радиации"));
        meta.setCustomModelData(1372);
        ITEM_STACK.setItemMeta(meta);
    }

    public Dosimeter() {
        super(KEY, ITEM_STACK);
        this.setEnabled(false);
    }

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<RecipeEntry> initRecipes() {
        return Collections.singletonList(
                RecipeEntry.fromBuilder(
                        RecipeBuilder.shaped()
                        .namespacedKey(this.namespacedKey)
                        .result(this.itemStack)
                        .shape(
                                "III",
                                "ILI",
                                "IRI"
                        )
                        .ingredients(
                                ShapedRecipeBuilder.itemStack('I', CustomItemType.PLUMBUM_INGOT.getCustomItem().getItem()),
                                ShapedRecipeBuilder.material('L', Material.REDSTONE_LAMP),
                                ShapedRecipeBuilder.material('R', Material.REDSTONE_TORCH)
                        ),
                        true
                )
        );
    }

    public boolean isEnabled() {
        return this.itemStack.getItemMeta().getPersistentDataContainer()
                .getOrDefault(
                        this.namespacedKey,
                        PersistentDataType.BYTE,
                        (byte) 0
                ) == 1;
    }

    public void setEnabled(final boolean enabled) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();

        if (itemMeta == null) {
            return;
        }

        itemMeta.getPersistentDataContainer().set(this.namespacedKey, PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));

        if (!enabled) {
            itemMeta.setCustomModelData(ScreenType.OFF.customModelData);
        } else {
            itemMeta.setCustomModelData(ScreenType.GREEN.customModelData);
        }

        this.itemStack.setItemMeta(itemMeta);
    }

    public @NotNull ScreenType getScreenType() {
        final ScreenType screenType = ScreenType.getScreenType(this.itemStack.getItemMeta().getCustomModelData());
        return screenType == null ? ScreenType.OFF : screenType;
    }

    public void setScreenType(final @NotNull ScreenType screenType) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();

        itemMeta.setCustomModelData(screenType.customModelData);
        this.itemStack.setItemMeta(itemMeta);
    }

    public void setScreenTypeByRadius(
            final @NotNull List<Double> radii,
            final @Nullable Double radius
    ) {
        if (radius == null || radii.isEmpty()) {
            this.setScreenType(ScreenType.GREEN);
            return;
        }

        final var firstHalf = radii.subList(0, radii.size() / 2);

        if (firstHalf.contains(radius)) {
            this.setScreenType(ScreenType.RED);
        } else {
            this.setScreenType(ScreenType.YELLOW);
        }
    }

    public enum ScreenType {
        OFF(1372),
        GREEN(1373),
        YELLOW(1374),
        RED(1375);

        public static final Set<ScreenType> VALUES = Set.of(values());

        private final int customModelData;

        ScreenType(final int customModelData) {
            this.customModelData = customModelData;
        }

        public int getCustomModelData() {
            return this.customModelData;
        }

        public static @Nullable ScreenType getScreenType(final int customModelData) {
            for (final var screenType : VALUES) {
                if (screenType.customModelData == customModelData) {
                    return screenType;
                }
            }

            return null;
        }
    }
}
