package com.github.minersstudios.msessentials.player;

import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.config.Config;
import com.github.minersstudios.msessentials.menu.ResourcePackMenu;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;

/**
 * Resource pack file loader with hash and url.
 * It is used to load resource packs from GitHub releases.
 * If the resource pack is not loaded or a new version is detected,
 * it will be loaded with the url and the hash will be generated,
 * then the new settings of the resource pack will be saved in config
 * and the file will be deleted.
 * All settings stored in the "config/minersstudios/MSEssentials/config.yml" file.
 */
public class ResourcePack {
    private final String hash;
    private final String url;
    private static String user;
    private static String repo;
    public static String tagName;

    public ResourcePack(
            @NotNull String hash,
            @NotNull String url
    ) {
        this.hash = hash;
        this.url = url;
    }

    public static void init() {
        Config config = MSEssentials.getConfiguration();
        user = config.user;
        repo = config.repo;
        var latestTagName = getLatestTagName();
        tagName = latestTagName.getValue();
        boolean hasNoUpdates =
                tagName.equals(config.version)
                && latestTagName.getKey()
                && config.fullHash != null
                && config.liteHash != null;

        String fullFileName = String.format(config.fullFileName, tagName);
        String liteFileName = String.format(config.liteFileName, tagName);

        String fullUrl = "https://github.com/" + user + "/" + repo + "/releases/download/" + tagName + "/" + fullFileName;
        String liteUrl = "https://github.com/" + user + "/" + repo + "/releases/download/" + tagName + "/" + liteFileName;

        String fullHash = hasNoUpdates
                ? config.fullHash
                : generateHash(fullUrl, fullFileName);
        String liteHash = hasNoUpdates
                ? config.liteHash
                : generateHash(liteUrl, liteFileName);

        Type.FULL.resourcePack = new ResourcePack(fullHash, fullUrl);
        Type.LITE.resourcePack = new ResourcePack(liteHash, liteUrl);

        if (!hasNoUpdates) {
            YamlConfiguration configYaml = YamlConfiguration.loadConfiguration(config.getFile());

            configYaml.set("resource-pack.version", tagName);
            configYaml.set("resource-pack.full.hash", fullHash);
            configYaml.set("resource-pack.lite.hash", liteHash);

            deleteResourcePackFiles(config.fullFileName);
            deleteResourcePackFiles(config.liteFileName);

            config.save(configYaml);
        }
    }

    private static void deleteResourcePackFiles(@NotNull String fileName) {
        File[] files = MSEssentials.getInstance().getPluginFolder().listFiles();

        if (files != null) {
            Arrays.stream(files)
            .filter(file -> file.getName().matches(String.format(fileName, ".*")))
            .forEach(file -> {
                if (!file.delete()) {
                    throw new SecurityException("File deletion failed: " + file.getAbsolutePath());
                }
            });
        }
    }

    @Contract("_, _ -> new")
    private static @NotNull String generateHash(
            @NotNull String url,
            @NotNull String fileName
    ) throws RuntimeException {
        File pluginFolder = MSEssentials.getInstance().getPluginFolder();

        try (
                var byteChannel = Channels.newChannel(new URL(url).openStream());
                var output = new FileOutputStream(pluginFolder + "/" + fileName)
        ) {
            output.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
            return bytesToHexString(createSha1(new File(pluginFolder, fileName)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate resource pack hash", e);
        }
    }

    private static byte @NotNull [] createSha1(@NotNull File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            return digest.digest();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create SHA-1 hash", e);
        }
    }

    private static @NotNull String bytesToHexString(byte @NotNull [] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            int value = b & 0xFF;

            if (value < 16) {
                stringBuilder.append("0");
            }

            stringBuilder.append(Integer.toHexString(value));
        }
        return stringBuilder.toString();
    }

    private static @NotNull Map.Entry<Boolean, String> getLatestTagName() throws NullPointerException {
        URI uri = URI.create("https://api.github.com/repos/" + user + "/" + repo + "/tags");

        try {
            String json = IOUtils.toString(uri, StandardCharsets.UTF_8);
            JsonArray tags = JsonParser.parseString(json).getAsJsonArray();
            JsonObject latestTag = tags.get(0).getAsJsonObject();

            return Map.entry(true, latestTag.get("name").getAsString());
        } catch (IOException e) {
            String configTagName = MSEssentials.getConfiguration().version;

            if (configTagName == null) {
                throw new NullPointerException("Apparently the API rate limit has been exceeded\nRequest URL : " + uri);
            } else {
                Bukkit.getLogger().log(Level.SEVERE, "Apparently the API rate limit has been exceeded. Plugin will use existing version\nRequest URL : " + uri, e);
                return Map.entry(false, configTagName);
            }
        }
    }

    /**
     * Sets resource pack for player
     *
     * @param playerInfo player info
     */
    public static void setResourcePack(@NotNull PlayerInfo playerInfo) {
        if (playerInfo.getOnlinePlayer() == null) return;

        Player player = playerInfo.getOnlinePlayer();
        PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();
        ResourcePack.Type type = playerSettings.getResourcePackType();

        if (
                Type.FULL.getUrl() == null
                || Type.FULL.getHash() == null
                || Type.LITE.getUrl() == null
                || Type.LITE.getHash() == null
        ) {
            playerInfo.kickPlayer(
                    Component.translatable("ms.server_not_fully_loaded.title"),
                    Component.translatable("ms.server_not_fully_loaded.subtitle")
            );
            return;
        }

        assert type.getUrl() != null;
        assert type.getHash() != null;

        if (type == ResourcePack.Type.NULL) {
            ResourcePackMenu.open(player);
        } else {
            player.setResourcePack(type.getUrl(), type.getHash());
        }
    }

    public enum Type {
        FULL, LITE, NONE, NULL;

        private @Nullable ResourcePack resourcePack;

        Type() {
            this.resourcePack = null;
        }

        public @Nullable String getHash() {
            return this.resourcePack == null ? null : this.resourcePack.hash;
        }

        public @Nullable String getUrl() {
            return this.resourcePack == null ? null : this.resourcePack.url;
        }
    }
}
