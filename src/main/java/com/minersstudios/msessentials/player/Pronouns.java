package com.minersstudios.msessentials.player;


import com.minersstudios.mscore.locale.LanguageFile;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.mscore.locale.Translations.*;

/**
 * Pronouns enum with custom messages. All messages stored in the
 * {@link LanguageFile}.
 *
 * @see PlayerFile
 */
public enum Pronouns {
    HE(
            PLAYER_PRONOUNS_HE_JOIN.asTranslatable(),
            PLAYER_PRONOUNS_HE_QUIT.asTranslatable(),
            PLAYER_PRONOUNS_HE_SPIT.asTranslatable(),
            PLAYER_PRONOUNS_HE_FART.asTranslatable(),
            PLAYER_PRONOUNS_HE_PRONOUNS.asTranslatable(),
            PLAYER_PRONOUNS_HE_TRAVELER.asTranslatable(),
            PLAYER_PRONOUNS_HE_SIT.asTranslatable(),
            PLAYER_PRONOUNS_HE_GET_UP.asTranslatable(),
            PLAYER_PRONOUNS_HE_DEATH.asTranslatable(),
            PLAYER_PRONOUNS_HE_KILL.asTranslatable(),
            PLAYER_PRONOUNS_HE_SAID.asTranslatable()
    ),
    SHE(
            PLAYER_PRONOUNS_SHE_JOIN.asTranslatable(),
            PLAYER_PRONOUNS_SHE_QUIT.asTranslatable(),
            PLAYER_PRONOUNS_SHE_SPIT.asTranslatable(),
            PLAYER_PRONOUNS_SHE_FART.asTranslatable(),
            PLAYER_PRONOUNS_SHE_PRONOUNS.asTranslatable(),
            PLAYER_PRONOUNS_SHE_TRAVELER.asTranslatable(),
            PLAYER_PRONOUNS_SHE_SIT.asTranslatable(),
            PLAYER_PRONOUNS_SHE_GET_UP.asTranslatable(),
            PLAYER_PRONOUNS_SHE_DEATH.asTranslatable(),
            PLAYER_PRONOUNS_SHE_KILL.asTranslatable(),
            PLAYER_PRONOUNS_SHE_SAID.asTranslatable()
    ),
    THEY(
            PLAYER_PRONOUNS_THEY_JOIN.asTranslatable(),
            PLAYER_PRONOUNS_THEY_QUIT.asTranslatable(),
            PLAYER_PRONOUNS_THEY_SPIT.asTranslatable(),
            PLAYER_PRONOUNS_THEY_FART.asTranslatable(),
            PLAYER_PRONOUNS_THEY_PRONOUNS.asTranslatable(),
            PLAYER_PRONOUNS_THEY_TRAVELER.asTranslatable(),
            PLAYER_PRONOUNS_THEY_SIT.asTranslatable(),
            PLAYER_PRONOUNS_THEY_GET_UP.asTranslatable(),
            PLAYER_PRONOUNS_THEY_DEATH.asTranslatable(),
            PLAYER_PRONOUNS_THEY_KILL.asTranslatable(),
            PLAYER_PRONOUNS_THEY_SAID.asTranslatable()
    );

    private final TranslatableComponent joinMessage;
    private final TranslatableComponent quitMessage;
    private final TranslatableComponent spitMessage;
    private final TranslatableComponent fartMessage;
    private final TranslatableComponent pronouns;
    private final TranslatableComponent traveler;
    private final TranslatableComponent sitMessage;
    private final TranslatableComponent unSitMessage;
    private final TranslatableComponent deathMessage;
    private final TranslatableComponent killMessage;
    private final TranslatableComponent saidMessage;

    Pronouns(
            final @NotNull TranslatableComponent joinMessage,
            final @NotNull TranslatableComponent quitMessage,
            final @NotNull TranslatableComponent spitMessage,
            final @NotNull TranslatableComponent fartMessage,
            final @NotNull TranslatableComponent pronouns,
            final @NotNull TranslatableComponent traveler,
            final @NotNull TranslatableComponent sitMessage,
            final @NotNull TranslatableComponent unSitMessage,
            final @NotNull TranslatableComponent deathMessage,
            final @NotNull TranslatableComponent killMessage,
            final @NotNull TranslatableComponent saidMessage
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

    public @NotNull TranslatableComponent getJoinMessage() {
        return this.joinMessage;
    }

    public @NotNull TranslatableComponent getQuitMessage() {
        return this.quitMessage;
    }

    public @NotNull TranslatableComponent getSpitMessage() {
        return this.spitMessage;
    }

    public @NotNull TranslatableComponent getFartMessage() {
        return this.fartMessage;
    }

    public @NotNull TranslatableComponent getPronouns() {
        return this.pronouns;
    }

    public @NotNull TranslatableComponent getTraveler() {
        return this.traveler;
    }

    public @NotNull TranslatableComponent getSitMessage() {
        return this.sitMessage;
    }

    public @NotNull TranslatableComponent getUnSitMessage() {
        return this.unSitMessage;
    }

    public @NotNull TranslatableComponent getDeathMessage() {
        return this.deathMessage;
    }

    public @NotNull TranslatableComponent getKillMessage() {
        return this.killMessage;
    }

    public @NotNull TranslatableComponent getSaidMessage() {
        return this.saidMessage;
    }
}
