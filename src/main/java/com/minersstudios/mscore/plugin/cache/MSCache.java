package com.minersstudios.mscore.plugin.cache;

import org.jetbrains.annotations.NotNull;

public abstract class MSCache {
    private boolean isLoaded;

    /**
     * Loads the cache
     *
     * @throws IllegalStateException If the cache is already loaded
     */
    public final void load() throws IllegalStateException {
        if (this.isLoaded()) {
            throw new IllegalStateException("Cache is already loaded");
        }

        this.isLoaded = true;
        this.onLoad();
    }

    /**
     * Unloads the cache
     *
     * @throws IllegalStateException If the cache is not loaded
     */
    public final void unload() throws IllegalStateException {
        if (!this.isLoaded()) {
            throw new IllegalStateException("Cache is not loaded");
        }

        this.isLoaded = false;
        this.onUnload();
    }

    /**
     * Returns whether the cache is loaded
     *
     * @return Whether the cache is loaded
     */
    public final boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Returns a string representation of this cache
     *
     * @return A string representation of this cache
     */
    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{isLoaded=" + this.isLoaded + "}";
    }

    /**
     * This method is called when cache is loaded. Subclasses must implement
     * this method to specify what happens when the cache is loaded.
     */
    protected abstract void onLoad();

    /**
     * This method is called when cache is unloaded. Subclasses must implement
     * this method to specify what happens when the cache is unloaded.
     */
    protected abstract void onUnload();
}
