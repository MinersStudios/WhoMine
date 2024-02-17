package com.minersstudios.msessentials.player;

import com.minersstudios.mscore.inventory.SignMenu;
import com.minersstudios.mscore.locale.LanguageFile;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.menu.PronounsMenu;
import com.minersstudios.msessentials.utility.MSPlayerUtils;
import com.minersstudios.msessentials.utility.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.minersstudios.mscore.locale.Translations.*;
import static net.kyori.adventure.text.Component.text;

/**
 * Registration process class. It is used to register a player on the server on
 * the first join. All messages stored in the {@link LanguageFile}.
 */
public final class RegistrationProcess {
    private final MSEssentials plugin;
    private Player player;
    private PlayerInfo playerInfo;
    private Location playerLocation;

    public RegistrationProcess(final @NotNull MSEssentials plugin) {
        this.plugin = plugin;
    }

    public void registerPlayer(final @NotNull PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
        this.player = playerInfo.getOnlinePlayer();

        if (this.player == null) {
            return;
        }

        this.playerLocation = this.player.getLocation();
        this.player.playSound(this.playerLocation, Sound.MUSIC_DISC_FAR, SoundCategory.MUSIC, 0.15f, 1.25f);
        playerInfo.createPlayerFile();

        this.sendDialogueMessage(REGISTRATION_MESSAGE_0.asTranslatable(), 100L);
        this.sendDialogueMessage(REGISTRATION_MESSAGE_1.asTranslatable(), 150L);
        this.sendDialogueMessage(REGISTRATION_MESSAGE_2.asTranslatable(), 225L);
        this.sendDialogueMessage(REGISTRATION_MESSAGE_3.asTranslatable(), 300L);
        this.sendDialogueMessage(REGISTRATION_MESSAGE_4.asTranslatable(), 350L);
        this.sendDialogueMessage(REGISTRATION_MESSAGE_5.asTranslatable(), 400L);
        this.sendDialogueMessage(REGISTRATION_MESSAGE_6.asTranslatable(), 450L);

        this.plugin.runTaskLater(this::setFirstname, 550L);
    }

    private void setFirstname() {
        final Locale locale = this.player.locale();

        new SignMenu(
                REGISTRATION_SIGN_FIRST_NAME_0.asComponent(locale),
                REGISTRATION_SIGN_FIRST_NAME_1.asComponent(locale),
                REGISTRATION_SIGN_FIRST_NAME_2.asComponent(locale),
                REGISTRATION_SIGN_FIRST_NAME_3.asComponent(locale),
                (player, strings) -> {
                    final String firstname = strings[0].trim();

                    if (!MSPlayerUtils.matchesNameRegex(firstname)) {
                        this.sendWarningMessage();
                        return false;
                    }

                    this.playerInfo.getPlayerFile().getPlayerName().setFirstName(firstname);

                    this.sendDialogueMessage(REGISTRATION_MESSAGE_7.asTranslatable(), 25L);
                    this.sendDialogueMessage(REGISTRATION_MESSAGE_8.asTranslatable(), 100L);
                    this.sendDialogueMessage(REGISTRATION_MESSAGE_9.asTranslatable(), 225L);
                    this.sendDialogueMessage(REGISTRATION_MESSAGE_10.asTranslatable(), 300L);

                    this.plugin.runTaskLater(this::setLastname, 375L);

                    return true;
                }
        ).open(this.player);
    }

    private void setLastname() {
        final Locale locale = this.player.locale();

        new SignMenu(
                REGISTRATION_SIGN_LAST_NAME_0.asComponent(locale),
                REGISTRATION_SIGN_LAST_NAME_1.asComponent(locale),
                REGISTRATION_SIGN_LAST_NAME_2.asComponent(locale),
                REGISTRATION_SIGN_LAST_NAME_3.asComponent(locale),
                (player, strings) -> {
                    final String lastname = strings[0].trim();

                    if (!MSPlayerUtils.matchesNameRegex(lastname)) {
                        this.sendWarningMessage();
                        return false;
                    }

                    this.playerInfo.getPlayerFile().getPlayerName().setLastName(lastname);
                    this.plugin.runTaskLater(this::setPatronymic, 10L);

                    return true;
                }
        ).open(this.player);
    }

    private void setPatronymic() {
        final Locale locale = this.player.locale();

        new SignMenu(
                REGISTRATION_SIGN_PATRONYMIC_0.asComponent(locale),
                REGISTRATION_SIGN_PATRONYMIC_1.asComponent(locale),
                REGISTRATION_SIGN_PATRONYMIC_2.asComponent(locale),
                REGISTRATION_SIGN_PATRONYMIC_3.asComponent(locale),
                (player, strings) -> {
                    final String patronymic = strings[0].trim();

                    if (!MSPlayerUtils.matchesNameRegex(patronymic)) {
                        this.sendWarningMessage();
                        return false;
                    }

                    final PlayerFile playerFile = this.playerInfo.getPlayerFile();
                    final PlayerName name = playerFile.getPlayerName();

                    name.setPatronymic(patronymic);
                    playerFile.updateName();
                    playerFile.save();
                    this.playerInfo.initNames();

                    this.sendDialogueMessage(
                            REGISTRATION_MESSAGE_11.asTranslatable().arguments(
                                    text(this.playerInfo.getID(true, false)),
                                    text(name.getFirstName()),
                                    text(name.getLastName()),
                                    text(name.getPatronymic())
                            ),
                            25L
                    );
                    this.sendDialogueMessage(REGISTRATION_MESSAGE_12.asTranslatable(), 100L);
                    this.sendDialogueMessage(REGISTRATION_MESSAGE_13.asTranslatable(), 150L);

                    this.plugin.runTaskLater(
                            () -> this.plugin.openCustomInventory(PronounsMenu.class, this.player),
                            225L
                    );

                    return true;
                }
        ).open(this.player);
    }

    public void setPronouns(
            final @NotNull Player player,
            final @NotNull PlayerInfo playerInfo
    ) {
        this.player = player;
        this.playerLocation = player.getLocation();
        this.playerInfo = playerInfo;
        final Pronouns pronouns = this.playerInfo.getPlayerFile().getPronouns();

        this.sendDialogueMessage(REGISTRATION_MESSAGE_14.asTranslatable(), 25L);
        this.sendDialogueMessage(REGISTRATION_MESSAGE_15.asTranslatable(), 75L);
        this.sendDialogueMessage(REGISTRATION_MESSAGE_16.asTranslatable(), 125L);
        this.sendDialogueMessage(
                REGISTRATION_MESSAGE_17.asTranslatable()
                .arguments(
                        pronouns.getPronouns(),
                        pronouns.getTraveler()
                ),
                175L
        );

        this.plugin.runTaskLater(this::setOther, 225L);
    }

    private void setOther() {
        if (this.player.isOnline()) {
            this.player.displayName(this.playerInfo.getDefaultName());
            this.playerInfo.handleJoin();
        }
    }

    private void sendWarningMessage() {
        this.player.sendMessage(
                REGISTRATION_ONLY_CYRILLIC.asTranslatable()
                .color(NamedTextColor.GOLD)
        );
    }

    private void sendDialogueMessage(
            final @NotNull Component message,
            final long delay
    ) {
        this.plugin.runTaskLater(() -> {
            this.player.sendMessage(
                    CHAT_LOCAL_FORMAT.asTranslatable()
                    .arguments(
                            REGISTRATION_ANONYMOUS_NAME.asTranslatable(),
                            message.color(MessageUtils.Colors.CHAT_COLOR_SECONDARY)
                    )
                    .color(MessageUtils.Colors.CHAT_COLOR_PRIMARY)
            );
            this.player.playSound(
                    this.playerLocation,
                    Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF,
                    SoundCategory.PLAYERS,
                    0.5f,
                    1.5f
            );
        }, delay);
    }
}
