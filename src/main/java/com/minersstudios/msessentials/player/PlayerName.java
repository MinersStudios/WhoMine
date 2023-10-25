package com.minersstudios.msessentials.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.mscore.util.ChatUtils.normalize;
import static com.minersstudios.msessentials.util.MessageUtils.Colors.*;
import static net.kyori.adventure.text.Component.text;

/**
 * Player name class with nickname, first name, last name and patronymic.
 * Name data stored in the {@link PlayerFile}.
 *
 * @see PlayerFile
 */
public final class PlayerName {
    private @NotNull String nickname;
    private @NotNull String firstName;
    private @NotNull String lastName;
    private @NotNull String patronymic;

    private PlayerName(
            final @NotNull String nickname,
            final @NotNull String firstName,
            final @NotNull String lastName,
            final @NotNull String patronymic
    ) throws IllegalArgumentException {
        if (nickname.isBlank()) {
            throw new IllegalArgumentException("Nickname cannot be blank");
        }

        if (firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }

        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
    }

    /**
     * Creates a new {@link PlayerName} instance with normalized names
     *
     * @param nickname   The player's nickname
     * @param firstName  The player's first name
     * @param lastName   The player's last name, can be blank
     * @param patronymic The player's patronymic, can be blank
     * @return The new {@link PlayerName} instance
     * @throws IllegalArgumentException If the nickname or first name is blank
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull PlayerName create(
            final @NotNull String nickname,
            final @NotNull String firstName,
            final @NotNull String lastName,
            final @NotNull String patronymic
    ) throws IllegalArgumentException {
        return new PlayerName(
                nickname,
                normalize(firstName),
                normalize(lastName),
                normalize(patronymic)
        );
    }

    /**
     * @return The player's nickname
     */
    public @NotNull String getNickname() {
        return this.nickname;
    }

    /**
     * Sets the player's nickname
     *
     * @param nickname New player's nickname
     * @throws IllegalArgumentException If the nickname is blank
     */
    public void setNickname(final @NotNull String nickname) throws IllegalArgumentException {
        if (nickname.isBlank()) {
            throw new IllegalArgumentException("Nickname cannot be blank");
        }

        this.nickname = nickname;
    }

    /**
     * @return The player's first name
     */
    public @NotNull String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets the player's first name
     *
     * @param firstName New player's first name
     * @throws IllegalArgumentException If the first name is blank
     */
    public void setFirstName(final @NotNull String firstName) throws IllegalArgumentException {
        if (firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }

        this.firstName = normalize(firstName);
    }

    /**
     * @return The player's last name, can be empty
     */
    public @NotNull String getLastName() {
        return this.lastName;
    }

    /**
     * Sets the player's last name
     *
     * @param lastName New player's last name, can be empty
     */
    public void setLastName(final @NotNull String lastName) {
        this.lastName = normalize(lastName);
    }

    /**
     * @return The player's patronymic, can be empty
     */
    public @NotNull String getPatronymic() {
        return this.patronymic;
    }

    /**
     * Sets the player's patronymic
     *
     * @param patronymic New player's patronymic, can be empty
     */
    public void setPatronymic(final @NotNull String patronymic) {
        this.patronymic = normalize(patronymic);
    }

    /**
     * Creates a {@link Component} with the player's ID, first name and last name
     *
     * @param id     The player's ID
     * @param first  The id color
     * @param second The name color
     * @return The player's ID, first name and last name as a {@link Component}
     */
    @Contract("_, _, _ -> new")
    public @NotNull Component createName(
            final int id,
            final @Nullable TextColor first,
            final @Nullable TextColor second
    ) {
        return text("[")
                .append(text(id)
                .append(text("] ")))
                .color(first)
                .append(text(this.getFirstName())
                .append(this.getLastName().isEmpty() ? Component.empty() : Component.space().append(text(this.getLastName())))
                .color(second));
    }

    /**
     * Creates a {@link Component} with the player's ID, first name, last name and patronymic
     *
     * @param id     The player's ID
     * @param first  The id color
     * @param second The name color
     * @return The player's ID, first name, last name and patronymic as a {@link Component}
     */
    @Contract("_, _, _ -> new")
    public @NotNull Component createFullName(
            final int id,
            final @Nullable TextColor first,
            final @Nullable TextColor second
    ) {
        return text("[")
                .append(text(id)
                .append(text("] ")))
                .color(first)
                .append(text(this.getFirstName())
                .append(this.getLastName().isBlank() ? Component.empty() : Component.space().append(text(this.getLastName())))
                .append(this.getPatronymic().isBlank() ? Component.empty() : Component.space().append(text(this.getPatronymic())))
                .color(second));
    }

    /**
     * Creates a name with default colors
     *
     * @param id The player's ID
     * @return The player's ID, first name and last name as a {@link Component} with default colors
     */
    public @NotNull Component createDefaultName(final int id) {
        return this.createName(id, null, null);
    }

    /**
     * Creates a name with gold colors
     *
     * @param id The player's ID
     * @return The player's ID, first name and last name as a {@link Component} with gold colors
     */
    public @NotNull Component createGoldenName(final int id) {
        return this.createName(id, JOIN_MESSAGE_COLOR_SECONDARY, JOIN_MESSAGE_COLOR_PRIMARY);
    }

    /**
     * Creates a name with gray and gold colors
     *
     * @param id The player's ID
     * @return The player's ID, first name and last name as a {@link Component} with gray and gold colors
     */
    public @NotNull Component createGrayIDGoldName(final int id) {
        return this.createName(id, NamedTextColor.GRAY, RP_MESSAGE_MESSAGE_COLOR_PRIMARY);
    }

    /**
     * Creates a name with gray and green colors
     *
     * @param id The player's ID
     * @return The player's ID, first name and last name as a {@link Component} with gray and green colors
     */
    public @NotNull Component createGrayIDGreenName(final int id) {
        return this.createName(id, NamedTextColor.GRAY, NamedTextColor.GREEN);
    }
}
