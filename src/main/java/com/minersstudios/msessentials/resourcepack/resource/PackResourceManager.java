package com.minersstudios.msessentials.resourcepack.resource;

import com.minersstudios.mscore.resource.ResourceManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a resource manager for a resource pack.
 * <br>
 * It provides methods to get the URI and the hash of the resource pack file.
 * <br>
 * There are two types of resource managers :
 * <ul>
 *     <li>{@link URIPackResourceManager}</li>
 *     <li>{@link GitHubPackResourceManager}</li>
 * </ul>
 *
 * You can create them using the static methods of this class :
 * <ul>
 *     <li>{@link #uri(URI)}</li>
 *     <li>{@link #url(String)}</li>
 *     <li>{@link #github(File, String, String)}</li>
 *     <li>{@link #github(File, String, String, String, String)}</li>
 * </ul>
 */
public interface PackResourceManager extends ResourceManager {
    String SHA1 = "SHA-1";

    /**
     * Returns the URI of the resource pack file.
     * <br>
     * Method to get URI according to resource manager type :
     * <ul>
     *     <li>{@link URIPackResourceManager} : Returns the URI passed in the
     *                                          constructor</li>
     *     <li>{@link GitHubPackResourceManager} : Generates a new URI from the
     *                                             user, repo, latest tag and
     *                                             file name fields</li>
     * </ul>
     *
     * @return The future containing the URI of the resource pack file
     * @throws IllegalStateException If the manager is a GitHub resource manager
     *                               and the repository has no tags
     */
    @NotNull CompletableFuture<URI> getUri() throws IllegalStateException;

    /**
     * Returns the hash of the resource pack file using {@link #SHA1} algorithm.
     * <br>
     * Method to generate the hash according to resource manager type :
     * <ul>
     *     <li>{@link URIPackResourceManager} :    Generates the hash of the
     *                                             file pointed by the URI</li>
     *     <li>{@link GitHubPackResourceManager} : Downloads the resource pack
     *                                             file from the latest release
     *                                             and generates the hash of the
     *                                             downloaded file</li>
     * </ul>
     *
     * @return The future containing the hash of the resource pack file
     * @throws IllegalStateException If the repository has no tags, or if an
     *                               error occurs while downloading/generating
     *                               the hash of the resource pack file
     */
    @NotNull CompletableFuture<String> generateHash() throws IllegalStateException;

    /**
     * Creates a new resource manager for the given url
     *
     * @param url The url of the resource pack file
     * @return The resource manager for the given url
     * @throws IllegalArgumentException If the given string violates
     *                                  RFC&nbsp;2396
     * @see #uri(URI)
     */
    @Contract("_ -> new")
    static @NotNull URIPackResourceManager url(final @NotNull String url) throws IllegalArgumentException {
        return uri(URI.create(url));
    }

    /**
     * Creates a new resource manager for the given URI
     *
     * @param uri The URI of the resource pack file
     * @return The resource manager for the given URI
     */
    @Contract("_ -> new")
    static @NotNull URIPackResourceManager uri(final @NotNull URI uri) {
        return new URIPackResourceManager(uri);
    }

    /**
     * Creates a new resource manager for the given GitHub repository
     *
     * @param file The file to download the resource pack file to
     * @param user The GitHub user
     * @param repo The GitHub repository
     * @return The resource manager for the given GitHub repository
     */
    @Contract("_, _, _ -> new")
    static @NotNull GitHubPackResourceManager github(
            final @NotNull File file,
            final @NotNull String user,
            final @NotNull String repo
    ) {
        return github(file, user, repo, null, null);
    }

    /**
     * Creates a new resource manager for the given GitHub repository
     *
     * @param file       The file to download the resource pack file to
     * @param user       The GitHub user
     * @param repo       The GitHub repository
     * @param currentTag The current saved tag of the resource pack file
     * @param token      The GitHub token
     * @return The resource manager for the given GitHub repository
     */
    @Contract("_, _, _, _, _ -> new")
    static @NotNull GitHubPackResourceManager github(
            final @NotNull File file,
            final @NotNull String user,
            final @NotNull String repo,
            final @Nullable String currentTag,
            final @Nullable String token
    ) {
        return new GitHubPackResourceManager(file, user, repo, currentTag, token);
    }

    /**
     * Generates the SHA-1 hash of the given input stream
     *
     * @param inputStream The input stream
     * @return The SHA-1 hash of the given input stream
     * @throws NoSuchAlgorithmException If the SHA-1 algorithm is not available
     * @throws IOException              If an I/O error occurs
     */
    @Contract("_ -> new")
    static @NotNull String generateSHA1(final @NotNull InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        try (inputStream) {
            final MessageDigest digest = MessageDigest.getInstance(SHA1);
            final byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            final StringBuilder stringBuilder = new StringBuilder();

            for (final byte b : digest.digest()) {
                final int value = b & 0xFF;

                if (value < 16) {
                    stringBuilder.append('0');
                }

                stringBuilder.append(Integer.toHexString(value));
            }

            return stringBuilder.toString();
        }
    }
}
