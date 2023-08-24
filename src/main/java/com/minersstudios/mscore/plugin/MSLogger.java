package com.minersstudios.mscore.plugin;

import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.util.Badges;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

/**
 * A custom logging utility designed to send formatted log messages
 * with different levels and colors to various targets. Supports
 * logging to the console, players, and command senders using the
 * AdventureAPI for text formatting.
 */
public final class MSLogger {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private static final ComponentLogger COMPONENT_LOGGER = ComponentLogger.logger(LOGGER.getName());

    private MSLogger() {
        throw new AssertionError("This class cannot be instantiated!");
    }

    /**
     * Logs a message with the specified severity level
     *
     * @param level   One of the message level identifiers, e.g., SEVERE
     * @param message The string message (or a key in the message catalog)
     * @see Logger#log(Level, String)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull String message
    ) {
        LOGGER.log(level, message);
    }

    /**
     * Log a message with the specified severity level
     * and associated array of parameters
     *
     * @param level   One of the message level identifiers,
     *                e.g., SEVERE
     * @param message The string message
     *                (or a key in the message catalog)
     * @param params  Array of parameters to the message
     * @see Logger#log(Level, String, Object[])
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull String message,
            final @Nullable Object @NotNull ... params
    ) {
        LOGGER.log(level, message, params);
    }

    /**
     * Logs a message with the specified severity level
     * and associated throwable
     *
     * @param level     One of the message level identifiers,
     *                  e.g., SEVERE
     * @param message   The string message
     *                  (or a key in the message catalog)
     * @param throwable Throwable associated with log message
     * @see Logger#log(Level, String, Throwable)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull String message,
            final @NotNull Throwable throwable
    ) {
        LOGGER.log(level, message, throwable);
    }

    /**
     * Logs a message with the specified severity level
     *
     * @param level   One of the message level identifiers,
     *                e.g., SEVERE
     * @param message The log message as {@link Component}
     * @see ComponentLogger#error(Component)
     * @see ComponentLogger#warn(Component)
     * @see ComponentLogger#info(Component)
     * @see ComponentLogger#debug(Component)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull Component message
    ) {
        switch (level.intValue()) {
            case 1000 -> COMPONENT_LOGGER.error(message);
            case 900 -> COMPONENT_LOGGER.warn(message);
            case 800 -> COMPONENT_LOGGER.info(message);
            default -> COMPONENT_LOGGER.debug(message);
        }
    }

    /**
     * Logs a message with the specified severity level
     * and array of object arguments
     *
     * @param level   One of the message level identifiers,
     *                e.g., SEVERE
     * @param message The log message as {@link Component}
     * @param params  Array of parameters to the message
     * @see ComponentLogger#error(Component, Object...)
     * @see ComponentLogger#warn(Component, Object...)
     * @see ComponentLogger#info(Component, Object...)
     * @see ComponentLogger#debug(Component, Object...)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull Component message,
            final @Nullable Object @NotNull ... params
    ) {
        switch (level.intValue()) {
            case 1000 -> COMPONENT_LOGGER.error(message, params);
            case 900 -> COMPONENT_LOGGER.warn(message, params);
            case 800 -> COMPONENT_LOGGER.info(message, params);
            default -> COMPONENT_LOGGER.debug(message, params);
        }
    }

    /**
     * Logs a message with the specified severity level
     * and associated throwable
     *
     * @param level     One of the message level identifiers,
     *                  e.g., SEVERE
     * @param message   The log message as {@link Component}
     * @param throwable Throwable associated with log message
     * @see ComponentLogger#error(Component, Object...)
     * @see ComponentLogger#warn(Component, Object...)
     * @see ComponentLogger#info(Component, Object...)
     * @see ComponentLogger#debug(Component, Object...)
     */
    public static void log(
            final @NotNull Level level,
            final @NotNull Component message,
            final @NotNull Throwable throwable
    ) {
        switch (level.intValue()) {
            case 1000 -> COMPONENT_LOGGER.error(message, throwable);
            case 900 -> COMPONENT_LOGGER.warn(message, throwable);
            case 800 -> COMPONENT_LOGGER.info(message, throwable);
            default -> COMPONENT_LOGGER.debug(message, throwable);
        }
    }

    /**
     * Sends string message to console with {@link Level#INFO}
     *
     * @param message Info message {@link String}
     * @see #info(Object, Component)
     */
    public static void info(final @NotNull String message) {
        info(null, message);
    }

    /**
     * Sends string message to target with {@link Level#INFO}
     *
     * @param target  Target, if null sends to console
     * @param message Info message as {@link String}
     * @see #info(Object, Component)
     */
    public static void info(
            final @Nullable Object target,
            final @NotNull String message
    ) {
        info(target, text(message));
    }

    /**
     * Sends component message to console with {@link Level#INFO}
     *
     * @param message Info message as {@link Component}
     * @see #info(Object, Component)
     */
    public static void info(final @NotNull Component message) {
        info(null, message);
    }

    /**
     * Sends component message to target with {@link Level#INFO}
     * If target is null or is not a {@link CommandSender}, sends to console.
     *
     * @param target  Target, if null sends to console
     * @param message Info message {@link Component}
     * @see #log(Level, Component)
     */
    public static void info(
            final @Nullable Object target,
            final @NotNull Component message
    ) {
        if (
                target instanceof ConsoleCommandSender
                || target == null
        ) {
            log(Level.INFO, message);
        } else if (target instanceof BlockCommandSender sender) {
            sender.sendMessage(
                    message instanceof TranslatableComponent translatableComponent
                    ? LanguageFile.renderTranslationComponent(translatableComponent)
                    : message
            );
        } else if (target instanceof final CommandSender sender) {
            sender.sendMessage(text(" ").append(message));
        } else {
            throw new IllegalArgumentException("Target must be a CommandSender or null!");
        }
    }

    /**
     * Sends string message to console with {@link Level#INFO}
     * but with green color
     *
     * @param message Fine message as {@link String}
     * @see #fine(Object, Component)
     */
    public static void fine(final @NotNull String message) {
        fine(null, message);
    }

    /**
     * Sends string message to target with {@link Level#INFO}
     * but with green color
     *
     * @param target  Target, if null sends to console
     * @param message Fine message as {@link String}
     * @see #fine(Object, Component)
     */
    public static void fine(
            final @Nullable Object target,
            final @NotNull String message
    ) {
        fine(target, text(message));
    }

    /**
     * Sends component message to console with {@link Level#INFO}
     * but with green color
     *
     * @param message Fine message as {@link Component}
     * @see #fine(Object, Component)
     */
    public static void fine(final @NotNull Component message) {
        fine(null, message);
    }

    /**
     * Sends component message to target with {@link Level#INFO}
     * but with green color. If target is null or is not a
     * {@link CommandSender}, sends to console.
     * If target is a {@link CommandSender}, message will be sent with
     * {@link Badges#GREEN_EXCLAMATION_MARK} prefix and
     * {@link NamedTextColor#GREEN} color.
     *
     * @param target  Target, if null sends to console
     * @param message Fine message as {@link Component}
     * @see #log(Level, Component)
     */
    public static void fine(
            final @Nullable Object target,
            final @NotNull Component message
    ) {
        if (
                target instanceof ConsoleCommandSender
                || target == null
        ) {
            log(Level.INFO, message.color(GREEN));
        } else if (target instanceof BlockCommandSender sender) {
            sender.sendMessage(
                    message instanceof TranslatableComponent translatableComponent
                    ? LanguageFile.renderTranslationComponent(translatableComponent)
                    : message
            );
        } else if (target instanceof final CommandSender sender) {
            sender.sendMessage(Badges.GREEN_EXCLAMATION_MARK.append(message.color(GREEN)));
        } else {
            throw new IllegalArgumentException("Target must be a CommandSender or null!");
        }
    }

    /**
     * Sends string message to console with {@link Level#WARNING}
     *
     * @param message Warning message as {@link String}
     * @see #warning(Object, Component)
     */
    public static void warning(final @NotNull String message) {
        warning(null, message);
    }

    /**
     * Sends string message to target with {@link Level#WARNING}
     *
     * @param target  Target, if null sends to console
     * @param message Warning message as {@link String}
     * @see #warning(Object, Component)
     */
    public static void warning(
            final @Nullable Object target,
            final @NotNull String message
    ) {
        warning(target, text(message));
    }

    /**
     * Sends component message to console with {@link Level#WARNING}
     *
     * @param message Warning message as {@link Component}
     * @see #warning(Object, Component)
     */
    public static void warning(final @NotNull Component message) {
        warning(null, message);
    }

    /**
     * Sends component message to target with {@link Level#WARNING}
     * If target is null or is not a {@link CommandSender}, sends to console.
     * If target is a {@link CommandSender}, message will be sent with
     * {@link Badges#YELLOW_EXCLAMATION_MARK} prefix and
     * {@link NamedTextColor#GOLD} color.
     *
     * @param target  Target, if null sends to console
     * @param message Warning message as {@link Component}
     * @see #log(Level, Component)
     */
    public static void warning(
            final @Nullable Object target,
            final @NotNull Component message
    ) {
        if (
                target instanceof ConsoleCommandSender
                || target == null
        ) {
            log(Level.WARNING, message);
        } else if (target instanceof BlockCommandSender sender) {
            sender.sendMessage(
                    message instanceof TranslatableComponent translatableComponent
                    ? LanguageFile.renderTranslationComponent(translatableComponent)
                    : message
            );
        } else if (target instanceof final CommandSender sender) {
            sender.sendMessage(Badges.YELLOW_EXCLAMATION_MARK.append(message.color(GOLD)));
        } else {
            throw new IllegalArgumentException("Target must be a CommandSender or null!");
        }
    }

    /**
     * Sends string message to console with {@link Level#SEVERE}
     *
     * @param message Error message as {@link String}
     * @see #severe(Object, Component)
     */
    public static void severe(final @NotNull String message) {
        severe(null, message);
    }

    /**
     * Sends string message to target with {@link Level#SEVERE}
     *
     * @param target  Target, if null sends to console
     * @param message Error message {@link String}
     * @see #severe(Object, Component)
     */
    public static void severe(
            final @Nullable Object target,
            final @NotNull String message
    ) {
        severe(target, text(message));
    }

    /**
     * Sends component message to console with {@link Level#SEVERE}
     *
     * @param message Error message as {@link Component}
     * @see #severe(Object, Component)
     */
    public static void severe(final @NotNull Component message) {
        severe(null, message);
    }

    /**
     * Sends component message to target with {@link Level#SEVERE}
     * If target is null or is not a {@link CommandSender}, sends to console.
     * If target is a {@link CommandSender}, message will be sent with
     * {@link Badges#RED_EXCLAMATION_MARK} prefix and
     * {@link NamedTextColor#RED} color.
     *
     * @param target  Target, if null sends to console
     * @param message Error message as {@link Component}
     * @see #log(Level, Component)
     */
    public static void severe(
            final @Nullable Object target,
            final @NotNull Component message
    ) {
        if (
                target instanceof ConsoleCommandSender
                || target == null
        ) {
            log(Level.SEVERE, message);
        } else if (target instanceof BlockCommandSender sender) {
            sender.sendMessage(
                    message instanceof TranslatableComponent translatableComponent
                    ? LanguageFile.renderTranslationComponent(translatableComponent)
                    : message
            );
        } else if (target instanceof final CommandSender sender) {
            sender.sendMessage(Badges.RED_EXCLAMATION_MARK.append(message.color(RED)));
        } else {
            throw new IllegalArgumentException("Target must be a CommandSender or null!");
        }
    }
}
