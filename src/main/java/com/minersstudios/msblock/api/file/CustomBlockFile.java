package com.minersstudios.msblock.api.file;

import com.google.gson.*;
import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.CustomBlockData;
import com.minersstudios.msblock.api.file.adapter.*;
import com.minersstudios.mscore.plugin.config.ConfigurationException;
import com.minersstudios.mscore.util.MSPluginUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
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
                    .registerTypeAdapter(NamespacedKey.class, new NamespacedKeyAdapter())
                    .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                    .registerTypeAdapter(Recipe.class, new RecipeAdapter())
                    .registerTypeAdapter(RecipeChoice.class, new RecipeChoiceAdapter())
                    .registerTypeAdapter(SoundCategory.class, new EnumDeserializer<>(SoundCategory.class))
                    .registerTypeAdapter(ToolType.class, new EnumDeserializer<>(ToolType.class))
                    .registerTypeAdapter(PlacingType.class, new PlacingTypeAdapter())
                    .registerTypeAdapter(NoteBlockData.class, new NoteBlockDataAdapter())
                    .setPrettyPrinting()
                    .create();

    private CustomBlockFile(
            final @NotNull File file,
            final @Nullable CustomBlockData data
    ) throws IllegalArgumentException {
        if (!file.getPath().endsWith(".json")) {
            throw new IllegalArgumentException("File must be a json file");
        }

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
            final @NotNull File file,
            final @NotNull CustomBlockData data
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
    public static @Nullable CustomBlockFile create(final @NotNull File file) {
        try {
            final CustomBlockFile customBlockFile = new CustomBlockFile(file, null);

            customBlockFile.load();
            return customBlockFile;
        } catch (final ConfigurationException e) {
            MSBlock.logger().log(Level.SEVERE, "Failed to create a custom block file from file", e);
        } catch (final IllegalArgumentException e) {
            MSBlock.logger().log(Level.SEVERE, "The file is not a json file", e);
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
        final String path = this.file.getAbsolutePath();

        if (!this.file.exists()) {
            throw new ConfigurationException("File not found: " + path);
        }

        try {
            this.data = deserialize(Files.readString(this.file.toPath(), StandardCharsets.UTF_8));
        } catch (final Exception e) {
            throw new ConfigurationException("Failed to load custom block data from file: " + path, e);
        }
    }

    /**
     * Saves the {@link CustomBlockData} to the file
     */
    public void save() {
        final File directory = this.file.getParentFile();

        if (
                !directory.exists()
                && !directory.mkdirs()
        ) {
            MSBlock.logger().warning("Failed to create a new directory: " + directory.getAbsolutePath());
        }

        try (final var writer = new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8)) {
            GSON.toJson(this.data, writer);
        } catch (final IOException e) {
            MSBlock.logger().log(Level.SEVERE, "Failed to save a file: " + this.file.getAbsolutePath(), e);
        }
    }

    /**
     * @return The {@link Gson} instance used to serialize and deserialize
     *         this custom block file
     */
    public static @NotNull Gson getGson() {
        return GSON;
    }

    private static @NotNull CustomBlockData deserialize(final @NotNull String json) {
        final JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        final JsonElement recipeEntries = jsonObject.get("recipeEntries");

        if (recipeEntries != null) {
            jsonObject.remove("recipeEntries");

            final CustomBlockData data = GSON.fromJson(jsonObject, CustomBlockData.class);
            MSBlock.getInstance().runTaskTimer(task -> {
                if (MSPluginUtils.isLoadedCustoms()) {
                    task.cancel();
                    data.registerRecipes(recipeEntries);
                }
            }, 0L, 10L);

            return data;
        }

        return GSON.fromJson(jsonObject, CustomBlockData.class);
    }
}
