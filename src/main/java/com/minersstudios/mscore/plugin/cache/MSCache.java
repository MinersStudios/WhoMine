package com.minersstudios.mscore.plugin.cache;

public abstract class MSCache {
    private boolean loaded;

    /**
     * Loads the cache
     *
     * @throws IllegalStateException If the cache is already loaded
     */
    public final void load() throws IllegalStateException {
        if (this.isLoaded()) {
            throw new IllegalStateException("Cache is already loaded");
        }

        this.loaded = true;
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

        this.loaded = false;
        this.onUnload();
    }

    /**
     * @return Whether the cache is loaded
     */
    public final boolean isLoaded() {
        return this.loaded;
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
