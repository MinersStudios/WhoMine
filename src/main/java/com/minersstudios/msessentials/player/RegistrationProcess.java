package com.minersstudios.msessentials.player;

import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.util.SignMenu;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.menu.PronounsMenu;
import com.minersstudios.msessentials.utils.MSPlayerUtils;
import com.minersstudios.msessentials.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

/**
 * Registration process class.
 * It is used to register a player on the server on the first join.
 * All messages stored in the {@link LanguageFile}.
 */
public class RegistrationProcess {
    private Player player;
    private PlayerInfo playerInfo;
    private Location playerLocation;

    private static final TranslatableComponent M_0 = translatable("ms.registration.message.0");
    private static final TranslatableComponent M_1 = translatable("ms.registration.message.1");
    private static final TranslatableComponent M_2 = translatable("ms.registration.message.2");
    private static final TranslatableComponent M_3 = translatable("ms.registration.message.3");
    private static final TranslatableComponent M_4 = translatable("ms.registration.message.4");
    private static final TranslatableComponent M_5 = translatable("ms.registration.message.5");
    private static final TranslatableComponent M_6 = translatable("ms.registration.message.6");
    private static final TranslatableComponent M_7 = translatable("ms.registration.message.7");
    private static final TranslatableComponent M_8 = translatable("ms.registration.message.8");
    private static final TranslatableComponent M_9 = translatable("ms.registration.message.9");
    private static final TranslatableComponent M_10 = translatable("ms.registration.message.10");
    private static final TranslatableComponent M_11 = translatable("ms.registration.message.11");
    private static final TranslatableComponent M_12 = translatable("ms.registration.message.12");
    private static final TranslatableComponent M_13 = translatable("ms.registration.message.13");
    private static final TranslatableComponent M_14 = translatable("ms.registration.message.14");
    private static final TranslatableComponent M_15 = translatable("ms.registration.message.15");
    private static final TranslatableComponent M_16 = translatable("ms.registration.message.16");
    private static final TranslatableComponent M_17 = translatable("ms.registration.message.17");
    private static final TranslatableComponent ONLY_CYRILLIC = translatable("ms.registration.only_cyrillic", NamedTextColor.GOLD);
    private static final TranslatableComponent LOCAL_FORMAT = translatable("ms.chat.local.format");
    private static final TranslatableComponent ANONYMOUS_NAME = translatable("ms.registration.anonymous.name");

    public void registerPlayer(@NotNull PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
        this.player = playerInfo.getOnlinePlayer();

        if (this.player == null) return;

        this.playerLocation = this.player.getLocation();
        this.player.playSound(this.playerLocation, Sound.MUSIC_DISC_FAR, SoundCategory.MUSIC, 0.15f, 1.25f);
        playerInfo.createPlayerFile();

        this.sendDialogueMessage(M_0, 100L);
        this.sendDialogueMessage(M_1, 150L);
        this.sendDialogueMessage(M_2, 225L);
        this.sendDialogueMessage(M_3, 300L);
        this.sendDialogueMessage(M_4, 350L);
        this.sendDialogueMessage(M_5, 400L);
        this.sendDialogueMessage(M_6, 450L);

        MSEssentials.getInstance().runTaskLater(this::setFirstname, 550L);
    }

    private void setFirstname() {
        SignMenu.create(
                LanguageFile.renderTranslationComponent("ms.registration.sign.first_name.0"),
                LanguageFile.renderTranslationComponent("ms.registration.sign.first_name.1"),
                LanguageFile.renderTranslationComponent("ms.registration.sign.first_name.2"),
                LanguageFile.renderTranslationComponent("ms.registration.sign.first_name.3"),
                (player, strings) -> {
                    String firstname = strings[0].trim();

                    if (!MSPlayerUtils.matchesNameRegex(firstname)) {
                        this.sendWarningMessage();
                        return false;
                    }

                    this.playerInfo.getPlayerFile().getPlayerName().setFirstName(firstname);

                    this.sendDialogueMessage(M_7, 25L);
                    this.sendDialogueMessage(M_8, 100L);
                    this.sendDialogueMessage(M_9, 225L);
                    this.sendDialogueMessage(M_10, 300L);

                    MSEssentials.getInstance().runTaskLater(this::setLastname, 375L);
                    return true;
                }).open(this.player);
    }

    private void setLastname() {
        SignMenu.create(
                LanguageFile.renderTranslationComponent("ms.registration.sign.last_name.0"),
                LanguageFile.renderTranslationComponent("ms.registration.sign.last_name.1"),
                LanguageFile.renderTranslationComponent("ms.registration.sign.last_name.2"),
                LanguageFile.renderTranslationComponent("ms.registration.sign.last_name.3"),
                (player, strings) -> {
                    String lastname = strings[0].trim();

                    if (!MSPlayerUtils.matchesNameRegex(lastname)) {
                        this.sendWarningMessage();
                        return false;
                    }

                    this.playerInfo.getPlayerFile().getPlayerName().setLastName(lastname);
                    MSEssentials.getInstance().runTaskLater(this::setPatronymic, 10L);
                    return true;
                }).open(this.player);
    }

    private void setPatronymic() {
        SignMenu.create(
                LanguageFile.renderTranslationComponent("ms.registration.sign.patronymic.0"),
                LanguageFile.renderTranslationComponent("ms.registration.sign.patronymic.1"),
                LanguageFile.renderTranslationComponent("ms.registration.sign.patronymic.2"),
                LanguageFile.renderTranslationComponent("ms.registration.sign.patronymic.3"),
                (player, strings) -> {
                    String patronymic = strings[0].trim();

                    if (!MSPlayerUtils.matchesNameRegex(patronymic)) {
                        this.sendWarningMessage();
                        return false;
                    }

                    PlayerFile playerFile = this.playerInfo.getPlayerFile();
                    PlayerName name = playerFile.getPlayerName();

                    name.setPatronymic(patronymic);
                    playerFile.updateName();
                    playerFile.save();
                    this.playerInfo.initNames();

                    this.sendDialogueMessage(
                            M_11.args(
                                    text(this.playerInfo.getID(true, false)),
                                    text(name.getFirstName()),
                                    text(name.getLastName()),
                                    text(name.getPatronymic())
                            ),
                            25L
                    );
                    this.sendDialogueMessage(M_12, 100L);
                    this.sendDialogueMessage(M_13, 150L);

                    MSEssentials.getInstance().runTaskLater(() -> PronounsMenu.open(this.player), 225L);
                    return true;
                }).open(this.player);
    }

    public void setPronouns(
            @NotNull Player player,
            @NotNull PlayerInfo playerInfo
    ) {
        this.player = player;
        this.playerLocation = player.getLocation();
        this.playerInfo = playerInfo;
        Pronouns pronouns = this.playerInfo.getPlayerFile().getPronouns();

        this.sendDialogueMessage(M_14, 25L);
        this.sendDialogueMessage(M_15, 75L);
        this.sendDialogueMessage(M_16, 125L);
        this.sendDialogueMessage(
                M_17.args(
                        pronouns.getPronouns(),
                        pronouns.getTraveler()
                ),
                175L
        );

        MSEssentials.getInstance().runTaskLater(this::setOther, 225L);
    }

    private void setOther() {
        if (this.player.isOnline()) {
            this.player.displayName(this.playerInfo.getDefaultName());
            this.playerInfo.handleJoin();
        }
    }

    private void sendWarningMessage() {
        this.player.sendMessage(ONLY_CYRILLIC);
    }

    private void sendDialogueMessage(
            @NotNull Component message,
            long delay
    ) {
        MSEssentials.getInstance().runTaskLater(() -> {
            this.player.sendMessage(
                    LOCAL_FORMAT.args(
                            ANONYMOUS_NAME,
                            message.color(MessageUtils.Colors.CHAT_COLOR_SECONDARY)
                    ).color(MessageUtils.Colors.CHAT_COLOR_PRIMARY)
            );
            this.player.playSound(this.playerLocation, Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.PLAYERS, 0.5f, 1.5f);
        }, delay);
    }
}
