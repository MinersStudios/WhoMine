package com.minersstudios.msessentials.player;

import com.minersstudios.mscore.inventory.SignMenu;
import com.minersstudios.mscore.language.LanguageFile;
import com.minersstudios.mscore.language.LanguageRegistry;
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

import static net.kyori.adventure.text.Component.text;

/**
 * Registration process class.
 * It is used to register a player on the server on the first join.
 * All messages stored in the {@link LanguageFile}.
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

        this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_0, 100L);
        this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_1, 150L);
        this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_2, 225L);
        this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_3, 300L);
        this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_4, 350L);
        this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_5, 400L);
        this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_6, 450L);

        this.plugin.runTaskLater(this::setFirstname, 550L);
    }

    private void setFirstname() {
        new SignMenu(
                LanguageRegistry.Components.REGISTRATION_SIGN_FIRST_NAME_0,
                LanguageRegistry.Components.REGISTRATION_SIGN_FIRST_NAME_1,
                LanguageRegistry.Components.REGISTRATION_SIGN_FIRST_NAME_2,
                LanguageRegistry.Components.REGISTRATION_SIGN_FIRST_NAME_3,
                (player, strings) -> {
                    final String firstname = strings[0].trim();

                    if (!MSPlayerUtils.matchesNameRegex(firstname)) {
                        this.sendWarningMessage();
                        return false;
                    }

                    this.playerInfo.getPlayerFile().getPlayerName().setFirstName(firstname);

                    this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_7, 25L);
                    this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_8, 100L);
                    this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_9, 225L);
                    this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_10, 300L);

                    this.plugin.runTaskLater(this::setLastname, 375L);

                    return true;
                }
        ).open(this.player);
    }

    private void setLastname() {
        new SignMenu(
                LanguageRegistry.Components.REGISTRATION_SIGN_LAST_NAME_0,
                LanguageRegistry.Components.REGISTRATION_SIGN_LAST_NAME_1,
                LanguageRegistry.Components.REGISTRATION_SIGN_LAST_NAME_2,
                LanguageRegistry.Components.REGISTRATION_SIGN_LAST_NAME_3,
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
        new SignMenu(
                LanguageRegistry.Components.REGISTRATION_SIGN_PATRONYMIC_0,
                LanguageRegistry.Components.REGISTRATION_SIGN_PATRONYMIC_1,
                LanguageRegistry.Components.REGISTRATION_SIGN_PATRONYMIC_2,
                LanguageRegistry.Components.REGISTRATION_SIGN_PATRONYMIC_3,
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
                            LanguageRegistry.Components.REGISTRATION_MESSAGE_11.args(
                                    text(this.playerInfo.getID(true, false)),
                                    text(name.getFirstName()),
                                    text(name.getLastName()),
                                    text(name.getPatronymic())
                            ),
                            25L
                    );
                    this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_12, 100L);
                    this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_13, 150L);

                    this.plugin.runTaskLater(() -> PronounsMenu.open(this.player), 225L);

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

        this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_14, 25L);
        this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_15, 75L);
        this.sendDialogueMessage(LanguageRegistry.Components.REGISTRATION_MESSAGE_16, 125L);
        this.sendDialogueMessage(
                LanguageRegistry.Components.REGISTRATION_MESSAGE_17
                .args(
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
                LanguageRegistry.Components.REGISTRATION_ONLY_CYRILLIC
                        .color(NamedTextColor.GOLD)
        );
    }

    private void sendDialogueMessage(
            final @NotNull Component message,
            final long delay
    ) {
        this.plugin.runTaskLater(() -> {
            this.player.sendMessage(
                    LanguageRegistry.Components.CHAT_LOCAL_FORMAT
                    .args(
                            LanguageRegistry.Components.REGISTRATION_ANONYMOUS_NAME,
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
