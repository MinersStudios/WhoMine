package com.minersstudios.msessentials.player;


import com.minersstudios.mscore.config.LanguageFile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static net.kyori.adventure.text.Component.translatable;

/**
 * Pronouns enum with custom messages.
 * All messages stored in the {@link LanguageFile}.
 *
 * @see PlayerFile
 */
public enum Pronouns {
    HE, SHE, THEY;

    public @NotNull Component getJoinMessage() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".join");
    }

    public @NotNull Component getQuitMessage() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".quit");
    }

    public @NotNull Component getSpitMessage() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".spit");
    }

    public @NotNull Component getFartMessage() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".fart");
    }

    public @NotNull Component getPronouns() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".pronouns");
    }

    public @NotNull Component getTraveler() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".traveler");
    }

    public @NotNull Component getSitMessage() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".sit");
    }

    public @NotNull Component getUnSitMessage() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".get_up");
    }

    public @NotNull Component getDeathMessage() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".death");
    }

    public @NotNull Component getKillMessage() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".kill");
    }

    public @NotNull Component getSaidMessage() {
        return translatable("ms.player.pronouns." + this.name().toLowerCase(Locale.ROOT) + ".said");
    }
}
