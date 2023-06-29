package com.github.minersstudios.msutils.player;


import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Pronouns {
    HE, SHE, THEY;

    public @NotNull Component getJoinMessage() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".join");
    }

    public @NotNull Component getQuitMessage() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".quit");
    }

    public @NotNull Component getSpitMessage() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".spit");
    }

    public @NotNull Component getFartMessage() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".fart");
    }

    public @NotNull Component getPronouns() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".pronouns");
    }

    public @NotNull Component getTraveler() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".traveler");
    }

    public @NotNull Component getSitMessage() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".sit");
    }

    public @NotNull Component getUnSitMessage() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".get_up");
    }

    public @NotNull Component getDeathMessage() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".death");
    }

    public @NotNull Component getKillMessage() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".kill");
    }

    public @NotNull Component getSaidMessage() {
        return Component.translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".said");
    }
}
