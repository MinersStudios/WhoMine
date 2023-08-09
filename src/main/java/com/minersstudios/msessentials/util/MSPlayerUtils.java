package com.minersstudios.msessentials.util;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for players
 */
@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
public final class MSPlayerUtils {
    /**
     * Regex supports all <a href="https://jrgraphix.net/r/Unicode/0400-04FF">cyrillic</a> characters
     */
    public static final @NotNull String NAME_REGEX = "[-Ѐ-ӿ]+";

    private MSPlayerUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Adds player to the "hide_tags" {@link Team} and sets the {@link Scoreboard} for player
     *
     * @param player the player
     */
    public static void hideNameTag(@Nullable Player player) {
        if (player != null) {
            MSEssentials.getScoreboardHideTagsTeam().addEntry(player.getName());
            player.setScoreboard(MSEssentials.getScoreboardHideTags());
        }
    }

    /**
     * @return A list of all online players' names and IDs
     */
    public static @NotNull List<String> getLocalPlayerNames() {
        var completions = new LinkedList<String>();

        for (var player : Bukkit.getOnlinePlayers()) {
            PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

            if (playerInfo.isOnline()) {
                int id = playerInfo.getID(false, false);

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
    @Contract(value = "null -> false")
    public static boolean matchesNameRegex(@Nullable String string) {
        return string != null && string.matches(NAME_REGEX);
    }
}
