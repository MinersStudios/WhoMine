package com.minersstudios.msessentials.player;


import com.minersstudios.mscore.plugin.config.LanguageFile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

/**
 * Pronouns enum with custom messages.
 * All messages stored in the {@link LanguageFile}.
 *
 * @see PlayerFile
 */
public enum Pronouns {
    HE(
            translatable("ms.player.pronouns.he.join"),
            translatable("ms.player.pronouns.he.quit"),
            translatable("ms.player.pronouns.he.spit"),
            translatable("ms.player.pronouns.he.fart"),
            translatable("ms.player.pronouns.he.pronouns"),
            translatable("ms.player.pronouns.he.traveler"),
            translatable("ms.player.pronouns.he.sit"),
            translatable("ms.player.pronouns.he.get_up"),
            translatable("ms.player.pronouns.he.death"),
            translatable("ms.player.pronouns.he.kill"),
            translatable("ms.player.pronouns.he.said")
    ),
    SHE(
            translatable("ms.player.pronouns.she.join"),
            translatable("ms.player.pronouns.she.quit"),
            translatable("ms.player.pronouns.she.spit"),
            translatable("ms.player.pronouns.she.fart"),
            translatable("ms.player.pronouns.she.pronouns"),
            translatable("ms.player.pronouns.she.traveler"),
            translatable("ms.player.pronouns.she.sit"),
            translatable("ms.player.pronouns.she.get_up"),
            translatable("ms.player.pronouns.she.death"),
            translatable("ms.player.pronouns.she.kill"),
            translatable("ms.player.pronouns.she.said")
    ),
    THEY(
            translatable("ms.player.pronouns.they.join"),
            translatable("ms.player.pronouns.they.quit"),
            translatable("ms.player.pronouns.they.spit"),
            translatable("ms.player.pronouns.they.fart"),
            translatable("ms.player.pronouns.they.pronouns"),
            translatable("ms.player.pronouns.they.traveler"),
            translatable("ms.player.pronouns.they.sit"),
            translatable("ms.player.pronouns.they.get_up"),
            translatable("ms.player.pronouns.they.death"),
            translatable("ms.player.pronouns.they.kill"),
            translatable("ms.player.pronouns.they.said")
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
