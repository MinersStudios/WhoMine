package com.minersstudios.mscore.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.*;

/**
 * Utility class for text-related operations
 */
public final class ChatUtils {
    /**
     * Default style for components.
     * <ul>
     *     <li><b>Color:</b> {@link NamedTextColor#WHITE}
     *     <li><b>OBFUSCATED:</b> false
     *     <li><b>BOLD:</b> false
     *     <li><b>ITALIC:</b> false
     *     <li><b>STRIKETHROUGH:</b> false
     *     <li><b>UNDERLINED:</b> false
     * </ul>
     */
    public static final Style DEFAULT_STYLE = Style.style(
            WHITE,
            OBFUSCATED.withState(false),
            BOLD.withState(false),
            ITALIC.withState(false),
            STRIKETHROUGH.withState(false),
            UNDERLINED.withState(false)
    );

    /**
     * Colorless default style for components.
     * <ul>
     *     <li><b>OBFUSCATED:</b> false
     *     <li><b>BOLD:</b> false
     *     <li><b>ITALIC:</b> false
     *     <li><b>STRIKETHROUGH:</b> false
     *     <li><b>UNDERLINED:</b> false
     * </ul>
     */
    public static final Style COLORLESS_DEFAULT_STYLE = Style.style(
            OBFUSCATED.withState(false),
            BOLD.withState(false),
            ITALIC.withState(false),
            STRIKETHROUGH.withState(false),
            UNDERLINED.withState(false)
    );
    public static final String KEY_REGEX = "[a-z0-9./_-]+";
    public static final Pattern KEY_PATTERN = Pattern.compile(KEY_REGEX);

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();
    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.gson();

    @Contract(" -> fail")
    private ChatUtils() throws AssertionError {
        throw new AssertionError("Utility class");
    }

    /**
     * Extracts a message from array of arguments. Joins all arguments starting
     * from the given index with spaces between them.
     * <br>
     * Example:
     * <pre>{@code
     * extractMessage(new String[] { "Hello", "Sir.", "PackmanDude" }, 1);
     * }</pre>
     * - Will return "Sir. PackmanDude"
     *
     * @param args  Array of words
     * @param start Start index
     * @return Message extracted from an array of arguments and joined by spaces
     */
    @Contract("_, _ -> new")
    public static @NotNull String extractMessage(
            final @NotNull String[] args,
            final int start
    ) {
        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    /**
     * Normalizes the given text by capitalizing the first letter and converting
     * the rest of the letters to lowercase using the
     * {@link Locale#ROOT} locale.
     * <br>
     * Example:
     * <pre>{@code
     * normalize("hELLO");
     * }</pre>
     * - Will return "Hello"
     *
     * @param text Text to be normalized
     * @return Normalized text, or empty string if the given text is blank
     */
    public static @NotNull String normalize(final @NotNull String text) {
        final int length = text.length();

        if (length == 0) {
            return text;
        }

        final char[] chars = new char[length];
        chars[0] = Character.toUpperCase(text.charAt(0));

        for (int i = 1; i < length; ++i) {
            chars[i] = Character.toLowerCase(text.charAt(i));
        }

        return new String(chars);
    }

    /**
     * Normalizes the given component by capitalizing the first letter and
     * converting the rest of the letters to lowercase using the
     * {@link Locale#ROOT} locale.
     * <br>
     * Example:
     * <pre>{@code
     * normalize(text("hELLO"));
     * }</pre>
     * - Will return "Hello"
     *
     * @param component Component to be normalized
     * @return Normalized component
     * @see #normalize(String)
     */
    public static @NotNull Component normalize(final @NotNull Component component) {
        return text(normalize(serializePlainComponent(component))).style(component.style());
    }

    /**
     * Creates text with {@link #DEFAULT_STYLE}
     *
     * @param text Text to be styled
     * @return Default styled text
     */
    @Contract("_ -> new")
    public static @NotNull Component createDefaultStyledText(final @NotNull String text) {
        return text().append(text(text).style(DEFAULT_STYLE)).build();
    }

    /**
     * Serializes component to JSON string
     *
     * @param component Component to be serialized
     * @return Serialized component
     */
    @Contract("_ -> new")
    public static @NotNull String serializeGsonComponent(final @NotNull Component component) {
        return GSON_SERIALIZER.serialize(component);
    }

    /**
     * Serializes component to legacy string with hex colors and unusual X
     * repeated character hex format enabled
     *
     * @param component Component to be serialized
     * @return Serialized component
     */
    @Contract("_ -> new")
    public static @NotNull String serializeLegacyComponent(final @NotNull Component component) {
        return LEGACY_SERIALIZER.serialize(component);
    }

    /**
     * Serializes component to plain string
     *
     * @param component Component to be serialized
     * @return Serialized component
     */
    @Contract("_ -> new")
    public static @NotNull String serializePlainComponent(final @NotNull Component component) {
        return PLAIN_SERIALIZER.serialize(component);
    }

    /**
     * Deserializes component from JSON string
     *
     * @param text Text to be deserialized
     * @return Deserialized component
     */
    @Contract("_ -> new")
    public static @NotNull Component deserializeGsonComponent(final @NotNull String text) {
        return GSON_SERIALIZER.deserialize(text);
    }

    /**
     * Deserializes component from legacy string with hex colors and unusual X
     * repeated character hex format enabled
     *
     * @param text Text to be deserialized
     * @return Deserialized component
     */
    @Contract("_ -> new")
    public static @NotNull Component deserializeLegacyComponent(final @NotNull String text) {
        return LEGACY_SERIALIZER.deserialize(text);
    }

    /**
     * Deserializes component from plain string
     *
     * @param text Text to be deserialized
     * @return Deserialized component
     */
    @Contract("_ -> new")
    public static @NotNull Component deserializePlainComponent(final @NotNull String text) {
        return PLAIN_SERIALIZER.deserialize(text);
    }

    /**
     * Converts strings to components.
     * <br>
     * Example:
     * <pre>{@code
     * convertStringsToComponents(null, "Hello", "Sir", "PackmanDude");
     * }</pre>
     * - Will return a list of components with text "Hello", "Sir" and
     * "PackmanDude"
     *
     * @param style   Style to be applied to all components
     * @param strings Strings to be converted to components
     * @return List of components
     */
    public static @NotNull List<Component> convertStringsToComponents(
            final @Nullable Style style,
            final @NotNull List<String> strings
    ) {
        final var components = new ArrayList<Component>(strings.size());

        for (final var string : strings) {
            components.add(
                    style == null
                    ? text(string)
                    : text(string, style)
            );
        }

        return components;
    }

    /**
     * Converts strings to components with {@link #DEFAULT_STYLE}
     * <br>
     * Example:
     * <pre>{@code
     *     convertStringsToComponents(null, "Hello", "Sir", "PackmanDude");
     * }</pre>
     * - Will return a list of components with text "Hello", "Sir" and
     * "PackmanDude"
     *
     * @param strings Strings to be converted to components
     * @return List of components
     */
    public static @NotNull List<Component> convertStringsToComponents(final @NotNull List<String> strings) {
        return convertStringsToComponents(DEFAULT_STYLE, strings);
    }

    /**
     * Converts strings to components with {@link #DEFAULT_STYLE}
     * <br>
     * Example:
     * <pre>{@code
     *     convertStringsToComponents(null, "Hello", "Sir.", "PackmanDude");
     * }</pre>
     * - Will return a list of components with text "Hello", "Sir" and
     * "PackmanDude"
     *
     * @param first First string
     * @param rest  Other strings
     * @return List of components
     */
    public static @NotNull List<Component>  convertStringsToComponents(
            final @NotNull String first,
            final String @NotNull ... rest
    ) {
        return convertStringsToComponents(DEFAULT_STYLE, first, rest);
    }

    /**
     * Converts strings to components
     * <br>
     * Example:
     * <pre>{@code
     *     convertStringsToComponents(null, "Hello", "Sir.", "PackmanDude");
     * }</pre>
     * - Will return a list of components with text "Hello", "Sir" and
     * "PackmanDude"
     *
     * @param style Style to be applied to all components
     * @param first First string
     * @param rest Other strings
     * @return List of components
     */
    public static @NotNull List<Component> convertStringsToComponents(
            final @Nullable Style style,
            final @NotNull String first,
            final String @NotNull ... rest
    ) {
        final int restLength = rest.length;
        final Component[] components = new Component[restLength + 1];
        components[0] =
                style == null
                ? text(first)
                : text(first, style);

        for (int i = 0; i < restLength; ++i) {
            components[i + 1] =
                    style == null
                    ? text(rest[i])
                    : text(rest[i], style);
        }

        return Arrays.asList(components);
    }

    /**
     * @param string String to be checked
     * @return True if the string is blank
     */
    @Contract("null -> true")
    public static boolean isBlank(final @Nullable String string) {
        return string == null || string.isBlank();
    }

    /**
     * @param string String to be checked
     * @return True if the string is not blank
     */
    @Contract("null -> false")
    public static boolean isNotBlank(final @Nullable String string) {
        return !isBlank(string);
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #KEY_REGEX}
     */
    @Contract("null -> false")
    public static boolean matchesKey(final @Nullable String string) {
        return isNotBlank(string)
                && KEY_PATTERN.matcher(string).matches();
    }

    /**
     * @param string String to be checked
     * @throws IllegalArgumentException If string doesn't match {@link #KEY_REGEX}
     */
    @Contract("null -> fail")
    public static void validateKey(final @Nullable String string) {
        if (!matchesKey(string)) {
            throw new IllegalArgumentException("Key '" + string + "' does not match regex " + ChatUtils.KEY_REGEX);
        }
    }
}
