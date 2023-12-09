package com.minersstudios.msblock.api;

import com.google.gson.JsonElement;
import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.file.*;
import com.minersstudios.msblock.api.file.adapter.RecipeAdapter;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.msessentials.menu.CraftsMenu;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

/**
 * Class representing the data of a custom block. This class
 * holds information about the block's key, block settings,
 * drop settings, sound group, and recipe entries. It also
 * holds a default instance of the CustomBlockData class which
 * is used as a fallback in some scenarios.
 *
 * @see #getDefault()
 */
public class CustomBlockData {
    private final String key;
    private final BlockSettings blockSettings;
    private final DropSettings dropSettings;
    private final SoundGroup soundGroup;
    private Set<RecipeEntry> recipeEntries;

    private static final CustomBlockData DEFAULT = new CustomBlockData(
            //<editor-fold desc="Default note block params" defaultstate="collapsed">
            "default",
            new BlockSettings(
                    11.0f,
                    BlockSettings.Tool.create(
                            ToolType.AXE,
                            false
                    ),
                    BlockSettings.Placing.create(
                            PlacingType.defaultType(NoteBlockData.defaultData())
                    )
            ),
            new DropSettings(
                    new ItemStack(Material.NOTE_BLOCK),
                    0
            ),
            SoundGroup.WOOD
            //</editor-fold>
    );

    /**
     * Constructs the CustomBlockData with the specified parameters
     *
     * @param key           The key of the custom block data
     * @param blockSettings The block settings of the custom block data
     * @param dropSettings  The drop settings of the custom block data
     * @param soundGroup    The sound group of the custom block data
     * @param recipeEntries The recipe entries of the custom block data
     */
    public CustomBlockData(
            final @NotNull String key,
            final @NotNull BlockSettings blockSettings,
            final @NotNull DropSettings dropSettings,
            final @NotNull SoundGroup soundGroup,
            final @NotNull Set<RecipeEntry> recipeEntries
    ) {
        this.key = key.toLowerCase(Locale.ENGLISH);
        this.blockSettings = blockSettings;
        this.dropSettings = dropSettings;
        this.soundGroup = soundGroup;
        this.recipeEntries = recipeEntries;
    }

    /**
     * Constructs the CustomBlockData with the specified parameters
     *
     * @param key           The key of the custom block data
     * @param blockSettings The block settings of the custom block data
     * @param dropSettings  The drop settings of the custom block data
     * @param soundGroup    The sound group of the custom block data
     * @param recipeEntries The recipe entries of the custom block data
     */
    public CustomBlockData(
            final @NotNull String key,
            final @NotNull BlockSettings blockSettings,
            final @NotNull DropSettings dropSettings,
            final @NotNull SoundGroup soundGroup,
            final RecipeEntry @NotNull ... recipeEntries
    ) {
        this(
                key,
                blockSettings,
                dropSettings,
                soundGroup,
                Set.of(recipeEntries)
        );
    }

    /**
     * @return Default custom block data with the following parameters:
     *     <br> - key: "default"
     *     <br> - hardness: 11.0f
     *     <br> - custom model data: 0
     *     <br> - tool type: {@link ToolType#AXE}
     *     <br> - note block data: {@link NoteBlockData#defaultData()}
     *     <br> - sound group: {@link SoundGroup#WOOD}
     * @see #DEFAULT
     */
    public static @NotNull CustomBlockData getDefault() {
        return DEFAULT;
    }

    /**
     * Loads the custom block data from the specified file
     *
     * @param file The file to load the custom block data from
     * @return The custom block data loaded from the file,
     *         or null if an error occurred
     * @see CustomBlockFile#create(File)
     */
    public static @Nullable CustomBlockData fromFile(final @NotNull File file) {
        final CustomBlockFile blockFile = CustomBlockFile.create(file);

        if (blockFile == null) {
            MSBlock.logger().severe("Failed to load custom block file: " + file.getName());
            return null;
        }

        return blockFile.getData();
    }

    /**
     * @return The key of the custom block data
     */
    public @NotNull String getKey() {
        return this.key;
    }

    /**
     * @return The block settings of the custom block data
     * @see BlockSettings
     */
    public @NotNull BlockSettings getBlockSettings() {
        return this.blockSettings;
    }

    /**
     * @return The drop settings of the custom block data
     * @see DropSettings
     */
    public @NotNull DropSettings getDropSettings() {
        return this.dropSettings;
    }

    /**
     * @return The sound group of the custom block data
     * @see SoundGroup
     */
    public @NotNull SoundGroup getSoundGroup() {
        return this.soundGroup;
    }

    /**
     * @return The recipe entries of the custom block data
     */
    public @NotNull @Unmodifiable Set<RecipeEntry> getRecipeEntries() {
        return this.recipeEntries == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(this.recipeEntries);
    }

    /**
     * Sets the recipe entries of the custom block data
     *
     * @param recipeEntries The new recipe entries of the custom block data
     */
    public void setRecipeEntries(final @NotNull Set<RecipeEntry> recipeEntries) {
        if (this.recipeEntries != null) {
            this.unregisterRecipes();
        }

        this.recipeEntries = recipeEntries;
    }

    /**
     * @return True if the custom block data is the default instance
     * @see #getDefault()
     */
    public boolean isDefault() {
        return this == DEFAULT;
    }

    /**
     * Converts the custom block data to a custom block file
     *
     * @param file The file that stores the custom block data
     * @return The custom block file created from the custom block data
     * @throws IllegalArgumentException If the file is not a json file
     * @see CustomBlockFile#create(File)
     */
    public @NotNull CustomBlockFile toFile(final @NotNull File file) throws IllegalArgumentException {
        return CustomBlockFile.create(file, this);
    }

    /**
     * Creates an ItemStack based on the custom block data
     * parameters. The ItemStack will have the custom block
     * data key stored in its persistent data container.
     *
     * @return The new ItemStack based on the custom block data
     *         parameters
     * @see CustomBlockRegistry#TYPE_NAMESPACED_KEY
     */
    public @NotNull ItemStack craftItemStack() {
        final ItemStack itemStack = this.dropSettings.getItem();
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(
                CustomBlockRegistry.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.key
        );

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Registers the recipes of the custom block data.
     * If the showInCraftsMenu parameter of a recipe entry
     * is true, the recipe will be added to the global
     * cache's custom block recipes list. This list is
     * used to display the custom block recipes in the
     * {@link CraftsMenu}
     */
    public void registerRecipes(final @NotNull JsonElement recipeJson) {
        try {
            this.setRecipeEntries(RecipeAdapter.deserializeEntries(new ItemStack(this.craftItemStack()), recipeJson.getAsJsonArray()));
        } catch (final Exception e) {
            MSBlock.logger().log(Level.SEVERE, "Failed to deserialize recipes for custom block data: " + this.key, e);
            return;
        }

        final MSBlock plugin = MSBlock.singleton();
        final Server server = plugin.getServer();

        for (final var recipeEntry : this.recipeEntries) {
            final Recipe recipe = recipeEntry.getRecipe();

            plugin.runTask(() -> server.addRecipe(recipe));

            if (recipeEntry.isShowInCraftsMenu()) {
                MSPlugin.globalCache().customBlockRecipes.add(recipe);
            }
        }
    }

    /**
     * Unregisters the recipes of the custom block data
     */
    public void unregisterRecipes() {
        final var recipes = MSPlugin.globalCache().customBlockRecipes;

        for (final var recipeEntry : this.recipeEntries) {
            final Recipe recipe = recipeEntry.getRecipe();

            recipes.remove(recipe);

            if (recipe instanceof final Keyed keyed) {
                Bukkit.removeRecipe(keyed.getKey());
            }
        }
    }
}
