package com.minersstudios.mscore.locale.resource;

import com.minersstudios.mscore.resource.ResourceManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;

public interface TranslationResourceManager extends ResourceManager {

    /**
     * Creates a new resource manager for the given url
     *
     * @param url The url of the translation file
     * @return The resource manager for the given url
     * @throws IllegalArgumentException If the given string violates
     *                                  RFC&nbsp;2396
     * @see #uri(URI)
     */
    @Contract("_ -> new")
    static @NotNull URITranslationResourceManager url(final @NotNull String url) throws IllegalArgumentException {
        return uri(URI.create(url));
    }

    /**
     * Creates a new resource manager for the given URI
     *
     * @param uri The URI of the translation file
     * @return The resource manager for the given URI
     */
    @Contract("_ -> new")
    static @NotNull URITranslationResourceManager uri(final @NotNull URI uri) {
        return new URITranslationResourceManager(uri);
    }

    /**
     * Creates a new resource manager for the given file
     *
     * @param file The file of the translation file
     * @return The resource manager for the given file
     */
    @Contract("_ -> new")
    static @NotNull FileTranslationResourceManager file(final @NotNull File file) {
        return new FileTranslationResourceManager(file);
    }

    /**
     * Creates a new resource manager for the given GitHub repository
     *
     * @param file The file to download translation file to
     * @param user The GitHub user
     * @param repo The GitHub repository
     * @return The resource manager for the given GitHub repository
     */
    @Contract("_, _, _ -> new")
    static @NotNull GitHubTranslationResourceManager github(
            final @NotNull File file,
            final @NotNull String user,
            final @NotNull String repo
    ) {
        return github(file, user, repo, null, null, null);
    }

    /**
     * Creates a new resource manager for the given GitHub repository
     *
     * @param file       The file to download translation file to
     * @param user       The GitHub user
     * @param repo       The GitHub repository
     * @param folderPath The path of the translation folder
     * @return The resource manager for the given GitHub repository
     */
    @Contract("_, _, _, _ -> new")
    static @NotNull GitHubTranslationResourceManager github(
            final @NotNull File file,
            final @NotNull String user,
            final @NotNull String repo,
            final @Nullable String folderPath
    ) {
        return github(file, user, repo, null, null, folderPath);
    }

    /**
     * Creates a new resource manager for the given GitHub repository
     *
     * @param file       The file to download the translation file to
     * @param user       The GitHub user
     * @param repo       The GitHub repository
     * @param currentTag The current saved tag of the translation file
     * @param token      The GitHub token
     * @param folderPath The path of the translation folder
     * @return The resource manager for the given GitHub repository
     */
    @Contract("_, _, _, _, _, _ -> new")
    static @NotNull GitHubTranslationResourceManager github(
            final @NotNull File file,
            final @NotNull String user,
            final @NotNull String repo,
            final @Nullable String currentTag,
            final @Nullable String token,
            final @Nullable String folderPath
    ) {
        return new GitHubTranslationResourceManager(file, user, repo, currentTag, token, folderPath);
    }
}
