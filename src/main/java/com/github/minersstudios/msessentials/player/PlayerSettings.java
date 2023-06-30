package com.github.minersstudios.msessentials.player;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

/**
 * Player settings class, which contains all player custom settings.
 *
 * @see PlayerFile
 */
@SuppressWarnings("unused")
public class PlayerSettings {
    private final @NotNull PlayerFile playerFile;
    private final @NotNull YamlConfiguration config;

    private final @NotNull Parameter<ResourcePack.Type> resourcePackType;

    public PlayerSettings(
            @NotNull PlayerFile playerFile
    ) {
        this.playerFile = playerFile;
        this.config = playerFile.getYamlConfiguration();

        ResourcePack.Type resourcePackType = null;
        try {
            resourcePackType = ResourcePack.Type.valueOf(this.config.getString("resource-pack.resource-pack-type", "NULL"));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Incorrect resource-pack type in : " + playerFile.getFile().getName(), e);
        }
        this.resourcePackType = new Parameter<>("resource-pack.resource-pack-type", resourcePackType);
    }

    public void save() {
        this.playerFile.save();
    }

    public @NotNull Parameter<ResourcePack.Type> getResourcePackParam() {
        return this.resourcePackType;
    }

    public @NotNull ResourcePack.Type getResourcePackType() {
        return this.resourcePackType.getValue();
    }

    public void setResourcePackType(@Nullable ResourcePack.Type resourcePackType) {
        this.resourcePackType.setValue(resourcePackType);
        this.resourcePackType.setForYaml(
                this.config,
                resourcePackType == null
                        ? ResourcePack.Type.NULL
                        : resourcePackType.name()
        );
    }

    public static class Parameter<V> {
        protected final @NotNull String path;
        protected V value;

        public Parameter(
                @NotNull String path,
                V value
        ) {
            this.path = path;
            this.value = value;
        }

        public void setForYaml(@NotNull YamlConfiguration yamlConfiguration) {
            this.setForYaml(yamlConfiguration, this.value);
        }

        public void setForYaml(
                @NotNull YamlConfiguration yamlConfiguration,
                Object value
        ) {
            yamlConfiguration.set(this.path, value);
        }

        public void saveForFile(@NotNull PlayerFile playerFile) {
            this.setForYaml(playerFile.getYamlConfiguration());
            playerFile.save();
        }

        public void saveForFile(
                @NotNull PlayerFile playerFile,
                Object value
        ) {
            this.setForYaml(
                    playerFile.getYamlConfiguration(),
                    value
            );
            playerFile.save();
        }

        public @NotNull String getPath() {
            return this.path;
        }

        public V getValue() {
            return this.value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
