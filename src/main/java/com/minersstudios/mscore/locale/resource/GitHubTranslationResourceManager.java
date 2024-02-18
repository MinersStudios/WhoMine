package com.minersstudios.mscore.locale.resource;

import com.minersstudios.mscore.resource.github.AbstractGithubResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;

public final class GitHubTranslationResourceManager extends AbstractGithubResourceManager implements TranslationResourceManager {
    private final String folderPath;

    private static final String DOWNLOAD_TRANSLATION_URL = "https://raw.githubusercontent.com/%s/%s/%s/%s";

    GitHubTranslationResourceManager(
            final @NotNull File file,
            final @NotNull String user,
            final @NotNull String repo,
            final @Nullable String currentTag,
            final @Nullable String token,
            final @Nullable String folderPath
    ) {
        super(file, user, repo, currentTag, token);

        this.folderPath = folderPath;
    }


    @Override
    public @NotNull URI getFileUri(final @NotNull String tag) {
        return URI.create(
                DOWNLOAD_TRANSLATION_URL.formatted(
                        this.getUser(), this.getRepo(), tag,
                        this.folderPath == null
                        ? this.getFile().getName()
                        : this.folderPath + "/" + this.getFile().getName()
                )
        );
    }
}
