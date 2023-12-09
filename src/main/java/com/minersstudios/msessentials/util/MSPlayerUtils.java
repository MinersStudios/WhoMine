package com.minersstudios.msessentials.util;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for players
 */
public final class MSPlayerUtils {
    /**
     * Regex supports all <a href="https://jrgraphix.net/r/Unicode/0400-04FF">cyrillic</a> characters
     */
    public static final String NAME_REGEX = "[-Ѐ-ӿ]+";
    public static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    private MSPlayerUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Adds player to the "hide_tags" {@link Team} and sets the {@link Scoreboard} for player
     *
     * @param player the player
     */
    public static void hideNameTag(final @Nullable Player player) {
        if (player != null) {
            MSEssentials.scoreboardHideTagsTeam().addEntry(player.getName());
            player.setScoreboard(MSEssentials.scoreboardHideTags());
        }
    }

    /**
     * @return A list of all online players' names and IDs
     */
    public static @NotNull List<String> getLocalPlayerNames() {
        final var completions = new ArrayList<String>();

        for (final var player : Bukkit.getOnlinePlayers()) {
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

            if (playerInfo.isOnline()) {
                final int id = playerInfo.getID(false, false);

                if (id != -1) {
                    completions.add(String.valueOf(id));
                }

                completions.add(player.getName());
            }
        }

        return completions;
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAME_REGEX}
     */
    @Contract("null -> false")
    public static boolean matchesNameRegex(final @Nullable String string) {
        return StringUtils.isNotBlank(string)
                && NAME_PATTERN.matcher(string).matches();
    }
}
