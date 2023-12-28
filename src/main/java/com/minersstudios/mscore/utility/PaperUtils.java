package com.minersstudios.mscore.utility;

import com.google.common.base.Joiner;
import com.minersstudios.mscore.plugin.MSLogger;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.jetbrains.annotations.*;

import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for paper
 */
public final class PaperUtils {

    @Contract(" -> fail")
    private PaperUtils() throws AssertionError {
        throw new AssertionError("Utility class");
    }

    /**
     * Edits the given config file.
     * <br>
     * Use {@link EditResponse#set(String, Object)} to set the value of a config
     * entry.
     * <br>
     * Use {@link EditResponse#save()} to save the config file.
     *
     * @param configType The config type to edit
     * @param server     The server that owns the config file
     * @return An edit response
     */
    @Contract("_, _ -> new")
    public static @NotNull PaperUtils.EditResponse editConfig(
            final @NotNull ConfigType configType,
            final @NotNull Server server
    ) {
        try {
            return new EditResponseImpl(configType, server);
        } catch (final FileNotFoundException e) {
            MSLogger.severe(
                    "Failed to open " + configType + " because the file was not found",
                    e
            );
        } catch (final InvalidConfigurationException e) {
            MSLogger.severe(
                    "Failed to open " + configType + " because the configuration was invalid",
                    e
            );
        } catch (final IOException e) {
            MSLogger.severe(
                    "Failed to open " + configType,
                    e
            );
        }

        return new EmptyEditResponse();
    }

    @ApiStatus.Internal
    public interface EditResponse {

        /**
         * @return An unmodifiable view of the edit history
         */
        @NotNull @UnmodifiableView Map<String, HistoryEntry> getHistory();

        /**
         * @return A string representation of the edit history. The string
         *         representation consists of the path and the history entry.
         */
        @NotNull String getHistoryAsString();

        /**
         * Sets the value of the given path to the given value if it is not
         * already set. If the set value is equal to the current value, the
         * value will not be updated and the configuration file will not be
         * saved.
         *
         * @param path  The path of the value
         * @param value The value to set
         * @return This edit response
         */
        @NotNull PaperUtils.EditResponse set(
                final @NotNull String path,
                final @NotNull Object value
        );

        /**
         * Sets the value of the given path to the given value if it is not
         * already set. If the set value is equal to the current value, the
         * value will not be updated and the configuration file will not be
         * saved.
         *
         * @param path  The path of the value
         * @param value The value to set
         * @return This edit response
         */
        @NotNull PaperUtils.EditResponse setIfAbsent(
                final @NotNull String path,
                final @NotNull Object value
        );

        /**
         * Saves the config file. If no changes were made, the config file will
         * not be saved and this method will return true.
         *
         * @return True if the config was saved successfully, or false when the
         *         IOException was thrown, or when response {@link #isEmpty()}
         */
        boolean save();

        /**
         * @return A string representation of this edit response
         */
        @Override
        @NotNull String toString();

        /**
         * @return True if the config file was not successfully opened
         */
        boolean isEmpty();
    }

    private static class EmptyEditResponse implements EditResponse {

        @Override
        public @NotNull @UnmodifiableView Map<String, HistoryEntry> getHistory() {
            return Collections.emptyMap();
        }

        @Override
        public @NotNull String getHistoryAsString() {
            return "";
        }

        @Override
        public @NotNull EditResponse set(
                final @NotNull String path,
                final @NotNull Object value
        ) {
            return this;
        }

        @Override
        public @NotNull EditResponse setIfAbsent(
                final @NotNull String path,
                final @NotNull Object value
        ) {
            return this;
        }

        @Override
        public boolean save() {
            return false;
        }

        @Override
        public @NotNull String toString() {
            return "EmptyEditResponse{}";
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    private static class EditResponseImpl implements EditResponse {
        private final Map<String, HistoryEntry> history;
        private final File file;
        private final YamlConfiguration yaml;
        private final MinecraftServer server;

        private EditResponseImpl(
                final @NotNull ConfigType configType,
                final @NotNull Server server
        ) throws IOException, InvalidConfigurationException {
            this.history = new HashMap<>();
            this.file = new File(configType.filePath);
            this.yaml = new YamlConfiguration();
            this.server = ((CraftServer) server).getServer();

            this.yaml.load(this.file);
        }

        @Override
        public @NotNull @UnmodifiableView Map<String, HistoryEntry> getHistory() {
            return Collections.unmodifiableMap(this.history);
        }

        @Override
        public @NotNull String getHistoryAsString() {
            return Joiner.on(", ").join(this.history.entrySet());
        }

        @Override
        public @NotNull EditResponse set(
                final @NotNull String path,
                final @NotNull Object value
        ) {
            final var currentValue = this.yaml.get(path);

            if (
                    currentValue == null
                    || !currentValue.equals(value)
            ) {
                this.history.put(
                        path,
                        new HistoryEntry(currentValue, value)
                );
                this.yaml.set(path, value);
            }

            return this;
        }

        @Override
        public @NotNull EditResponse setIfAbsent(
                final @NotNull String path,
                final @NotNull Object value
        ) {
            return this.yaml.isSet(path)
                    ? this
                    : this.set(path, value);
        }

        @Override
        public boolean save() {
            if (this.history.isEmpty()) {
                return true;
            }

            final String history = this.getHistoryAsString();

            try {
                this.yaml.save(this.file);
                this.history.clear();
                this.server.paperConfigurations.reloadConfigs(this.server);
                this.server.server.reloadCount++;

                MSLogger.info("Saved " + this.file.getName() + " with : [" + history + ']');

                return true;
            } catch (final IOException e) {
                MSLogger.severe(
                        "Failed to save " + this.file.getName() + " with : [" + history + ']',
                        e
                );
                return false;
            }
        }

        @Override
        public @NotNull String toString() {
            return "EditResponse{" +
                    "server=" + this.server +
                    ", file=" + this.file +
                    ", yaml=" + this.yaml +
                    ", editHistory=" + this.getHistoryAsString() +
                    '}';
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    public enum ConfigType {
        GLOBAL(SharedConstants.PAPER_GLOBAL_CONFIG_PATH),
        WORLD_DEFAULTS(SharedConstants.PAPER_WORLD_DEFAULTS_PATH);

        private final String filePath;

        ConfigType(final @NotNull String filePath) {
            this.filePath = filePath;
        }

        /**
         * @return The file path to the config file
         */
        public @NotNull String getFilePath() {
            return this.filePath;
        }

        /**
         * @return A string representation of this config type. The string
         *         representation consists of the name and the file path to the
         *         config file.
         */
        @Override
        public @NotNull String toString() {
            return this.name() + '{' +
                    "filePath=\"" + this.filePath + '"' +
                    '}';
        }
    }

    @Immutable
    public static final class HistoryEntry {
        private final Object oldValue;
        private final Object newValue;

        /**
         * Creates a new history entry with the given old and new value
         *
         * @param oldValue The old value of the config entry
         * @param newValue The new value of the config entry
         */
        public HistoryEntry(
                final @Nullable Object oldValue,
                final @Nullable Object newValue
        ) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        /**
         * @return The old value of the config entry
         */
        public @Nullable Object getOldValue() {
            return this.oldValue;
        }

        /**
         * @return The new value of the config entry
         */
        public @Nullable Object getNewValue() {
            return this.newValue;
        }

        /**
         * @return A string representation of this history entry. The string
         *         representation consists of the old and new value.
         */
        @Override
        public @NotNull String toString() {
            return '{' +
                    "old=\"" + this.oldValue + '"' +
                    ", new=\"" + this.newValue + '"' +
                    '}';
        }
    }
}
