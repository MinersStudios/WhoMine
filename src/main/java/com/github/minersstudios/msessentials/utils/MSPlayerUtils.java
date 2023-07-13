package com.github.minersstudios.msessentials.utils;

import com.github.minersstudios.msessentials.MSEssentials;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslation;
import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslationComponent;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
public final class MSPlayerUtils {
    /**
     * Regex supports all <a href="https://jrgraphix.net/r/Unicode/0400-04FF">cyrillic</a> characters
     */
    public static final @NotNull String NAME_REGEX = "[-Ѐ-ӿ]+";
    public static final TranslatableComponent DEFAULT_BAN_REASON = renderTranslationComponent("ms.command.ban.default_reason");
    public static final TranslatableComponent DEFAULT_MUTE_REASON = renderTranslationComponent("ms.command.mute.default_reason");
    public static final TranslatableComponent DEFAULT_KICK_REASON = renderTranslationComponent("ms.command.kick.default_reason");
    public static final String DEFAULT_BAN_REASON_STRING = renderTranslation("ms.command.ban.default_reason");
    public static final String DEFAULT_MUTE_REASON_STRING = renderTranslation("ms.command.mute.default_reason");
    public static final String DEFAULT_KICK_REASON_STRING = renderTranslation("ms.command.kick.default_reason");

    private MSPlayerUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Adds player to the "hide_tags" {@link Team} and sets the {@link Scoreboard} for player
     *
     * @param player the player
     */
    public static void hideNameTag(@NotNull Player player) {
        MSEssentials.getScoreboardHideTagsTeam().addEntry(player.getName());
        player.setScoreboard(MSEssentials.getScoreboardHideTags());
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean matchesNameRegex(@Nullable String string) {
        return string != null && string.matches(NAME_REGEX);
    }
}
