package com.github.minersstudios.mscore;

import com.github.minersstudios.mscore.config.Config;
import com.github.minersstudios.mscore.config.LanguageFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class MSCore extends MSPlugin {
    private static MSCore instance;
    private static Cache cache;

    @Override
    public void enable() {
        instance = this;
        cache = new Cache();
        Config config = cache.config;

        LanguageFile.loadLanguage(config.languageUrl, config.languageCode);
    }

    @Override
    public void disable() {
        LanguageFile.unloadLanguage();
    }

    @Contract(pure = true)
    public static @NotNull Cache getCache() {
        return cache;
    }

    @Contract(pure = true)
    public static @NotNull Config getConfiguration() {
        return cache.config;
    }

    @Contract(pure = true)
    public static @NotNull MSCore getInstance() {
        return instance;
    }
}
