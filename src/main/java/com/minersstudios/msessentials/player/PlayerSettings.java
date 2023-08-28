package com.minersstudios.msessentials.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.player.skin.Skin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

/**
 * Player settings class, which contains all player custom settings.
 *
 * @see PlayerFile
 */
public class PlayerSettings {
    private final @NotNull PlayerFile playerFile;
    private final @NotNull YamlConfiguration config;

    private final @NotNull Parameter<ResourcePack.Type> resourcePackType;
    private final @NotNull Parameter<Skin> skin;

    public PlayerSettings(final @NotNull PlayerFile playerFile) {
        this.playerFile = playerFile;
        this.config = playerFile.getConfig();

        this.resourcePackType = new Parameter<>(
                "settings.resource-pack.resource-pack-type",
                switch (this.config.getString("settings.resource-pack.resource-pack-type", "NULL")) {
                    case "FULL" -> ResourcePack.Type.FULL;
                    case "LITE" -> ResourcePack.Type.LITE;
                    case "NONE" -> ResourcePack.Type.NONE;
                    case "NULL" -> ResourcePack.Type.NULL;
                    default -> {
                        MSLogger.log(Level.SEVERE, "Incorrect resource-pack type in : " + playerFile.getFile().getName());
                        yield ResourcePack.Type.NULL;
                    }
                }
        );
        this.skin = new Parameter<>("settings.skin", this.playerFile.getSkin(this.config.getString("settings.skin", "")));
    }

    public @NotNull Parameter<ResourcePack.Type> getResourcePackParam() {
        return this.resourcePackType;
    }

    public @NotNull ResourcePack.Type getResourcePackType() {
        return this.resourcePackType.getValue();
    }

    public void setResourcePackType(final @Nullable ResourcePack.Type resourcePackType) {
        this.resourcePackType.setValue(resourcePackType);
        this.resourcePackType.setForYaml(
                this.config,
                resourcePackType == null
                ? ResourcePack.Type.NULL
                : resourcePackType.name()
        );
    }

    public @NotNull Parameter<Skin> getSkinParam() {
        return this.skin;
    }

    public @Nullable Skin getSkin() {
        return this.skin.getValue();
    }

    public void setSkin(final @Nullable Skin skin) {
        this.skin.setValue(skin);
        this.skin.setForYaml(
                this.config,
                skin == null
                ? null
                : skin.getName()
        );
    }

    public void save() {
        this.playerFile.save();
    }

    public static class Parameter<V> {
        protected final @NotNull String path;
        protected V value;

        public Parameter(
                final @NotNull String path,
                final V value
        ) {
            this.path = path;
            this.value = value;
        }

        public void setForYaml(final @NotNull YamlConfiguration yamlConfiguration) {
            this.setForYaml(yamlConfiguration, this.value);
        }

        public void setForYaml(
                final @NotNull YamlConfiguration yamlConfiguration,
                final @Nullable Object value
        ) {
            yamlConfiguration.set(this.path, value);
        }

        public void saveForFile(final @NotNull PlayerFile playerFile) {
            this.setForYaml(playerFile.getConfig());
            playerFile.save();
        }

        public void saveForFile(
                final @NotNull PlayerFile playerFile,
                final Object value
        ) {
            this.setForYaml(
                    playerFile.getConfig(),
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

        public void setValue(final V value) {
            this.value = value;
        }
    }
}
