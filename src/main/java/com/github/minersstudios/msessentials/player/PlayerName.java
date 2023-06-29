package com.github.minersstudios.msessentials.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static com.github.minersstudios.msessentials.utils.MessageUtils.Colors.*;
import static net.kyori.adventure.text.Component.text;

public class PlayerName {
    private @NotNull String nickname;
    private @NotNull String firstName;
    private @NotNull String lastName;
    private @NotNull String patronymic;

    protected PlayerName(
            @NotNull String nickname,
            @NotNull String firstName,
            @NotNull String lastName,
            @NotNull String patronymic
    ) {
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull PlayerName create(
            @NotNull String nickname,
            @NotNull String firstName,
            @NotNull String lastName,
            @NotNull String patronymic
    ) {
        return new PlayerName(
                nickname,
                normalize(firstName),
                normalize(lastName),
                normalize(patronymic)
        );
    }

    public @NotNull Component createName(
            int id,
            @Nullable TextColor first,
            @Nullable TextColor second
    ) {
        return text("[")
                .append(text(id)
                .append(text("] ")))
                .color(first)
                .append(text(this.getFirstName())
                .append(this.getLastName().isEmpty() ? Component.empty() : Component.space()
                .append(text(this.getLastName())))
                .color(second));
    }

    public @NotNull Component createDefaultName(int id) {
        return this.createName(id, null, null);
    }

    public @NotNull Component createGoldenName(int id) {
        return this.createName(id, JOIN_MESSAGE_COLOR_SECONDARY, JOIN_MESSAGE_COLOR_PRIMARY);
    }

    public @NotNull Component createGrayIDGoldName(int id) {
        return this.createName(id, NamedTextColor.GRAY, RP_MESSAGE_MESSAGE_COLOR_PRIMARY);
    }

    public @NotNull Component createGrayIDGreenName(int id) {
        return this.createName(id, NamedTextColor.GRAY, NamedTextColor.GREEN);
    }

    public @NotNull String getNickname() {
        return this.nickname;
    }

    public void setNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    public @NotNull String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(@NotNull String firstName) {
        this.firstName = normalize(firstName);
    }

    public @NotNull String getLastName() {
        return this.lastName;
    }

    public void setLastName(@NotNull String lastName) {
        this.lastName = normalize(lastName);
    }

    public @NotNull String getPatronymic() {
        return this.patronymic;
    }

    public void setPatronymic(@NotNull String patronymic) {
        this.patronymic = normalize(patronymic);
    }

    private static @NotNull String normalize(@NotNull String string) {
        if (string.isEmpty()) return string;
        String firstLetter = string.substring(0, 1).toUpperCase(Locale.ROOT);
        String other = string.substring(1).toLowerCase(Locale.ROOT);
        return firstLetter + other;
    }
}
