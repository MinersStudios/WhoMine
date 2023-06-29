package com.github.minersstudios.msessentials.player;

import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.menu.PronounsMenu;
import com.github.minersstudios.msessentials.menu.ResourcePackMenu;
import com.github.minersstudios.msessentials.utils.MSPlayerUtils;
import com.github.minersstudios.msessentials.utils.MessageUtils;
import com.github.minersstudios.msessentials.utils.SignMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslationComponent;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class RegistrationProcess {
    private Player player;
    private PlayerInfo playerInfo;
    private Location playerLocation;

    public void registerPlayer(@NotNull PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
        this.player = playerInfo.getOnlinePlayer();

        if (this.player == null) return;

        this.playerLocation = this.player.getLocation();
        this.player.playSound(this.playerLocation, Sound.MUSIC_DISC_FAR, SoundCategory.MUSIC, 0.15f, 1.25f);
        playerInfo.createPlayerFile();

        this.sendDialogueMessage(translatable("ms.registration.message.0"), 100L);
        this.sendDialogueMessage(translatable("ms.registration.message.1"), 150L);
        this.sendDialogueMessage(translatable("ms.registration.message.2"), 225L);
        this.sendDialogueMessage(translatable("ms.registration.message.3"), 300L);
        this.sendDialogueMessage(translatable("ms.registration.message.4"), 350L);
        this.sendDialogueMessage(translatable("ms.registration.message.5"), 400L);
        this.sendDialogueMessage(translatable("ms.registration.message.6"), 450L);

        Bukkit.getScheduler().runTaskLater(MSEssentials.getInstance(), this::setFirstname, 550L);
    }

    private void setFirstname() {
        SignMenu.create(
                renderTranslationComponent("ms.registration.sign.first_name.0"),
                renderTranslationComponent("ms.registration.sign.first_name.1"),
                renderTranslationComponent("ms.registration.sign.first_name.2"),
                renderTranslationComponent("ms.registration.sign.first_name.3"),
                (player, strings) -> {
                    String firstname = strings[0].trim();

                    if (!MSPlayerUtils.matchesNameRegex(firstname)) {
                        this.sendWarningMessage();
                        return false;
                    }

                    this.playerInfo.getPlayerFile().getPlayerName().setFirstName(firstname);

                    this.sendDialogueMessage(translatable("ms.registration.message.7"), 25L);
                    this.sendDialogueMessage(translatable("ms.registration.message.8"), 100L);
                    this.sendDialogueMessage(translatable("ms.registration.message.9"), 225L);
                    this.sendDialogueMessage(translatable("ms.registration.message.10"), 300L);

                    Bukkit.getScheduler().runTaskLater(MSEssentials.getInstance(), this::setLastname, 375L);
                    return true;
                }).open(this.player);
    }

    private void setLastname() {
        SignMenu.create(
                renderTranslationComponent("ms.registration.sign.last_name.0"),
                renderTranslationComponent("ms.registration.sign.last_name.1"),
                renderTranslationComponent("ms.registration.sign.last_name.2"),
                renderTranslationComponent("ms.registration.sign.last_name.3"),
                (player, strings) -> {
                    String lastname = strings[0].trim();

                    if (!MSPlayerUtils.matchesNameRegex(lastname)) {
                        this.sendWarningMessage();
                        return false;
                    }

                    this.playerInfo.getPlayerFile().getPlayerName().setLastName(lastname);
                    Bukkit.getScheduler().runTaskLater(MSEssentials.getInstance(), this::setPatronymic, 10L);
                    return true;
                }).open(this.player);
    }

    private void setPatronymic() {
        SignMenu.create(
                renderTranslationComponent("ms.registration.sign.patronymic.0"),
                renderTranslationComponent("ms.registration.sign.patronymic.1"),
                renderTranslationComponent("ms.registration.sign.patronymic.2"),
                renderTranslationComponent("ms.registration.sign.patronymic.3"),
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
                            translatable(
                                    "ms.registration.message.11",
                                    text(this.playerInfo.getID(true, false)),
                                    text(name.getFirstName()),
                                    text(name.getLastName()),
                                    text(name.getPatronymic())
                            ),
                            25L
                    );
                    this.sendDialogueMessage(translatable("ms.registration.message.12"), 100L);
                    this.sendDialogueMessage(translatable("ms.registration.message.13"), 150L);

                    Bukkit.getScheduler().runTaskLater(
                            MSEssentials.getInstance(),
                            () -> PronounsMenu.open(this.player),
                            225L
                    );
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

        this.sendDialogueMessage(translatable("ms.registration.message.14"), 25L);
        this.sendDialogueMessage(translatable("ms.registration.message.15"), 75L);
        this.sendDialogueMessage(translatable("ms.registration.message.16"), 125L);
        this.sendDialogueMessage(
                translatable(
                        "ms.registration.message.17",
                        pronouns.getPronouns(),
                        pronouns.getTraveler()
                ),
                175L
        );

        Bukkit.getScheduler().runTaskLater(MSEssentials.getInstance(), this::setOther, 225L);
    }

    private void setOther() {
        PlayerSettings playerSettings = this.playerInfo.getPlayerFile().getPlayerSettings();
        this.player.displayName(this.playerInfo.getDefaultName());

        if (playerSettings.getResourcePackType() == ResourcePack.Type.NULL) {
            Bukkit.getScheduler().runTask(MSEssentials.getInstance(), () -> ResourcePackMenu.open(this.player));
        } else if (playerSettings.getResourcePackType() == ResourcePack.Type.NONE) {
            Bukkit.getScheduler().runTask(MSEssentials.getInstance(), this.playerInfo::initJoin);
        } else {
            ResourcePack.setResourcePack(this.playerInfo);
        }
    }

    private void sendWarningMessage() {
        this.player.sendMessage(translatable("ms.registration.only_cyrillic", NamedTextColor.GOLD));
    }

    private void sendDialogueMessage(
            @NotNull Component message,
            long delay
    ) {
        Bukkit.getScheduler().runTaskLater(MSEssentials.getInstance(), () -> {
            this.player.sendMessage(
                    translatable(
                            "ms.chat.local.format",
                            translatable("ms.registration.anonymous.name"),
                            message.color(MessageUtils.Colors.CHAT_COLOR_SECONDARY)
                    ).color(MessageUtils.Colors.CHAT_COLOR_PRIMARY)
            );
            this.player.playSound(this.playerLocation, Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.PLAYERS, 0.5f, 1.5f);
        }, delay);
    }
}
