package com.minersstudios.mscore.resource.file;

import com.minersstudios.mscore.resource.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface FileResourceManager extends ResourceManager {

    /**
     * Returns the file of the resource
     *
     * @return The file of the resource
     */
    @NotNull File getFile();
}
