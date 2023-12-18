package com.minersstudios.mscore.plugin;

import com.minersstudios.mscore.language.LanguageFile;
import com.minersstudios.mscore.utility.Font;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

/**
 * A custom logging utility designed to send formatted log messages with
 * different levels and colors to various targets. Supports logging to the
 * console, players, and command senders using AdventureAPI for text formatting.
 * <br>
 * Available levels, and their corresponding colors:
 * <ul>
 *     <li>{@link Level#SEVERE} - Red</li>
 *     <li>{@link Level#WARNING} - Yellow</li>
 *     <li>{@link Level#INFO} - Default</li>
 *     <li>{@link Level#FINE} - Green</li>
 * </ul>
 */
public final class MSLogger {
    private static final String NAME = "MS";
    private static final Logger LOGGER = Logger.getLogger(NAME);
    private static final String ANSI_LIME = "\u001B[92m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final int SEVERE = 1000;
    private static final int WARNING = 900;
    private static final int FINE = 500;

    @Contract(" -> fail")
    private MSLogger() {
        throw new AssertionError("This class cannot be instantiated!");
    }

    /**
     * Logs a message with the specified severity level. If the level is 
     * {@link Level#FINE}, the message will be logged with the {@link Level#INFO}
     * level and colored lime green.
     *
     * @param level   One of the message level identifiers
     * @param message The component message
     * @see #log(Level, String)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull Component message
    ) {
        log(level, serialize(message));
    }

    /**
     * Logs a message with the specified severity level. If the level is 
     * {@link Level#FINE}, the message will be logged with the {@link Level#INFO}
     * level and colored lime green.
     *
     * @param level   One of the message level identifiers
     * @param message The string message
     * @see Logger#log(Level, String)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull String message
    ) {
        if (level == Level.FINE) {
            LOGGER.log(Level.INFO, ANSI_LIME + message + ANSI_RESET);
        } else {
            LOGGER.log(level, message);
        }
    }

    /**
     * Log a message with the specified severity level and associated array of 
     * parameters. If the level is {@link Level#FINE}, the message will be 
     * logged with the {@link Level#INFO} level and colored lime green.
     *
     * @param level   One of the message level identifiers
     * @param message The component message
     * @param params  Array of parameters to the message
     * @see #log(Level, String, Object...)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull Component message,
            final Object @NotNull ... params
    ) {
        log(level, serialize(message), params);
    }

    /**
     * Log a message with the specified severity level and associated array of 
     * parameters. If the level is {@link Level#FINE}, the message will be 
     * logged with the {@link Level#INFO} level and colored lime green.
     *
     * @param level   One of the message level identifiers
     * @param message The string message
     * @param params  Array of parameters to the message
     * @see Logger#log(Level, String, Object...)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull String message,
            final @Nullable Object @NotNull ... params
    ) {
        if (level == Level.FINE) {
            LOGGER.log(Level.INFO, ANSI_LIME + message + ANSI_RESET, params);
        } else {
            LOGGER.log(level, message, params);
        }
    }

    /**
     * Logs a message with the specified severity level and associated throwable.
     * If the level is {@link Level#FINE}, the message will be logged with the
     * {@link Level#INFO} level and colored lime green.
     *
     * @param level     One of the message level identifiers
     * @param message   The component message
     * @param throwable Throwable associated with log message
     * @see #log(Level, String, Throwable)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull Component message,
            final @NotNull Throwable throwable
    ) {
        log(level, serialize(message), throwable);
    }

    /**
     * Logs a message with the specified severity level and associated throwable.
     * If the level is {@link Level#FINE}, the message will be logged with the 
     * {@link Level#INFO} level and colored lime green.
     *
     * @param level     One of the message level identifiers
     * @param message   The string message
     * @param throwable Throwable associated with log message
     * @see Logger#log(Level, String, Throwable)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull String message,
            final @NotNull Throwable throwable
    ) {
        if (level == Level.FINE) {
            LOGGER.log(Level.INFO, ANSI_LIME + message + ANSI_RESET, throwable);
        } else {
            LOGGER.log(level, message, throwable);
        }
    }

    /**
     * Logs a message with the specified severity level to the specified target.
     * If the target is null, the message will be logged to the console.
     * <br>
     * All messages will be colored according to their severity level.
     *
     * @param level   One of the message level identifiers
     * @param target  The target to send the message to
     *                (null for console sender)
     * @param message The string message
     * @see #logChat(Level, CommandSender, Component)
     */
    public static void logChat(
            final @NotNull Level level,
            final @Nullable CommandSender target,
            final @NotNull String message
    ) {
        logChat(level, target, text(message));
    }

    /**
     * Logs a message with the specified severity level  to the specified target.
     * If the target is null, the message will be logged to the console.
     * <br>
     * All messages will be colored according to their severity level.
     *
     * @param level   One of the message level identifiers
     * @param target  The target to send the message to
     *                (null for console sender)
     * @param message The component message
     */
    public static void logChat(
            final @NotNull Level level,
            final @Nullable CommandSender target,
            final @NotNull Component message
    ) {
        final Component coloredMessage = switch (level.intValue()) {
            case SEVERE ->  message.color(RED);
            case WARNING -> message.color(GOLD);
            case FINE ->    message.color(GREEN);
            default ->      message;
        };

        if (target == null) {
            try {
                Bukkit.getServer().getConsoleSender().sendMessage(coloredMessage);
            } catch (final Exception e) {
                warning("Tried to log a message to the console, but the console was null!");
            }
        } else if (target instanceof BlockCommandSender) {
            target.sendMessage(
                    message instanceof final TranslatableComponent translatableComponent
                    ? LanguageFile.renderTranslationComponent(translatableComponent)
                    : message
            );
        } else {
            target.sendMessage(
                    switch (level.intValue()) {
                        case SEVERE ->  space().append(Font.Components.RED_EXCLAMATION_MARK).append(space())
                                        .append(coloredMessage);
                        case WARNING -> space().append(Font.Components.YELLOW_EXCLAMATION_MARK).append(space())
                                        .append(coloredMessage);
                        case FINE ->    space().append(Font.Components.GREEN_EXCLAMATION_MARK).append(space())
                                        .append(coloredMessage);
                        default ->      coloredMessage;
                    }
            );
        }
    }

    /**
     * Logs a message with the {@link Level#SEVERE} severity level
     *
     * @param message The string message
     * @see #log(Level, String)
     */
    public static void severe(final @NotNull String message) {
        log(Level.SEVERE, message);
    }

    /**
     * Logs a message with the {@link Level#SEVERE} severity level
     *
     * @param message The component message
     * @see #log(Level, Component)
     */
    public static void severe(final @NotNull Component message) {
        log(Level.SEVERE, message);
    }

    /**
     * Logs a message with the {@link Level#SEVERE} severity level and 
     * associated array of parameters
     *
     * @param message The string message
     * @param params  Array of parameters to the message
     * @see #log(Level, String, Object...)
     */
    public static void severe(
            final @NotNull String message,
            final Object @NotNull ... params
    ) {
        log(Level.SEVERE, message, params);
    }

    /**
     * Logs a message with the {@link Level#SEVERE} severity level and 
     * associated array of parameters
     *
     * @param message The component message
     * @param params  Array of parameters to the message
     * @see #log(Level, Component, Object...)
     */
    public static void severe(
            final @NotNull Component message,
            final Object @NotNull ... params
    ) {
        log(Level.SEVERE, message, params);
    }

    /**
     * Logs a message with the {@link Level#SEVERE} severity level and
     * associated throwable
     *
     * @param message   The string message
     * @param throwable Throwable associated with log message
     * @see #log(Level, String, Throwable)
     */
    public static void severe(
            final @NotNull String message,
            final @NotNull Throwable throwable
    ) {
        log(Level.SEVERE, message, throwable);
    }

    /**
     * Logs a message with the {@link Level#SEVERE} severity level and 
     * associated throwable
     *
     * @param message   The component message
     * @param throwable Throwable associated with log message
     * @see #log(Level, Component, Throwable)
     */
    public static void severe(
            final @NotNull Component message,
            final @NotNull Throwable throwable
    ) {
        log(Level.SEVERE, message, throwable);
    }

    /**
     * Logs a message with the {@link Level#SEVERE} severity level to the 
     * specified target
     *
     * @param sender  The target to send the message to
     *                (null for console sender)
     * @param message The string message
     * @see #logChat(Level, CommandSender, String)
     */
    public static void severe(
            final @Nullable CommandSender sender,
            final @NotNull String message
    ) {
        logChat(Level.SEVERE, sender, message);
    }

    /**
     * Logs a message with the {@link Level#SEVERE} severity level to the 
     * specified target
     *
     * @param sender  The target to send the message to
     *                (null for console sender)
     * @param message The component message
     * @see #logChat(Level, CommandSender, Component)
     */
    public static void severe(
            final @Nullable CommandSender sender,
            final @NotNull Component message
    ) {
        logChat(Level.SEVERE, sender, message);
    }

    /**
     * Logs a message with the {@link Level#WARNING} severity level
     *
     * @param message The string message
     * @see #log(Level, String)
     */
    public static void warning(final @NotNull String message) {
        log(Level.WARNING, message);
    }

    /**
     * Logs a message with the {@link Level#WARNING} severity level
     *
     * @param message The component message
     * @see #log(Level, Component)
     */
    public static void warning(final @NotNull Component message) {
        log(Level.WARNING, message);
    }

    /**
     * Logs a message with the {@link Level#WARNING} severity level and 
     * associated array of parameters
     *
     * @param message The string message
     * @param params  Array of parameters to the message
     * @see #log(Level, String, Object...)
     */
    public static void warning(
            final @NotNull String message,
            final Object @NotNull ... params
    ) {
        log(Level.WARNING, message, params);
    }

    /**
     * Logs a message with the {@link Level#WARNING} severity level and
     * associated array of parameters
     *
     * @param message The component message
     * @param params  Array of parameters to the message
     * @see #log(Level, Component, Object...)
     */
    public static void warning(
            final @NotNull Component message,
            final Object @NotNull ... params
    ) {
        log(Level.WARNING, message, params);
    }

    /**
     * Logs a message with the {@link Level#WARNING} severity level and 
     * associated throwable
     *
     * @param message   The string message
     * @param throwable Throwable associated with log message
     * @see #log(Level, String, Throwable)
     */
    public static void warning(
            final @NotNull String message,
            final @NotNull Throwable throwable
    ) {
        log(Level.WARNING, message, throwable);
    }

    /**
     * Logs a message with the {@link Level#WARNING} severity level and 
     * associated throwable
     *
     * @param message   The component message
     * @param throwable Throwable associated with log message
     * @see #log(Level, Component, Throwable)
     */
    public static void warning(
            final @NotNull Component message,
            final @NotNull Throwable throwable
    ) {
        log(Level.WARNING, message, throwable);
    }

    /**
     * Logs a message with the {@link Level#WARNING} severity level to the
     * specified target
     *
     * @param sender  The target to send the message to
     *                (null for console sender)
     * @param message The string message
     * @see #logChat(Level, CommandSender, String)
     */
    public static void warning(
            final @Nullable CommandSender sender,
            final @NotNull String message
    ) {
        logChat(Level.WARNING, sender, message);
    }

    /**
     * Logs a message with the {@link Level#WARNING} severity level to the
     * specified target
     *
     * @param sender  The target to send the message to
     *                (null for console sender)
     * @param message The component message
     * @see #logChat(Level, CommandSender, Component)
     */
    public static void warning(
            final @Nullable CommandSender sender,
            final @NotNull Component message
    ) {
        logChat(Level.WARNING, sender, message);
    }

    /**
     * Logs a message with the {@link Level#INFO} severity level
     *
     * @param message The string message
     * @see #log(Level, String)
     */
    public static void info(final @NotNull String message) {
        log(Level.INFO, message);
    }

    /**
     * Logs a message with the {@link Level#INFO} severity level
     *
     * @param message The component message
     * @see #log(Level, Component)
     */
    public static void info(final @NotNull Component message) {
        log(Level.INFO, message);
    }

    /**
     * Logs a message with the {@link Level#INFO} severity level and associated 
     * array of parameters
     *
     * @param message The string message
     * @param params  Array of parameters to the message
     * @see #log(Level, String, Object...)
     */
    public static void info(
            final @NotNull String message,
            final Object @NotNull ... params
    ) {
        log(Level.INFO, message, params);
    }

    /**
     * Logs a message with the {@link Level#INFO}  severity level and associated 
     * array of parameters
     *
     * @param message The component message
     * @param params  Array of parameters to the message
     * @see #log(Level, Component, Object...)
     */
    public static void info(
            final @NotNull Component message,
            final Object @NotNull ... params
    ) {
        log(Level.INFO, message, params);
    }

    /**
     * Logs a message with the {@link Level#INFO} severity level and associated 
     * throwable
     *
     * @param message   The string message
     * @param throwable Throwable associated with log message
     * @see #log(Level, String, Throwable)
     */
    public static void info(
            final @NotNull String message,
            final @NotNull Throwable throwable
    ) {
        log(Level.INFO, message, throwable);
    }

    /**
     * Logs a message with the {@link Level#INFO}  severity level and associated 
     * throwable
     *
     * @param message   The component message
     * @param throwable Throwable associated with log message
     * @see #log(Level, Component, Throwable)
     */
    public static void info(
            final @NotNull Component message,
            final @NotNull Throwable throwable
    ) {
        log(Level.INFO, message, throwable);
    }

    /**
     * Logs a message with the {@link Level#INFO} severity level to the 
     * specified target
     *
     * @param sender  The target to send the message to
     *                (null for console sender)
     * @param message The string message
     * @see #logChat(Level, CommandSender, String)
     */
    public static void info(
            final @Nullable CommandSender sender,
            final @NotNull String message
    ) {
        logChat(Level.INFO, sender, message);
    }

    /**
     * Logs a message with the {@link Level#INFO} severity level to the
     * specified target
     *
     * @param sender  The target to send the message to
     *                (null for console sender)
     * @param message The component message
     * @see #logChat(Level, CommandSender, Component)
     */
    public static void info(
            final @Nullable CommandSender sender,
            final @NotNull Component message
    ) {
        logChat(Level.INFO, sender, message);
    }

    /**
     * Logs a message with the {@link Level#FINE} severity level
     *
     * @param message The string message
     * @see #log(Level, String)
     */
    public static void fine(final @NotNull String message) {
        log(Level.FINE, message);
    }

    /**
     * Logs a message with the {@link Level#FINE} severity level
     *
     * @param message The component message
     * @see #log(Level, Component)
     */
    public static void fine(final @NotNull Component message) {
        log(Level.FINE, message);
    }

    /**
     * Logs a message with the {@link Level#FINE}  severity level and associated 
     * array of parameters
     *
     * @param message The string message
     * @param params  Array of parameters to the message
     * @see #log(Level, String, Object...)
     */
    public static void fine(
            final @NotNull String message,
            final Object @NotNull ... params
    ) {
        log(Level.FINE, message, params);
    }

    /**
     * Logs a message with the {@link Level#FINE} severity level and associated 
     * array of parameters
     *
     * @param message The component message
     * @param params  Array of parameters to the message
     * @see #log(Level, Component, Object...)
     */
    public static void fine(
            final @NotNull Component message,
            final Object @NotNull ... params
    ) {
        log(Level.FINE, message, params);
    }

    /**
     * Logs a message with the {@link Level#FINE} severity level and associated 
     * throwable
     *
     * @param message   The string message
     * @param throwable Throwable associated with log message
     * @see #log(Level, String, Throwable)
     */
    public static void fine(
            final @NotNull String message,
            final @NotNull Throwable throwable
    ) {
        log(Level.FINE, message, throwable);
    }

    /**
     * Logs a message with the {@link Level#FINE} severity level and associated 
     * throwable
     *
     * @param message   The component message
     * @param throwable Throwable associated with log message
     * @see #log(Level, Component, Throwable)
     */
    public static void fine(
            final @NotNull Component message,
            final @NotNull Throwable throwable
    ) {
        log(Level.FINE, message, throwable);
    }

    /**
     * Logs a message with the {@link Level#FINE} severity level to the 
     * specified target
     *
     * @param sender  The target to send the message to
     *                (null for console sender)
     * @param message The string message
     * @see #logChat(Level, CommandSender, String)
     */
    public static void fine(
            final @Nullable CommandSender sender,
            final @NotNull String message
    ) {
        logChat(Level.FINE, sender, message);
    }

    /**
     * Logs a message with the {@link Level#FINE} severity level to the
     * specified target
     *
     * @param sender  The target to send the message to
     *                (null for console sender)
     * @param message The component message
     * @see #logChat(Level, CommandSender, Component)
     */
    public static void fine(
            final @Nullable CommandSender sender,
            final @NotNull Component message
    ) {
        logChat(Level.FINE, sender, message);
    }

    /**
     * Serializes a component message to a string
     *
     * @param message The component message
     * @return The serialized string
     */
    private static @NotNull String serialize(final @NotNull Component message) {
        return PaperAdventure.ANSI_SERIALIZER.serialize(
                GlobalTranslator.render(
                        message,
                        Locale.getDefault()
                )
        );
    }
}
