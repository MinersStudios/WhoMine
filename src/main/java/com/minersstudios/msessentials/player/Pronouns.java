package com.minersstudios.msessentials.player;


import com.minersstudios.mscore.language.LanguageFile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;

/**
 * Pronouns enum with custom messages.
 * All messages stored in the {@link LanguageFile}.
 *
 * @see PlayerFile
 */
public enum Pronouns {
    HE(
            PLAYER_PRONOUNS_HE_JOIN,
            PLAYER_PRONOUNS_HE_QUIT,
            PLAYER_PRONOUNS_HE_SPIT,
            PLAYER_PRONOUNS_HE_FART,
            PLAYER_PRONOUNS_HE_PRONOUNS,
            PLAYER_PRONOUNS_HE_TRAVELER,
            PLAYER_PRONOUNS_HE_SIT,
            PLAYER_PRONOUNS_HE_GET_UP,
            PLAYER_PRONOUNS_HE_DEATH,
            PLAYER_PRONOUNS_HE_KILL,
            PLAYER_PRONOUNS_HE_SAID
    ),
    SHE(
            PLAYER_PRONOUNS_SHE_JOIN,
            PLAYER_PRONOUNS_SHE_QUIT,
            PLAYER_PRONOUNS_SHE_SPIT,
            PLAYER_PRONOUNS_SHE_FART,
            PLAYER_PRONOUNS_SHE_PRONOUNS,
            PLAYER_PRONOUNS_SHE_TRAVELER,
            PLAYER_PRONOUNS_SHE_SIT,
            PLAYER_PRONOUNS_SHE_GET_UP,
            PLAYER_PRONOUNS_SHE_DEATH,
            PLAYER_PRONOUNS_SHE_KILL,
            PLAYER_PRONOUNS_SHE_SAID
    ),
    THEY(
            PLAYER_PRONOUNS_THEY_JOIN,
            PLAYER_PRONOUNS_THEY_QUIT,
            PLAYER_PRONOUNS_THEY_SPIT,
            PLAYER_PRONOUNS_THEY_FART,
            PLAYER_PRONOUNS_THEY_PRONOUNS,
            PLAYER_PRONOUNS_THEY_TRAVELER,
            PLAYER_PRONOUNS_THEY_SIT,
            PLAYER_PRONOUNS_THEY_GET_UP,
            PLAYER_PRONOUNS_THEY_DEATH,
            PLAYER_PRONOUNS_THEY_KILL,
            PLAYER_PRONOUNS_THEY_SAID
    );

    private final Component joinMessage;
    private final Component quitMessage;
    private final Component spitMessage;
    private final Component fartMessage;
    private final Component pronouns;
    private final Component traveler;
    private final Component sitMessage;
    private final Component unSitMessage;
    private final Component deathMessage;
    private final Component killMessage;
    private final Component saidMessage;

    Pronouns(
            final @NotNull Component joinMessage,
            final @NotNull Component quitMessage,
            final @NotNull Component spitMessage,
            final @NotNull Component fartMessage,
            final @NotNull Component pronouns,
            final @NotNull Component traveler,
            final @NotNull Component sitMessage,
            final @NotNull Component unSitMessage,
            final @NotNull Component deathMessage,
            final @NotNull Component killMessage,
            final @NotNull Component saidMessage
    ) {
        this.joinMessage = joinMessage;
        this.quitMessage = quitMessage;
        this.spitMessage = spitMessage;
        this.fartMessage = fartMessage;
        this.pronouns = pronouns;
        this.traveler = traveler;
        this.sitMessage = sitMessage;
        this.unSitMessage = unSitMessage;
        this.deathMessage = deathMessage;
        this.killMessage = killMessage;
        this.saidMessage = saidMessage;
    }

    public @NotNull Component getJoinMessage() {
        return this.joinMessage;
    }

    public @NotNull Component getQuitMessage() {
        return this.quitMessage;
    }

    public @NotNull Component getSpitMessage() {
        return this.spitMessage;
    }

    public @NotNull Component getFartMessage() {
        return this.fartMessage;
    }

    public @NotNull Component getPronouns() {
        return this.pronouns;
    }

    public @NotNull Component getTraveler() {
        return this.traveler;
    }

    public @NotNull Component getSitMessage() {
        return this.sitMessage;
    }

    public @NotNull Component getUnSitMessage() {
        return this.unSitMessage;
    }

    public @NotNull Component getDeathMessage() {
        return this.deathMessage;
    }

    public @NotNull Component getKillMessage() {
        return this.killMessage;
    }

    public @NotNull Component getSaidMessage() {
        return this.saidMessage;
    }
}
