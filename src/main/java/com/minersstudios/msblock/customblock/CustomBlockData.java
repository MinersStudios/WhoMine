package com.minersstudios.msblock.customblock;

import com.minersstudios.msblock.customblock.file.*;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class CustomBlockData implements Cloneable {
    private final String key;
    private final BlockSettings blockSettings;
    private final DropSettings dropSettings;
    private final SoundGroup soundGroup;
    private final RecipeEntry[] recipeEntries;

    private static final CustomBlockData DEFAULT = new CustomBlockData(
            //<editor-fold desc="Default note block params">
            "default",
            new BlockSettings(
                    11.0f,
                    new BlockSettings.Tool(
                            ToolType.AXE,
                            false
                    ),
                    new BlockSettings.Placing(
                            new PlacingType.Default(
                                    NoteBlockData.getDefault()
                            )
                    )
            ),
            new DropSettings(
                    new ItemStack(Material.NOTE_BLOCK),
                    0
            ),
            SoundGroup.wood()
            //</editor-fold>
    );

    /**
     * @param key           The key of the custom block data
     * @param blockSettings The block settings of the custom block data
     * @param dropSettings  The drop settings of the custom block data
     * @param soundGroup    The sound group of the custom block data
     * @param recipeEntries The recipe entries of the custom block data
     */
    public CustomBlockData(
            @NotNull String key,
            @NotNull BlockSettings blockSettings,
            @NotNull DropSettings dropSettings,
            @NotNull SoundGroup soundGroup,
            RecipeEntry... recipeEntries
    ) {
        this.key = key;
        this.blockSettings = blockSettings;
        this.dropSettings = dropSettings;
        this.soundGroup = soundGroup;
        this.recipeEntries = recipeEntries;
    }

    /**
     * @return Default custom block data with the following parameters:
     *     <p> - key: "default"
     *     <p> - hardness: 11.0f
     *     <p> - custom model data: 0
     *     <p> - tool type: {@link ToolType#AXE}
     *     <p> - note block data: {@link NoteBlockData#getDefault()}
     *     <p> - sound group: {@link SoundGroup#wood()}
     * @see #DEFAULT
     */
    public static @NotNull CustomBlockData getDefault() {
        return DEFAULT;
    }

    /**
     *
     *
     * @param file The file to load the custom block data from
     * @return The custom block data loaded from the file,
     *         or null if an error occurred
     * @see CustomBlockFile#create(File)
     */
    public static @Nullable CustomBlockData fromFile(@NotNull File file) {
        CustomBlockFile blockFile = CustomBlockFile.create(file);

        if (blockFile == null) {
            MSLogger.severe("Failed to load custom block file: " + file.getName());
            return null;
        }

        return blockFile.getData();
    }

    public @NotNull String getKey() {
        return this.key;
    }

    public @NotNull BlockSettings getBlockSettings() {
        return this.blockSettings;
    }

    public @NotNull DropSettings getDropSettings() {
        return this.dropSettings;
    }

    public @NotNull SoundGroup getSoundGroup() {
        return this.soundGroup;
    }

    public RecipeEntry[] getRecipes() {
        return this.recipeEntries;
    }

    public float calculateDigSpeed(@NotNull Player player) {
        float base = 1.0f;
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        Material material = itemInMainHand.getType();
        PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.FAST_DIGGING);
        ToolType toolType = this.blockSettings.tool().type();

        if (toolType == ToolType.fromMaterial(material)) {
            base = ToolTier.fromMaterial(material).getDigSpeed();

            if (itemInMainHand.containsEnchantment(Enchantment.DIG_SPEED)) {
                base += itemInMainHand.getEnchantmentLevel(Enchantment.DIG_SPEED) * 0.3f;
            }
        } else if (toolType == ToolType.PICKAXE) {
            base /= 30.0f;
        } else {
            base /= 5.0f;
        }

        if (potionEffect != null) {
            base *= (potionEffect.getAmplifier() + 1) * 0.32f;
        }

        return base / this.blockSettings.hardness();
    }

    public @NotNull ItemStack craftItemStack() {
        ItemStack itemStack = this.dropSettings.item();
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(
                CustomBlockRegistry.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.key
        );

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void registerRecipes() {
        for (var recipeEntry : this.recipeEntries) {
            Recipe recipe = recipeEntry.getRecipe();

            Bukkit.addRecipe(recipe);

            if (recipeEntry.isShowInCraftsMenu()) {
                MSPlugin.getGlobalCache().customBlockRecipes.add(recipe);
            }
        }
    }

    public boolean isDefault() {
        return this.equals(DEFAULT);
    }

    @Override
    public @NotNull CustomBlockData clone() {
        try {
            return (CustomBlockData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
