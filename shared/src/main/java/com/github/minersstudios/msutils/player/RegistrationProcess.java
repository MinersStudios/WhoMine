package com.github.minersstudios.msutils.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.menu.PronounsMenu;
import com.github.minersstudios.msutils.menu.ResourcePackMenu;
import com.github.minersstudios.msutils.utils.MSPlayerUtils;
import com.github.minersstudios.msutils.utils.MessageUtils;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

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

        Bukkit.getScheduler().runTaskLater(MSUtils.getInstance(), this::setFirstname, 550L);
    }

    private void setFirstname() {
        SignMenu menu = new SignMenu(
                translatable("ms.registration.sign.first_name.0"),
                translatable("ms.registration.sign.first_name.1"),
                translatable("ms.registration.sign.first_name.2"),
                translatable("ms.registration.sign.first_name.3")
        ).response((player, strings) -> {
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

            Bukkit.getScheduler().runTaskLater(MSUtils.getInstance(), this::setLastname, 375L);
            return true;
        });
        menu.open(this.player);
    }

    private void setLastname() {
        SignMenu menu = new SignMenu(
                translatable("ms.registration.sign.last_name.0"),
                translatable("ms.registration.sign.last_name.1"),
                translatable("ms.registration.sign.last_name.2"),
                translatable("ms.registration.sign.last_name.3")
        ).response((player, strings) -> {
            String lastname = strings[0].trim();

            if (!MSPlayerUtils.matchesNameRegex(lastname)) {
                this.sendWarningMessage();
                return false;
            }

            this.playerInfo.getPlayerFile().getPlayerName().setLastName(lastname);
            Bukkit.getScheduler().runTaskLater(MSUtils.getInstance(), this::setPatronymic, 10L);
            return true;
        });
        menu.open(this.player);
    }

    private void setPatronymic() {
        SignMenu menu = new SignMenu(
                translatable("ms.registration.sign.patronymic.0"),
                translatable("ms.registration.sign.patronymic.1"),
                translatable("ms.registration.sign.patronymic.2"),
                translatable("ms.registration.sign.patronymic.3")
        ).response((player, strings) -> {
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
                    MSUtils.getInstance(),
                    () -> PronounsMenu.open(this.player),
                    225L
            );
            return true;
        });
        menu.open(this.player);
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

        Bukkit.getScheduler().runTaskLater(MSUtils.getInstance(), this::setOther, 225L);
    }

    private void setOther() {
        PlayerSettings playerSettings = this.playerInfo.getPlayerFile().getPlayerSettings();
        this.player.displayName(this.playerInfo.getDefaultName());

        if (playerSettings.getResourcePackType() == ResourcePack.Type.NULL) {
            Bukkit.getScheduler().runTask(MSUtils.getInstance(), () -> ResourcePackMenu.open(this.player));
        } else if (playerSettings.getResourcePackType() == ResourcePack.Type.NONE) {
            Bukkit.getScheduler().runTask(MSUtils.getInstance(), this.playerInfo::initJoin);
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
        Bukkit.getScheduler().runTaskLater(MSUtils.getInstance(), () -> {
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

    public static final class SignMenu {
        private static final Map<Player, SignMenu> inputs = new HashMap<>();
        private final List<Component> text;
        private BiPredicate<Player, String[]> response;
        private Location location;

        public SignMenu(Component @NotNull ... text) {
            this.text = Lists.newArrayList(text);

            MSUtils.getProtocolManager().addPacketListener(new PacketAdapter(MSUtils.getInstance(), PacketType.Play.Client.UPDATE_SIGN) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    Player player = event.getPlayer();
                    SignMenu menu = inputs.remove(player);

                    if (menu == null) return;

                    event.setCancelled(true);
                    menu.location.setY(200);

                    if (!menu.response.test(player, event.getPacket().getStringArrays().read(0))) {
                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> menu.open(player), 2L);
                    }

                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        if (player.isOnline()) {
                            player.sendBlockChange(menu.location, menu.location.getBlock().getBlockData());
                        }
                    });
                }
            });
        }

        public SignMenu response(@NotNull BiPredicate<Player, String[]> response) {
            this.response = response;
            return this;
        }

        public void open(@NotNull Player player) {
            //TODO fix open sign
            PacketContainer openSign = MSUtils.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
            this.location = player.getLocation();

            this.location.setY(200);

            player.sendBlockChange(this.location, Material.OAK_SIGN.createBlockData());
            player.sendSignChange(this.location, this.text);

            try {
                openSign.getBlockPositionModifier().write(0, new BlockPosition(this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ()));
                MSUtils.getProtocolManager().sendServerPacket(player, openSign);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            inputs.put(player, this);
        }
    }
}
