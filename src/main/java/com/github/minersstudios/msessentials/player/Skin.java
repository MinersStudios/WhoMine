package com.github.minersstudios.msessentials.player;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Skin {
    private final String name;
    private final String value;
    private final String signature;

    private Skin(
            @NotNull String name,
            @NotNull String value,
            @NotNull String signature
    ) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    @Contract(value = "_, _, _ -> new")
    public static @NotNull Skin create(
            @NotNull String name,
            @NotNull String value,
            @NotNull String signature
    ) {
        return new Skin(name, value, signature);
    }

    public static @Nullable Skin create(
            @NotNull String name,
            @NotNull String link
    ) {
        Skin skin = Skin.create(name, "a", "b");
        //TODO
        return skin;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull String getValue() {
        return this.value;
    }

    public @NotNull String getSignature() {
        return this.signature;
    }

    public boolean equals(@NotNull Skin skin) {
        return this.name.equalsIgnoreCase(skin.getName())
                && this.value.equals(skin.getValue())
                && this.signature.equals(skin.getSignature());
    }
}
