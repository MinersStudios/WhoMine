package com.minersstudios.msessentials.player;

import org.jetbrains.annotations.NotNull;

@Deprecated(forRemoval = true)
public final class ResourcePack {
    private final String url;
    private final String hash;

    private ResourcePack(
            final @NotNull String url,
            final @NotNull String hash
    ) {
        this.url = url;
        this.hash = hash;
    }

    @Deprecated(forRemoval = true)
    public enum Type {
        FULL, LITE, NONE, NULL;

        private final ResourcePack resourcePack;

        Type() {
            this.resourcePack = new ResourcePack("", "");
        }

        public String getHash() throws NullPointerException, UnsupportedOperationException {
            return switch (this) {
                case FULL, LITE -> this.resourcePack.hash;
                default -> throw new UnsupportedOperationException("Cannot get hash of " + this.name() + " resource pack");
            };
        }

        public String getURL() throws NullPointerException, UnsupportedOperationException {
            return switch (this) {
                case FULL, LITE -> this.resourcePack.url;
                default -> throw new UnsupportedOperationException("Cannot get url of " + this.name() + " resource pack");
            };
        }
    }
}
