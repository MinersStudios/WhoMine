package com.minersstudios.mscore.resource.file;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class AbstractFileResourceManager implements FileResourceManager {
    private final File file;

    protected AbstractFileResourceManager(final @NotNull File file) {
        this.file = file;
    }

    @Override
    public @NotNull File getFile() {
        return this.file;
    }

    @Override
    public @NotNull FileInputStream openStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{file=" + this.file + '}';
    }
}
