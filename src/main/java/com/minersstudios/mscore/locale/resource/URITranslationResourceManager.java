package com.minersstudios.mscore.locale.resource;

import com.minersstudios.mscore.resource.uri.AbstractURIResourceManager;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

public final class URITranslationResourceManager extends AbstractURIResourceManager implements TranslationResourceManager {

    URITranslationResourceManager(final @NotNull URI uri) {
        super(uri);
    }
}
