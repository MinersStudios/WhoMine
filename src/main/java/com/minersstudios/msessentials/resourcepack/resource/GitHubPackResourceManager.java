package com.minersstudios.msessentials.resourcepack.resource;

import com.minersstudios.mscore.resource.github.AbstractGithubResourceManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a GitHub resource manager for a resource pack.
 * <br>
 * It provides methods to get the URI, the hash of the resource pack file, the
 * tags of the repository and the latest tag of the repository. Also, this class
 * can automatically download the resource pack file from the latest release if
 * the current tag is not the latest.
 * <br>
 * You can create it using the static methods of {@link PackResourceManager} :
 * <ul>
 *     <li>{@link PackResourceManager#github(File, String, String)}</li>
 *     <li>{@link PackResourceManager#github(File, String, String, String, String)}</li>
 * </ul>
 *
 * @see PackResourceManager
 */
public final class GitHubPackResourceManager extends AbstractGithubResourceManager implements PackResourceManager {
    /**
     * The URL to download a release from a GitHub repository.
     * <br>
     * Arguments:
     * <ol>
     *     <li>GitHub user</li>
     *     <li>GitHub repository</li>
     *     <li>Tag name</li>
     *     <li>File name</li>
     * </ol>
     */
    public static final String DOWNLOAD_RELEASE_URL = "https://github.com/%s/%s/releases/download/%s/%s";

    GitHubPackResourceManager(
            final @NotNull File file,
            final @NotNull String user,
            final @NotNull String repo,
            final @Nullable String currentTag,
            final @Nullable String token
    ) {
        super(file, user, repo, currentTag, token);
    }

    @Contract("_ -> new")
    @Override
    public @NotNull URI getFileUri(final @NotNull String tag) {
        return URI.create(
                DOWNLOAD_RELEASE_URL.formatted(this.getUser(), this.getRepo(), tag, this.getFile().getName())
        );
    }

    /**
     * Downloads the resource pack file from the latest release, generates the
     * {@link #SHA1} hash of the downloaded file and returns it
     *
     * @return The future containing the hash of the resource pack file
     * @throws IllegalStateException If the repository has no tags, or if an
     *                               error occurs while downloading/generating
     *                               the hash of the resource pack file
     */
    @Override
    public @NotNull CompletableFuture<String> generateHash() throws IllegalStateException {
        return this.updateFile(false)
                   .thenApply(ignored -> {
                       try (final var in = this.openStream()) {
                           return PackResourceManager.generateSHA1(in);
                       } catch (final Throwable e) {
                           throw new IllegalStateException(
                                   "Failed to generate SHA-1 hash for resource pack file: " + this.getFile(),
                                   e
                           );
                       }
                   });
    }
}
