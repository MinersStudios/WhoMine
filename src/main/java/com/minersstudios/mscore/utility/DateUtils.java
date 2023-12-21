package com.minersstudios.mscore.utility;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Utility class for date and time
 */
public final class DateUtils {
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    private static final String DEFAULT_TIMEZONE = DEFAULT_ZONE_ID.toString();
    private static final Map<InetAddress, String> TIMEZONE_CACHE = new ConcurrentHashMap<>();

    public static final String CHRONO_REGEX = "\\d+[smhdMy]";
    public static final Pattern CHRONO_PATTERN = Pattern.compile(CHRONO_REGEX);

    @Contract(" -> fail")
    private DateUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Gets timezone from ip
     *
     * @param ip IP address to get timezone from
     * @return Timezone from ip
     *         or default timezone if ip is null
     *         or failed to get timezone from ip
     */
    public static @NotNull String getTimezone(final @NotNull InetAddress ip) {
        final String cachedTimezone = TIMEZONE_CACHE.get(ip);

        if (cachedTimezone != null) {
            return cachedTimezone;
        }

        try (
                final var input = new URL("http://ip-api.com/json/" + ip.getHostAddress()).openStream();
                final var reader = new BufferedReader(new InputStreamReader(input))
        ) {
            final StringBuilder entirePage = new StringBuilder();
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                entirePage.append(inputLine);
            }

            final String pageString = entirePage.toString();
            final String timezone = pageString.contains("\"timezone\":\"")
                    ? pageString.split("\"timezone\":\"")[1].split("\",")[0]
                    : DEFAULT_TIMEZONE;

            TIMEZONE_CACHE.put(ip, timezone);

            return timezone;
        } catch (final IOException e) {
            MSLogger.warning("Failed to get timezone from ip " + ip.getHostAddress(), e);
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
            final @NotNull Instant date,
            final @Nullable InetAddress address
    ) {
        if (address == null) {
            return date
                    .atZone(DEFAULT_ZONE_ID)
                    .format(MSPlugin.globalConfig().getTimeFormatter());
        }

        final String timeZone = getTimezone(address);
        return date
                .atZone(
                        ZoneId.of(timeZone.equalsIgnoreCase("Europe/Kyiv")
                                ? "Europe/Kiev"
                                : timeZone
                        )
                ).format(MSPlugin.globalConfig().getTimeFormatter());
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
            final @NotNull Instant date,
            final @Nullable CommandSender sender
    ) {
        if (!(sender instanceof final Player player)) {
            return getDate(date, null);
        }

        final InetSocketAddress socketAddress = player.getAddress();
        return getDate(
                date,
                socketAddress != null
                ? socketAddress.getAddress()
                : null
        );
    }

    /**
     * Gets time suggestions from a string number. Can be used for time
     * arguments in tab complete.
     * <br>
     * Includes : s, m, h, d, M, y
     *
     * @param input Amount of time in string
     * @return Time suggestions or empty list if the input is not a number
     */
    public static @NotNull @Unmodifiable List<String> getTimeSuggestions(final @NotNull String input) {
        return StringUtils.isNumeric(input)
                ? List.of(
                        input + "s",
                        input + "m",
                        input + "h",
                        input + "d",
                        input + "M",
                        input + "y"
                )
                : Collections.emptyList();
    }

    /**
     * Gets a date with time added
     * <br>
     * Regex : \d+[smhdMy]
     *
     * @param string Time string
     * @return Date with time added
     * @throws NumberFormatException If the string does not contain a parsable
     *                               long
     * @throws DateTimeException     If the chrono unit value is too big and the
     *                               addition cannot be made
     * @throws ArithmeticException   If numeric overflow occurs
     * @see #getDateFromString(String, boolean)
     */
    public static @Nullable Instant getDateFromString(final @NotNull String string) throws NumberFormatException, DateTimeException, ArithmeticException {
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
     * @throws NumberFormatException If the string does not contain a parsable
     *                               long
     * @throws DateTimeException     If the chrono unit value is too big and the
     *                               addition cannot be made
     * @throws ArithmeticException   If numeric overflow occurs
     */
    public static @Nullable Instant getDateFromString(
            final @NotNull String string,
            final boolean throwException
    ) throws NumberFormatException, DateTimeException, ArithmeticException {
        if (!matchesChrono(string)) {
            return null;
        }

        final Instant now = Instant.now();
        final String amountString = string.replaceAll("[smhdMy]", "");
        final String chronoUnit = string.replaceAll("\\d+", "");

        try {
            final long amount = Long.parseLong(amountString);

            return switch (chronoUnit) {
                        case "s" -> now.plus(amount, ChronoUnit.SECONDS);
                        case "m" -> now.plus(amount, ChronoUnit.MINUTES);
                        case "h" -> now.plus(amount, ChronoUnit.HOURS);
                        case "M" -> now.plus(Math.multiplyExact(amount, 30), ChronoUnit.DAYS);
                        case "y" -> now.plus(Math.multiplyExact(amount, 365), ChronoUnit.DAYS);
                        default ->  now.plus(amount, ChronoUnit.DAYS);
                    };
        } catch (final DateTimeException | NumberFormatException | ArithmeticException e) {
            if (throwException) throw e;
            return null;
        }
    }

    /**
     * @param string String to be checked
     * @return True if the string matches the {@link #CHRONO_REGEX} regex
     */
    @Contract("null -> false")
    public static boolean matchesChrono(final @Nullable String string) {
        return ChatUtils.isNotBlank(string)
                && CHRONO_PATTERN.matcher(string).matches();
    }
}
