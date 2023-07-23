package com.minersstudios.mscore.utils;

import com.minersstudios.mscore.logger.MSLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static com.minersstudios.mscore.MSCore.getConfiguration;

public final class DateUtils {
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    private static final String DEFAULT_TIMEZONE = DEFAULT_ZONE_ID.toString();
    private static final Map<InetAddress, String> TIMEZONE_CACHE = new ConcurrentHashMap<>();

    public static final String CHRONO_REGEX = "\\d+[smhdMy]";
    public static final Pattern CHRONO_PATTERN = Pattern.compile(CHRONO_REGEX);

    @Contract(value = " -> fail")
    private DateUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets timezone from ip
     *
     * @param ip IP address to get timezone from
     * @return Timezone from ip or default timezone
     *         if ip is null or failed to get timezone
     *         from ip
     */
    public static @NotNull String getTimezone(@NotNull InetAddress ip) {
        String cachedTimezone = TIMEZONE_CACHE.get(ip);

        if (cachedTimezone != null) return cachedTimezone;

        try (
                var input = new URL("http://ip-api.com/json/" + ip.getHostAddress()).openStream();
                var reader = new BufferedReader(new InputStreamReader(input))
        ) {
            StringBuilder entirePage = new StringBuilder();

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                entirePage.append(inputLine);
            }

            String pageString = entirePage.toString();
            String timezone = pageString.contains("\"timezone\":\"")
                    ? pageString.split("\"timezone\":\"")[1].split("\",")[0]
                    : DEFAULT_TIMEZONE;

            TIMEZONE_CACHE.put(ip, timezone);

            return timezone;
        } catch (IOException e) {
            MSLogger.log(Level.WARNING, "Failed to get timezone from ip " + ip.getHostAddress(), e);
            return DEFAULT_TIMEZONE;
        }
    }

    /**
     * Gets date from string
     *
     * @param date    Date to be converted
     * @param address Address to get time zone from
     * @return String date format with address time zone
     */
    public static @NotNull String getDate(
            @NotNull Instant date,
            @Nullable InetAddress address
    ) {
        if (address == null) {
            return date.atZone(DEFAULT_ZONE_ID).format(getConfiguration().timeFormatter);
        }

        String timeZone = getTimezone(address);
        return date.atZone(
                ZoneId.of(timeZone.equalsIgnoreCase("Europe/Kyiv")
                        ? "Europe/Kiev"
                        : timeZone
                )).format(getConfiguration().timeFormatter);
    }

    /**
     * Gets date with sender time zone
     *
     * @param date   Date to be converted
     * @param sender Command sender to get time zone from
     * @return String date format with sender time zone
     *         or default time zone if sender is null
     *         or sender is not a player
     * @see #getDate(Instant, InetAddress)
     */
    public static @NotNull String getSenderDate(
            @NotNull Instant date,
            @Nullable CommandSender sender
    ) {
        if (sender instanceof Player player) {
            InetSocketAddress socketAddress = player.getAddress();
            return DateUtils.getDate(
                    date,
                    socketAddress != null
                    ? socketAddress.getAddress()
                    : null
            );
        }
        return DateUtils.getDate(date, null);
    }

    /**
     * Gets time suggestions from number
     * <br>
     * Used for command tab completer
     *
     * @param input Number of time
     * @return Time suggestions or empty list
     *         if the input does not match the regex
     */
    public static @NotNull List<String> getTimeSuggestions(@NotNull String input) {
        var suggestions = new ArrayList<String>();
        if (!input.matches("\\d+")) return suggestions;
        suggestions.add(input + "s");
        suggestions.add(input + "m");
        suggestions.add(input + "h");
        suggestions.add(input + "d");
        suggestions.add(input + "M");
        suggestions.add(input + "y");
        return suggestions;
    }

    /**
     * Gets a date with time added
     * <br>
     * Regex : \d+[smhdMy]
     *
     * @param string Time string
     * @return Date with time added
     * @throws NumberFormatException If the string does not contain a parsable long
     * @throws DateTimeException     If the chrono unit value is too big and the
     *                               addition cannot be made
     * @throws ArithmeticException   If numeric overflow occurs
     * @see #getDateFromString(String, boolean)
     */
    public static @Nullable Instant getDateFromString(@NotNull String string) throws NumberFormatException, DateTimeException, ArithmeticException {
        return getDateFromString(string, true);
    }

    /**
     * Gets a date with time added
     * <br>
     * Regex : \d+[smhdMy]
     *
     * @param string         Time string
     * @param throwException If true, an exception will be thrown
     * @return Date with time added
     * @throws NumberFormatException If the string does not contain a parsable long
     * @throws DateTimeException     If the chrono unit value is too big and the
     *                               addition cannot be made
     * @throws ArithmeticException   If numeric overflow occurs
     */
    public static @Nullable Instant getDateFromString(
            @NotNull String string,
            boolean throwException
    ) throws NumberFormatException, DateTimeException, ArithmeticException {
        if (!matchesChrono(string)) return null;

        Instant now = Instant.now();
        String chronoUnit = string.replaceAll("\\d+", "");

        try {
            long number = Long.parseLong(string.replaceAll("[smhdMy]", ""));
            return switch (chronoUnit) {
                        case "s" -> now.plus(number, ChronoUnit.SECONDS);
                        case "m" -> now.plus(number, ChronoUnit.MINUTES);
                        case "h" -> now.plus(number, ChronoUnit.HOURS);
                        case "M" -> now.plus(Math.multiplyExact(number, 30), ChronoUnit.DAYS);
                        case "y" -> now.plus(Math.multiplyExact(number, 365), ChronoUnit.DAYS);
                        default -> now.plus(number, ChronoUnit.DAYS);
                    };
        } catch (DateTimeException | NumberFormatException | ArithmeticException e) {
            if (throwException) throw e;
            return null;
        }
    }

    /**
     * @param string String to be checked
     * @return True if the string matches the {@link #CHRONO_REGEX} regex
     */
    @Contract(value = "null -> false")
    public static boolean matchesChrono(@Nullable String string) {
        return string != null && CHRONO_PATTERN.matcher(string).matches();
    }
}
