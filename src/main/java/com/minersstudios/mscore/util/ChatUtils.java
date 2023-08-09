package com.minersstudios.mscore.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.*;

/**
 * Utility class for text-related operations
 */
public final class ChatUtils {
    /**
     * Default style for components.
     * <br>
     * <b>Color:</b> {@link NamedTextColor#WHITE}
     * <br>
     * <b>Obfuscated:</b> false
     * <br>
     * <b>Bold:</b> false
     * <br>
     * <b>Italic:</b> false
     * <br>
     * <b>Strikethrough:</b> false
     * <br>
     * <b>Underlined:</b> false
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
     * <br>
     * <b>Obfuscated:</b> false
     * <br>
     * <b>Bold:</b> false
     * <br>
     * <b>Italic:</b> false
     * <br>
     * <b>Strikethrough:</b> false
     * <br>
     * <b>Underlined:</b> false
     */
    public static final Style COLORLESS_DEFAULT_STYLE = Style.style(
            OBFUSCATED.withState(false),
            BOLD.withState(false),
            ITALIC.withState(false),
            STRIKETHROUGH.withState(false),
            UNDERLINED.withState(false)
    );

    @Contract(value = " -> fail")
    private ChatUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Extracts message from array of arguments. Joins all arguments
     * starting from the given index with spaces between them.
     * <br>
     * Example:
     * <pre>{@code
     * extractMessage(new String[]{"Hello", "Sir.", "PackmanDude"}, 1);
     * }</pre>
     * - will return "Sir. PackmanDude"
     *
     * @param args  Array of words
     * @param start Start index
     * @return Message extracted from an array of arguments and joined by spaces
     */
    @Contract("_, _ -> new")
    public static @NotNull String extractMessage(
            @NotNull String[] args,
            int start
    ) {
        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    /**
     * Normalizes the given text by capitalizing the first letter and
     * converting the rest of the letters to lowercase using the
     * {@link Locale#ROOT} locale.
     * <br>
     * Example:
     * <pre>{@code
     * normalize("hELLO");
     * }</pre>
     * - will return "Hello"
     *
     * @param text Text to be normalized
     * @return Normalized text, or empty string if the given text is blank
     * @see StringUtils#isBlank(CharSequence)
     */
    public static @NotNull String normalize(@NotNull String text) {
        if (StringUtils.isBlank(text)) return text;

        String firstLetter = text.substring(0, 1).toUpperCase(Locale.ROOT);
        String other = text.substring(1).toLowerCase(Locale.ROOT);

        return firstLetter + other;
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
     * - will return "Hello"
     *
     * @param component Component to be normalized
     * @return Normalized component
     * @see #normalize(String)
     */
    public static @NotNull Component normalize(@NotNull Component component) {
        return text(normalize(serializePlainComponent(component))).style(component.style());
    }

    /**
     * Creates text with {@link #DEFAULT_STYLE}
     *
     * @param text Text to be styled
     * @return Default styled text
     */
    @Contract("_ -> new")
    public static @NotNull Component createDefaultStyledText(@NotNull String text) {
        return text().append(text(text).style(DEFAULT_STYLE)).build();
    }

    /**
     * Serializes component to JSON string
     *
     * @param component Component to be serialized
     * @return Serialized component
     */
    @Contract("_ -> new")
    public static @NotNull String serializeGsonComponent(@NotNull Component component) {
        return GsonComponentSerializer.gson().serialize(component);
    }

    /**
     * Serializes component to legacy string with hex colors and unusual X repeated character hex format enabled
     *
     * @param component Component to be serialized
     * @return Serialized component
     */
    @Contract("_ -> new")
    public static @NotNull String serializeLegacyComponent(@NotNull Component component) {
        return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(component);
    }

    /**
     * Serializes component to plain string
     *
     * @param component Component to be serialized
     * @return Serialized component
     */
    @Contract("_ -> new")
    public static @NotNull String serializePlainComponent(@NotNull Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Deserializes component from JSON string
     *
     * @param text Text to be deserialized
     * @return Deserialized component
     */
    @Contract("_ -> new")
    public static @NotNull Component deserializeGsonComponent(@NotNull String text) {
        return GsonComponentSerializer.gson().deserialize(text);
    }

    /**
     * Deserializes component from legacy string with
     * hex colors and unusual X repeated character hex
     * format enabled
     *
     * @param text Text to be deserialized
     * @return Deserialized component
     */
    @Contract("_ -> new")
    public static @NotNull Component deserializeLegacyComponent(@NotNull String text) {
        return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().deserialize(text);
    }

    /**
     * Deserializes component from plain string
     *
     * @param text Text to be deserialized
     * @return Deserialized component
     */
    @Contract("_ -> new")
    public static @NotNull Component deserializePlainComponent(@NotNull String text) {
        return PlainTextComponentSerializer.plainText().deserialize(text);
    }

    /**
     * Converts strings to components.
     * <br>
     * Example:
     * <pre>{@code
     * convertStringsToComponents(null, "Hello", "Sir.", "PackmanDude");
     * }</pre>
     * - will return list of components with text "Hello", "Sir." and "PackmanDude"
     *
     * @param style   Style to be applied to all components
     * @param strings Strings to be converted to components
     * @return List of components
     */
    public static @NotNull List<Component> convertStringsToComponents(
            @Nullable Style style,
            String @NotNull [] strings
    ) {
        var components = new ArrayList<Component>();

        for (var string : strings) {
            Component component = text(string);
            components.add(
                    style == null
                    ? component
                    : component.style(style)
            );
        }

        return components;
    }

    /**
     * Converts strings to components with {@link #DEFAULT_STYLE}
     * <br>
     * Example:
     * <pre>{@code
     *     convertStringsToComponents(null, "Hello", "Sir.", "PackmanDude");
     * }</pre>
     * - will return list of components with text "Hello", "Sir." and "PackmanDude"
     *
     * @param strings Strings to be converted to components
     * @return List of components
     */
    public static @NotNull List<Component> convertStringsToComponents(String @NotNull [] strings) {
        return convertStringsToComponents(DEFAULT_STYLE, strings);
    }

    /**
     * Converts strings to components with {@link #DEFAULT_STYLE}
     * <br>
     * Example:
     * <pre>{@code
     *     convertStringsToComponents(null, "Hello", "Sir.", "PackmanDude");
     * }</pre>
     * - will return list of components with text "Hello", "Sir." and "PackmanDude"
     *
     * @param first First string
     * @param other Other strings
     * @return List of components
     */
    public static @NotNull List<Component> convertStringsToComponents(
            @NotNull String first,
            String @NotNull ... other
    ) {
        return convertStringsToComponents(DEFAULT_STYLE, first, other);
    }

    /**
     * Converts strings to components
     * <br>
     * Example:
     * <pre>{@code
     *     convertStringsToComponents(null, "Hello", "Sir.", "PackmanDude");
     * }</pre>
     * - will return list of components with text "Hello", "Sir." and "PackmanDude"
     *
     * @param style Style to be applied to all components
     * @param first First string
     * @param other Other strings
     * @return List of components
     */
    public static @NotNull List<Component> convertStringsToComponents(
            @Nullable Style style,
            @NotNull String first,
            String @NotNull ... other
    ) {
        String[] strings = new String[other.length + 1];
        strings[0] = first;

        System.arraycopy(other, 0, strings, 1, other.length);
        return convertStringsToComponents(style, strings);
    }
}
