package com.minersstudios.mscustoms.custom.block.file;

import com.google.gson.*;
import com.minersstudios.mscore.inventory.recipe.entry.RecipeEntry;
import com.minersstudios.mscore.plugin.config.ConfigurationException;
import com.minersstudios.mscore.utility.SharedConstants;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.block.CustomBlockData;
import com.minersstudios.mscustoms.custom.block.file.adapter.*;
import com.minersstudios.mscustoms.custom.block.params.NoteBlockData;
import com.minersstudios.mscustoms.custom.block.params.PlacingType;
import com.minersstudios.mscustoms.custom.block.params.ToolType;
import com.minersstudios.mscustoms.custom.block.params.settings.Placing;
import com.minersstudios.mscustoms.sound.Sound;
import com.minersstudios.mscustoms.sound.SoundAdapter;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a handler for a custom block file, that is used to load and save
 * {@link CustomBlockData} to and from a file in json format using {@link Gson}.
 * <br>
 * You can create a {@link CustomBlockFile} using
 * {@link #create(File, CustomBlockData)} or {@link #create(MSCustoms, File)}.
 * <br>
 * The first method creates a new {@link CustomBlockFile} with the specified
 * file and data, and the second method creates a new {@link CustomBlockFile}
 * from the specified file and loads the {@link CustomBlockData} from the file,
 * or returns null if the error occurs.
 */
public final class CustomBlockFile {
    private final File file;
    private CustomBlockData data;

    private static final Gson GSON =
            new GsonBuilder()
            .registerTypeAdapter(NamespacedKey.class, new NamespacedKeyAdapter(SharedConstants.MSBLOCK_NAMESPACE))
            .registerTypeAdapter(ItemStack.class,     new ItemStackAdapter())
            .registerTypeAdapter(Recipe.class,        new RecipeAdapter())
            .registerTypeAdapter(RecipeChoice.class,  new RecipeChoiceAdapter())
            .registerTypeAdapter(SoundCategory.class, new EnumAdapter<>(SoundCategory.values()))
            .registerTypeAdapter(ToolType.class,      new EnumAdapter<>(ToolType.values()))
            .registerTypeAdapter(Placing.class,       new PlacingAdapter())
            .registerTypeAdapter(PlacingType.class,   new PlacingTypeAdapter())
            .registerTypeAdapter(NoteBlockData.class, new NoteBlockDataAdapter())
            .registerTypeAdapter(Sound.class,         new SoundAdapter())
            .registerTypeAdapter(RecipeEntry.class,   new RecipeEntryAdapter())
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
     * Creates a {@link CustomBlockFile} with the specified file and data
     *
     * @param file The file to use
     * @param data The data to use
     * @return A new {@link CustomBlockFile} with the specified file and data
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
     * Creates a {@link CustomBlockFile} from the specified file and loads the
     * {@link CustomBlockData} from the file. All errors are logged to the
     * console, and null is returned if an error occurs.
     *
     * @param plugin The plugin to load the data for
     * @param file   The file to load
     * @return The loaded {@link CustomBlockFile} from the file,
     *         or null if the file is not found,
     *         or if the file is not a valid json file
     * @see #load(MSCustoms)
     */
    public static @Nullable CustomBlockFile create(
            final @NotNull MSCustoms plugin,
            final @NotNull File file
    ) {
        final Logger logger = plugin.getLogger();

        try {
            final CustomBlockFile customBlockFile = new CustomBlockFile(file, null);

            customBlockFile.load(plugin);

            return customBlockFile;
        } catch (final ConfigurationException e) {
            logger.log(
                    Level.SEVERE,
                    "Failed to create a custom block file from file",
                    e
            );
        } catch (final IllegalArgumentException e) {
            logger.log(
                    Level.SEVERE,
                    "The file is not a json file",
                    e
            );
        }

        return null;
    }

    /**
     * @return The {@link Gson} instance used to serialize and deserialize this
     *         custom block file
     */
    public static @NotNull Gson gson() {
        return GSON;
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
    public void load(final @NotNull MSCustoms plugin) throws ConfigurationException {
        final String path = this.file.getAbsolutePath();

        if (!this.file.exists()) {
            throw new ConfigurationException("File not found: " + path);
        }

        try {
            this.data = deserialize(
                    plugin,
                    Files.readString(this.file.toPath(), StandardCharsets.UTF_8)
            );
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
            MSCustoms.logger().warning("Failed to create a new directory: " + directory.getAbsolutePath());
        }

        try (final var writer = new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8)) {
            GSON.toJson(this.data, writer);
        } catch (final IOException e) {
            MSCustoms.logger().log(
                    Level.SEVERE,
                    "Failed to save a file: " + this.file.getAbsolutePath(),
                    e
            );
        }
    }

    private static @NotNull CustomBlockData deserialize(
            final @NotNull MSCustoms plugin,
            final @NotNull String json
    ) {
        final JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        final JsonElement recipeEntries = jsonObject.get("recipeEntries");

        if (recipeEntries != null) {
            jsonObject.remove("recipeEntries");

            final CustomBlockData data = new CustomBlockData(GSON.fromJson(jsonObject, CustomBlockData.class));
            final var list = plugin.getCache().getBlockDataRecipes();

            synchronized (list) {
                list.add(Map.entry(data, recipeEntries));
            }

            return data;
        }

        return GSON.fromJson(jsonObject, CustomBlockData.class);
    }
}
