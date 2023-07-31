package com.minersstudios.msblock.customblock.file;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.file.adapter.*;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.config.ConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * Represents a handler for a custom block file,
 * that is used to load and save {@link CustomBlockData}
 * to and from a file in json format using {@link Gson}.
 * <p>
 * You can create a {@link CustomBlockFile} using
 * {@link #create(File, CustomBlockData)} or {@link #create(File)}.
 * <p>
 * The first method creates a new {@link CustomBlockFile} with the
 * specified file and data, and the second method creates a new
 * {@link CustomBlockFile} from the specified file and loads the
 * {@link CustomBlockData} from the file, or returns null if the
 * error occurs.
 */
public class CustomBlockFile {
    private CustomBlockData data;
    private final File file;

    private static final Gson GSON =
            new GsonBuilder()
                    .registerTypeAdapter(Recipe.class, new RecipeAdapter())
                    .registerTypeAdapter(RecipeChoice.class, new RecipeChoiceAdapter())
                    .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                    .registerTypeAdapter(NoteBlockData.class, new NoteBlockDataAdapter())
                    .registerTypeAdapter(PlacingType.class, new PlacingTypeAdapter())
                    .setPrettyPrinting()
                    .create();

    private CustomBlockFile(
            @NotNull File file,
            @Nullable CustomBlockData data
    ) throws IllegalArgumentException {
        Preconditions.checkArgument(file.getPath().endsWith(".json"), "File must be a json file");

        this.file = file;
        this.data = data;
    }

    /**
     * Creates a {@link CustomBlockFile} with the specified file
     * and data
     *
     * @param file The file to use
     * @param data The data to use
     * @return A new {@link CustomBlockFile} with the specified file
     *         and data
     * @throws IllegalArgumentException If the file is not a json file
     */
    @Contract("_, _ -> new")
    public static @NotNull CustomBlockFile create(
            @NotNull File file,
            @NotNull CustomBlockData data
    ) throws IllegalArgumentException {
        return new CustomBlockFile(file, data);
    }

    /**
     * Creates a {@link CustomBlockFile} from the specified file
     * and loads the {@link CustomBlockData} from the file. All
     * errors are logged to the console and null is returned if
     * an error occurs.
     *
     * @param file The file to load
     * @return The loaded {@link CustomBlockFile} from the file,
     *         or null if the file is not found,
     *         or if the file is not a valid json file
     * @see #load()
     */
    public static @Nullable CustomBlockFile create(@NotNull File file) {
        try {
            CustomBlockFile customBlockFile = new CustomBlockFile(file, null);

            customBlockFile.load();
            return customBlockFile;
        } catch (ConfigurationException e) {
            MSLogger.log(Level.SEVERE, "Failed to create a custom block file from file", e);
        } catch (IllegalArgumentException e) {
            MSLogger.log(Level.SEVERE, "The file is not a json file", e);
        }
        return null;
    }

    /**
     * @return The file of this custom block file
     */
    public @NotNull File getFile() {
        return this.file;
    }

    /**
     * @return The data of this custom block file
     */
    public @NotNull CustomBlockData getData() {
        return this.data;
    }

    /**
     * Loads the {@link CustomBlockData} from the specified file
     *
     * @throws ConfigurationException If the file is not found,
     *                                or if the file is not a valid json file,
     *                                or if an I/O error occurs
     */
    public void load() throws ConfigurationException {
        String path = this.file.getAbsolutePath();

        if (!this.file.exists()) {
            throw new ConfigurationException("File not found: " + path);
        }

        try {
            String json = Files.readString(this.file.toPath(), StandardCharsets.UTF_8);
            this.data = GSON.fromJson(json, CustomBlockData.class);
        } catch (Exception e) {
            throw new ConfigurationException("Failed to load file: " + path, e);
        }
    }

    /**
     * Creates a new file and saves the {@link CustomBlockData} to the file
     *
     * @see #save()
     */
    public void create() {
        File directory = this.file.getParentFile();

        try {
            if (
                    !directory.exists()
                    && !directory.mkdirs()
            ) {
                MSLogger.warning("Failed to create a new directory: " + directory.getAbsolutePath());
            }

            if (this.file.createNewFile()) {
                this.save();
            }
        } catch (IOException e) {
            MSLogger.log(Level.SEVERE, "Failed to create a new file: " + this.file.getAbsolutePath(), e);
        }
    }

    /**
     * Saves the {@link CustomBlockData} to the file
     */
    public void save() {
        try (var writer = new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8)) {
            GSON.toJson(this.data, writer);
        } catch (IOException e) {
            MSLogger.log(Level.SEVERE, "Failed to save a file: " + this.file.getAbsolutePath(), e);
        }
    }
}
