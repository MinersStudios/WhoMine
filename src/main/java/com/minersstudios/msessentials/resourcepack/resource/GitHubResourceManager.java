package com.minersstudios.msessentials.resourcepack.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minersstudios.mscore.utility.ChatUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a GitHub resource manager for a resource pack.
 * <br>
 * It provides methods to get the URI, the hash of the resource pack file, the
 * tags of the repository and the latest tag of the repository. Also, this class
 * can automatically download the resource pack file from the latest release if
 * the current tag is not the latest.
 * <br>
 * You can create it using the static methods of {@link ResourceManager} :
 * <ul>
 *     <li>{@link ResourceManager#github(File, String, String)}</li>
 *     <li>{@link ResourceManager#github(File, String, String, String, String)}</li>
 * </ul>
 *
 * @see ResourceManager
 */
public final class GitHubResourceManager implements ResourceManager {
    private final File file;
    private final String user;
    private final String repo;
    private final String currentTag;
    private final AtomicReference<Tag[]> tags;
    private transient final String token;

    //<editor-fold desc="GitHub API Constants" defaultstate="collapsed">
    /**
     * The URL to get the tags of a GitHub repository.
     * <br>
     * Arguments:
     * <ol>
     *     <li>GitHub user</li>
     *     <li>GitHub repository</li>
     * </ol>
     */
    public static final String TAGS_URL = "https://api.github.com/repos/%s/%s/tags";

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
    //</editor-fold>

    private static final Gson GSON =
            new GsonBuilder()
            .setPrettyPrinting()
            .create();

    GitHubResourceManager(
            final @NotNull File file,
            final @NotNull String user,
            final @NotNull String repo,
            final @Nullable String currentTag,
            final @Nullable String token
    ) {
        this.file = file;
        this.user = user;
        this.repo = repo;
        this.currentTag = currentTag;
        this.tags = new AtomicReference<>(null);
        this.token = token;
    }

    /**
     * Returns the file to download the resource pack file to
     *
     * @return The file to download the resource pack file to
     */
    public @NotNull File getFile() {
        return this.file;
    }

    /**
     * Returns the GitHub user
     *
     * @return The GitHub user
     */
    public @NotNull String getUser() {
        return this.user;
    }

    /**
     * Returns the GitHub repository
     *
     * @return The GitHub repository
     */
    public @NotNull String getRepo() {
        return this.repo;
    }

    /**
     * Returns the latest tag of the repository
     *
     * @return The latest tag of the repository
     */
    public @Nullable Tag getLatestTagNow() {
        final Tag[] tags = this.tags.get();

        return tags != null
                && tags.length != 0
                ? tags[0]
                : null;
    }

    /**
     * Returns a future containing the latest tag of the repository
     *
     * @return A future containing the latest tag of the repository
     * @throws IllegalStateException If the repository has no tags
     */
    public @NotNull CompletableFuture<Tag> getLatestTag() throws IllegalStateException {
        return this.getTags()
                .thenApplyAsync(tags -> {
                    if (tags.length == 0) {
                        throw new IllegalStateException("Provided repository has no tags");
                    }

                    return tags[0];
                });
    }

    /**
     * Returns the tag array of the repository
     *
     * @return The tag array of the repository
     */
    public Tag @Nullable [] getTagsNow() {
        return this.tags.get();
    }

    /**
     * Returns future and sets the tag array of the repository
     *
     * @return A future containing the tag array of the repository
     */
    public @NotNull CompletableFuture<Tag[]> getTags() {
        final Tag[] tags = this.tags.get();

        return tags != null
                && tags.length != 0
                ? CompletableFuture.completedFuture(tags)
                : this.updateTags();
    }

    /**
     * Generates and returns a new URI from the user, repo, latest tag and file
     * name fields
     *
     * @return The future containing the URI of the resource pack file
     * @throws IllegalStateException If the latest tag has no name, or if the
     *                               repository has no tags
     */
    @Override
    public @NotNull CompletableFuture<URI> getUri() throws IllegalStateException {
        return this.getLatestTag()
                .thenApplyAsync(tag -> {
                    final String tagName = tag.getName();

                    if (ChatUtils.isBlank(tagName)) {
                        throw new IllegalStateException("For some reason, the latest tag has no name");
                    }

                    return this.getReleaseUri(tagName);
                });
    }

    /**
     * Returns the URI of the resource pack file for the given tag
     *
     * @param tag The tag name
     * @return The URI of the resource pack file
     */
    @Contract("_ -> new")
    public @NotNull URI getReleaseUri(final @NotNull String tag) {
        return URI.create(
                DOWNLOAD_RELEASE_URL.formatted(this.user, this.repo, tag, this.getFile().getName())
        );
    }

    /**
     * Returns the repository tags URI
     *
     * @return The repository tags URI
     */
    @Contract(" -> new")
    public @NotNull URI getTagsUri() {
        return URI.create(
                TAGS_URL.formatted(this.user, this.repo)
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
        return this.getLatestTag()
                .thenCompose(
                        tag -> {
                            final var pathFuture =
                                    tag.getName().equals(this.currentTag) && this.file.exists()
                                    ? CompletableFuture.completedFuture(this.file.toPath())
                                    : this.updateFile();

                            return pathFuture
                                    .thenApply(path -> {
                                        try {
                                            return ResourceManager.generateSHA1(
                                                    Files.newInputStream(path)
                                            );
                                        } catch (final Throwable e) {
                                            throw new IllegalStateException("Failed to generate SHA-1 hash for resource pack file: " + this.file, e);
                                        }
                                    });
                        }
                );
    }

    @Override
    public @NotNull String toString() {
        return "GitHubResourceManager{" +
                "file=" + this.getFile() +
                ", user=" + this.user +
                ", repo=" + this.repo +
                ", tags=" + Arrays.toString(this.getTagsNow()) +
                '}';
    }

    private @NotNull CompletableFuture<Path> updateFile() throws IllegalStateException {
        return this.getUri()
                .thenApplyAsync(
                        uri -> {
                            final Path path = this.file.toPath();
                            final HttpClient client =
                                    HttpClient.newBuilder()
                                    .followRedirects(HttpClient.Redirect.ALWAYS)
                                    .build();

                            try {
                                final int statusCode =
                                        client.send(
                                                HttpRequest.newBuilder(uri).build(),
                                                HttpResponse.BodyHandlers.ofFile(path)
                                        ).statusCode();

                                if (statusCode != HttpURLConnection.HTTP_OK) {
                                    throw new IllegalStateException(
                                            "Failed to update resource pack file: " + this.file + " (status code: " + statusCode + ')'
                                    );
                                }
                            } catch (final IOException | InterruptedException e) {
                                throw new IllegalStateException("Failed to update resource pack file: " + this.file, e);
                            }

                            return path;
                        }
                );
    }

    private @NotNull CompletableFuture<Tag[]> updateTags() {
        return CompletableFuture.supplyAsync(() -> {
            final HttpRequest.Builder builder = HttpRequest.newBuilder(this.getTagsUri()).GET();
            final HttpResponse<String> response;

            if (ChatUtils.isNotBlank(this.token)) {
                builder.setHeader("Authorization", "Bearer " + this.token);
            }

            try {
                response = HttpClient.newHttpClient().send(
                        builder.build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );
            } catch (final IOException | InterruptedException e) {
                throw new IllegalStateException("Failed to get latest tag for " + this.user + '/' + this.repo, e);
            }

            final int statusCode = response.statusCode();

            switch (statusCode) {
                case HttpURLConnection.HTTP_OK -> {
                    final Tag[] newTags = GSON.fromJson(response.body(), Tag[].class);

                    this.tags.set(newTags);

                    return newTags;
                }
                case HttpURLConnection.HTTP_FORBIDDEN -> throw new IllegalStateException("GitHub API rate limit exceeded");
                case HttpURLConnection.HTTP_NOT_FOUND -> throw new IllegalStateException("GitHub repository not found");
                default -> throw new IllegalStateException(
                        "Failed to get latest tag for " + this.user + '/' + this.repo +
                        "(Status code: " + statusCode + ')'
                );
            }
        });
    }
}
