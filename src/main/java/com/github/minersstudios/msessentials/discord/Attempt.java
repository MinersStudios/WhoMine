package com.github.minersstudios.msessentials.discord;

import org.jetbrains.annotations.Range;

public record Attempt(
        @Range(from = 0, to = Integer.MAX_VALUE) int count,
        @Range(from = 0, to = Long.MAX_VALUE) long time
) {
    public static final Attempt ZERO_ATTEMPT = new Attempt(0, 0L);
}
