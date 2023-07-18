package com.github.minersstudios.msessentials.player;

import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.msessentials.Config;
import com.github.minersstudios.msessentials.MSEssentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
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
    private final String url;
    private final String hash;

    private ResourcePack(
            @NotNull String hash,
            @NotNull String url
    ) {
        this.url = url;
        this.hash = hash;
    }

    /**
     * Initializes the resource pack's parameters
     *
     * @see ResourcePack
     */
    public static void init() {
        Config config = MSEssentials.getConfiguration();
        String user = config.user;
        String repo = config.repo;
        String tagName = getLatestTagName(config.user, config.repo).orElse(config.version);

        if (StringUtils.isBlank(tagName)) {
            MSLogger.severe("""
                    Apparently the API rate limit has been exceeded and the tag name could not be obtained.
                    The players will not be able to connect to the server.
                    Please try again later or generate resource pack parameters manually."""
            );

            Type.FULL.resourcePack = null;
            Type.LITE.resourcePack = null;

            return;
        }

        boolean upToDate = config.version != null && config.version.equals(tagName) && config.fullHash != null && config.liteHash != null;

        String fullFileName = String.format(config.fullFileName, tagName);
        String liteFileName = String.format(config.liteFileName, tagName);

        String fullUrl = "https://github.com/" + user + "/" + repo + "/releases/download/" + tagName + "/" + fullFileName;
        String liteUrl = "https://github.com/" + user + "/" + repo + "/releases/download/" + tagName + "/" + liteFileName;

        String fullHash = upToDate
                ? config.fullHash
                : generateHash(fullUrl, fullFileName);
        String liteHash = upToDate
                ? config.liteHash
                : generateHash(liteUrl, liteFileName);

        Type.FULL.resourcePack = new ResourcePack(fullHash, fullUrl);
        Type.LITE.resourcePack = new ResourcePack(liteHash, liteUrl);

        if (!upToDate) {
            YamlConfiguration configYaml = config.getConfig();

            configYaml.set("resource-pack.version", tagName);
            configYaml.set("resource-pack.full.hash", fullHash);
            configYaml.set("resource-pack.lite.hash", liteHash);

            config.save();
        }
    }

    /**
     * @return True if the resource pack is loaded
     */
    public static boolean isResourcePackLoaded() {
        return Type.FULL.resourcePack != null
                && Type.LITE.resourcePack != null
                && Type.FULL.getUrl() != null
                && Type.FULL.getHash() != null
                && Type.LITE.getUrl() != null
                && Type.LITE.getHash() != null;
    }

    /**
     * Downloads the resource pack file
     * and generates a hash with the SHA-1 algorithm
     *
     * @param link     The link to the file,
     *                 from which the resource pack will be downloaded
     * @param fileName The name of the file,
     *                 from which the hash will be generated
     * @return The hash of the file
     * @see #createSHA1(Path)
     * @see #bytesToHexString(byte[])
     */
    private static @NotNull String generateHash(
            @NotNull String link,
            @NotNull String fileName
    ) {
        URI uri = URI.create(link);
        Path path = MSEssentials.getInstance().getPluginFolder().toPath().resolve(fileName);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder(uri).build();
        var bodyHandler = HttpResponse.BodyHandlers.ofFile(path);

        client.sendAsync(request, bodyHandler)
        .thenAccept(response -> {
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to download resource pack. Response code: " + response.statusCode());
            }
        }).join();

        String hash = bytesToHexString(createSHA1(path));

        if (path.toFile().delete()) {
            MSLogger.log(Level.INFO, "File deleted: " + path);
        } else {
            MSLogger.log(Level.WARNING, "Failed to delete file: " + path);
        }

        return hash;
    }

    /**
     * Generates a hash from a file using the SHA-1 algorithm
     *
     * @param path The path to the file
     * @return The hash of the file
     */
    private static byte @NotNull [] createSHA1(@NotNull Path path) {
        try (var in = Files.newInputStream(path)) {
            MessageDigest digest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_1);
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            return digest.digest();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create SHA-1 hash", e);
        }
    }

    /**
     * Converts bytes to hex string
     *
     * @param bytes The bytes to convert to hex string
     * @return The hex string of the bytes
     */
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

    /**
     * @param user The name of the user who owns the repository
     * @param repo The name of the repository
     * @return The latest tag name
     */
    private static @NotNull @Unmodifiable Optional<String> getLatestTagName(
            @NotNull String user,
            @NotNull String repo
    ) {
        URI uri = URI.create("https://api.github.com/repos/" + user + "/" + repo + "/tags");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        var bodyHandler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        try {
            var response = client.send(request, bodyHandler);

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                MSLogger.severe("Failed to get latest tag. Response code : " + response.statusCode() + ". Trying to get the latest tag from the config...");
                return Optional.empty();
            }

            String json = response.body();
            JsonArray tags = JsonParser.parseString(json).getAsJsonArray();

            if (tags.isEmpty()) {
                MSLogger.severe("No tags found in the repository. Trying to get the latest tag from the config...");
                return Optional.empty();
            }

            JsonObject latestTag = tags.get(0).getAsJsonObject();

            return Optional.of(latestTag.get("name").getAsString());
        } catch (InterruptedException | IOException e) {
            MSLogger.log(Level.WARNING, "Failed to get latest tag. Trying to get the latest tag from the config...", e);
            return Optional.empty();
        }
    }

    /**
     * The type of resource pack.
     * <ul>
     *     <li>{@link Type#FULL} - Full resource pack</li>
     *     <li>{@link Type#LITE} - Lite resource pack</li>
     *     <li>{@link Type#NONE} - No resource pack, need to kick a player if they already have a resource pack loaded</li>
     *     <li>{@link Type#NULL} - Null resource pack, used for null check</li>
     * </ul>
     */
    public enum Type {
        FULL, LITE, NONE, NULL;

        private ResourcePack resourcePack;

        Type() {
            this.resourcePack = null;
        }

        /**
         * @return The hash of the resource pack
         * @throws NullPointerException If the resource pack is null
         *                              use {@link #isResourcePackLoaded()} to check
         *                              if the resource pack is loaded
         */
        public String getHash() {
            return this.resourcePack.hash;
        }

        /**
         * @return The url of the resource pack
         * @throws NullPointerException If the resource pack is null
         *                              use {@link #isResourcePackLoaded()} to check
         *                              if the resource pack is loaded
         */
        public String getUrl() {
            return this.resourcePack.url;
        }
    }
}
