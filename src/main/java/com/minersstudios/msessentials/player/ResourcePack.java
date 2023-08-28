package com.minersstudios.msessentials.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.Config;
import com.minersstudios.msessentials.MSEssentials;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

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
            final @NotNull String url,
            final @NotNull String hash
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
        final Config config = MSEssentials.getConfiguration();
        final String user = config.user;
        final String repo = config.repo;
        final String tagName = getLatestTagName(config.user, config.repo).orElse(config.version);

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

        final String fullFileName = String.format(config.fullFileName, tagName);
        final String liteFileName = String.format(config.liteFileName, tagName);

        final String fullUrl = "https://github.com/" + user + "/" + repo + "/releases/download/" + tagName + "/" + fullFileName;
        final String liteUrl = "https://github.com/" + user + "/" + repo + "/releases/download/" + tagName + "/" + liteFileName;

        final String fullHash = upToDate
                ? config.fullHash
                : generateHash(fullUrl, fullFileName);
        final String liteHash = upToDate
                ? config.liteHash
                : generateHash(liteUrl, liteFileName);

        Type.FULL.resourcePack = new ResourcePack(fullUrl, fullHash);
        Type.LITE.resourcePack = new ResourcePack(liteUrl, liteHash);

        if (!upToDate) {
            YamlConfiguration configYaml = config.getYaml();

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
                && Type.FULL.getURL() != null
                && Type.FULL.getHash() != null
                && Type.LITE.getURL() != null
                && Type.LITE.getHash() != null;
    }

    /**
     * Downloads the resource pack file
     * and generates a hash with the SHA-1 algorithm
     *
     * @param url      The URL to the resource pack file,
     *                 from which the resource pack will be downloaded
     * @param fileName The name of the file,
     *                 from which the hash will be generated
     * @return The hash of the file
     * @see #createSHA1(Path)
     * @see #bytesToHexString(byte[])
     */
    private static @NotNull String generateHash(
            final @NotNull String url,
            final @NotNull String fileName
    ) {
        final URI uri = URI.create(url);
        final Path path = MSEssentials.getInstance().getPluginFolder().toPath().resolve(fileName);

        final HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        final HttpRequest request = HttpRequest.newBuilder(uri).build();
        final var bodyHandler = HttpResponse.BodyHandlers.ofFile(path);

        client.sendAsync(request, bodyHandler)
        .thenAccept(response -> {
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to download resource pack. Response code : " + response.statusCode());
            }
        }).join();

        final String hash = bytesToHexString(createSHA1(path));

        if (path.toFile().delete()) {
            MSLogger.info("File deleted: " + path);
        } else {
            MSLogger.warning("Failed to delete file: " + path);
        }

        return hash;
    }

    /**
     * Generates a hash from a file using the SHA-1 algorithm
     *
     * @param path The path to the file
     * @return The hash of the file
     */
    private static byte @NotNull [] createSHA1(final @NotNull Path path) {
        try (final var in = Files.newInputStream(path)) {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            final byte[] buffer = new byte[8192];
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
    private static @NotNull String bytesToHexString(final byte @NotNull [] bytes) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (final byte b : bytes) {
            final int value = b & 0xFF;

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
    private static @NotNull Optional<String> getLatestTagName(
            final @NotNull String user,
            final @NotNull String repo
    ) {
        final URI uri = URI.create("https://api.github.com/repos/" + user + "/" + repo + "/tags");
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        final var bodyHandler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        try {
            final var response = client.send(request, bodyHandler);

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                MSLogger.severe("Failed to get latest tag. Response code : " + response.statusCode() + ". Trying to get the latest tag from the config...");
                return Optional.empty();
            }

            final JsonArray tags = JsonParser.parseString(response.body()).getAsJsonArray();

            if (tags.isEmpty()) {
                MSLogger.severe("No tags found in the repository. Trying to get the latest tag from the config...");
                return Optional.empty();
            }

            final JsonObject latestTag = tags.get(0).getAsJsonObject();

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
         * @throws UnsupportedOperationException If the resource pack type is
         *                                       {@link Type#NULL} or {@link Type#NONE}
         */
        public String getHash() throws NullPointerException, UnsupportedOperationException {
            return switch (this) {
                case FULL, LITE -> this.resourcePack.hash;
                default -> throw new UnsupportedOperationException("Cannot get hash of " + this.name() + " resource pack");
            };
        }

        /**
         * @return The URL of the resource pack
         * @throws NullPointerException If the resource pack is null
         *                              use {@link #isResourcePackLoaded()} to check
         *                              if the resource pack is loaded
         * @throws UnsupportedOperationException If the resource pack type is
         *                                       {@link Type#NULL} or {@link Type#NONE}
         */
        public String getURL() throws NullPointerException, UnsupportedOperationException {
            return switch (this) {
                case FULL, LITE -> this.resourcePack.url;
                default -> throw new UnsupportedOperationException("Cannot get url of " + this.name() + " resource pack");
            };
        }
    }
}
