package com.minersstudios.mscore.resource.github;

import com.minersstudios.mscore.resource.file.FileResourceManager;
import com.minersstudios.mscore.resource.uri.URIResourceManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public interface GithubResourceManager extends FileResourceManager, URIResourceManager {
    /**
     * The URL to get the tags of a GitHub repository.
     * <br>
     * Arguments:
     * <ol>
     *     <li>GitHub user</li>
     *     <li>GitHub repository</li>
     * </ol>
     */
    String TAGS_URL_FORMAT = "https://api.github.com/repos/%s/%s/tags";

    /**
     * Returns the GitHub user
     *
     * @return The GitHub user
     */
    @NotNull String getUser();

    /**
     * Returns the GitHub repository
     *
     * @return The GitHub repository
     */
    @NotNull String getRepo();

    /**
     * Returns a future containing the latest tag of the repository.
     * <br>
     * Returns failed future with {@link IllegalStateException}, if the
     * repository has no tags.
     *
     * @return A future containing the latest tag of the repository
     */
    @NotNull CompletableFuture<Tag> getLatestTag();

    /**
     * Returns future and sets the tag array of the repository
     *
     * @return A future containing the tag array of the repository
     */
    @NotNull CompletableFuture<Tag[]> getTags();

    /**
     * Returns the latest tag of the repository
     *
     * @return The latest tag of the repository
     */
    @Nullable Tag getLatestTagNow();

    /**
     * Returns the tag array of the repository
     *
     * @return The tag array of the repository
     */
    Tag @Nullable [] getTagsNow();

    /**
     * Generates and returns a new URI from the user, repo, latest tag and file
     * name fields.
     * <br>
     * Returns failed future with {@link IllegalStateException}, if the latest
     * tag has no name, or if the repository has no tags.
     *
     * @return The future containing the URI of the resource pack file
     */
    @Override
    @NotNull CompletableFuture<URI> getUri();

    /**
     * Returns the repository tags URI
     *
     * @return The repository tags URI
     */
    @Contract(" -> new")
    @NotNull URI getTagsUri();

    /**
     * Returns the URI of the file for the given tag
     *
     * @param tag The tag name
     * @return The URI of the file
     */
    @Contract("_ -> new")
    @NotNull URI getFileUri(final @NotNull String tag);

    /**
     * Updates and returns the future containing the file
     *
     * @param forceUpdate If the file should be updated even if the current tag
     *                    is the latest
     * @return The future containing the file
     */
    @NotNull CompletableFuture<File> updateFile(final boolean forceUpdate);
}
