package com.github.minersstudios.msessentials.discord;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.DiscordMap;
import com.github.minersstudios.msessentials.player.PlayerFile;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.Skin;
import com.github.minersstudios.msessentials.utils.MessageUtils;
import github.scarsz.discordsrv.api.events.DiscordPrivateMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslation;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class BotHandler {
    private final User user;
    private final long userId;
    private Message message;
    private String messageString;
    private Runnable waitingReplyTask;
    private PlayerInfo playerInfo;

    private Attempt messageAttempt = Attempt.ZERO_ATTEMPT;
    private Attempt codeAttempt = Attempt.ZERO_ATTEMPT;

    private static final int MESSAGE_MAX_ATTEMPTS = 2;
    private static final long MESSAGE_FLOOD_TIMEOUT = 1500L;
    private static final int CODE_MAX_ATTEMPTS = 10;
    private static final long CODE_FLOOD_TIMEOUT = 300000L;

    public BotHandler(@NotNull DiscordPrivateMessageReceivedEvent event) {
        this.user = event.getAuthor();
        this.userId = this.user.getIdLong();
        this.playerInfo = PlayerInfo.fromDiscord(this.userId);
        this.handleMessage(event.getMessage());
    }

    public @NotNull User getUser() {
        return this.user;
    }

    public @NotNull Message getMessage() {
        return this.message;
    }

    public @Nullable PlayerInfo getPlayerInfo() {
        return this.playerInfo;
    }

    public void setPlayerInfo(@Nullable PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public @Nullable Runnable getWaitingReplyTask() {
        return this.waitingReplyTask;
    }

    public void setWaitingReplyTask(@Nullable Runnable waitingReplyTask) {
        this.waitingReplyTask = waitingReplyTask;
    }

    public void handleMessage(@NotNull Message message) {
        this.message = message;
        this.messageString = this.message.getContentDisplay();
        short code = 0;

        if (this.isFlooding()) {
            this.reply(renderTranslation("ms.discord.message_attempts_limit_reached"));
            return;
        }

        if (this.waitingReplyTask != null) {
            this.waitingReplyTask.run();
            return;
        }

        if (this.messageString.matches("\\d+")) {
            String invalidCode = renderTranslation("ms.discord.invalid_code");

            try {
                code = Short.parseShort(this.messageString);
            } catch (NumberFormatException e) {
                this.reply(invalidCode);
                return;
            }

            if (code < 1000 || code > 9999) {
                this.reply(invalidCode);
                return;
            }

            if (this.isCodeFlooding()) {
                this.reply(renderTranslation("ms.discord.code_attempts_limit_reached"));
                return;
            }
        }

        if (code == 0 && this.playerInfo == null) {
            this.reply(renderTranslation("ms.discord.not_linked"));
            return;
        }

        if (code > 0) {
            this.handleCode(code);
            return;
        }

        var attachments = this.message.getAttachments();
        int attachmentSize = attachments.size();

        if (attachmentSize > 1) {
            this.reply(renderTranslation("ms.discord.skin.only_one_img"));
            return;
        } else if (attachmentSize == 1) {
            Message.Attachment attachment = attachments.get(0);
            String link = attachment.getUrl();

            if (this.messageString.isEmpty()) {
                this.reply(renderTranslation("ms.discord.skin.no_name"));
                return;
            }

            if (!this.messageString.matches("[a-zA-ZЀ-ӿ-0-9]{1,32}")) {
                this.reply(renderTranslation("ms.discord.skin.invalid_name_regex"));
                return;
            }

            if (this.isValidSkinImg(link)) {
                this.handleSkin(link);
            } else {
                this.reply(renderTranslation("ms.discord.skin.invalid_img"));
            }

            return;
        }

        this.reply(renderTranslation("ms.discord.unknown_command"));
    }

    private void reply(@NotNull String reply) {
        this.message.reply(reply).queue();
    }

    private void replyEmbed(@NotNull String reply) {
        this.message.replyEmbeds(MessageUtils.craftEmbed(reply)).queue();
    }

    private boolean isCodeFlooding() {
        long currentTime = System.currentTimeMillis();
        int attemptCount = this.codeAttempt.count;
        long lastAttempt = this.codeAttempt.time;
        Attempt newAttempt = new Attempt(++attemptCount, currentTime);

        if (lastAttempt == 0L) {
            this.codeAttempt = newAttempt;
            return false;
        }

        if (currentTime - lastAttempt <= CODE_FLOOD_TIMEOUT) {
            if (attemptCount < CODE_MAX_ATTEMPTS) {
                this.codeAttempt = newAttempt;
            }

            return attemptCount >= CODE_MAX_ATTEMPTS;
        }

        this.codeAttempt = Attempt.ZERO_ATTEMPT;
        return false;
    }

    private boolean isFlooding() {
        long currentTime = System.currentTimeMillis();
        int attemptCount = this.messageAttempt.count;
        long lastAttempt = this.messageAttempt.time;
        Attempt newAttempt = new Attempt(++attemptCount, currentTime);

        if (lastAttempt == 0L) {
            this.messageAttempt = newAttempt;
            return false;
        }

        if (currentTime - lastAttempt <= MESSAGE_FLOOD_TIMEOUT) {
            if (attemptCount < MESSAGE_MAX_ATTEMPTS) {
                this.messageAttempt = newAttempt;
            }

            return attemptCount >= MESSAGE_MAX_ATTEMPTS;
        }

        this.messageAttempt = Attempt.ZERO_ATTEMPT;
        return false;
    }

    private void handleCode(short code) {
        DiscordMap discordMap = MSEssentials.getCache().discordMap;
        PlayerInfo fromCode = discordMap.validateCode(code);

        if (fromCode == null) {
            this.reply(renderTranslation("ms.discord.no_code"));
        } else if (fromCode.equals(this.playerInfo)) {
            this.replyEmbed(renderTranslation("ms.discord.already_linked"));
        } else {
            this.playerInfo = fromCode;

            discordMap.removeCode(code);
            fromCode.linkDiscord(this.userId);
            this.replyEmbed(
                    renderTranslation(
                            translatable(
                                    "ms.discord.successfully_linked",
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );

            Player onlinePlayer = fromCode.getOnlinePlayer();
            if (onlinePlayer != null) {
                if (
                        onlinePlayer.getOpenInventory().getTopInventory() instanceof CustomInventory customInventory
                        && customInventory.getTitle().startsWith("§f" + renderTranslation("ms.menu.discord.title"))
                ) {
                    MSEssentials.getInstance().runTask(() -> onlinePlayer.closeInventory());
                }

                ChatUtils.sendFine(
                        onlinePlayer,
                        translatable(
                                "ms.command.discord.link.success",
                                Component.text(this.user.getName())
                        )
                );
            }
        }
    }

    private void handleSkin(@NotNull String link) {
        PlayerFile playerFile = this.playerInfo.getPlayerFile();
        String skinName = this.messageString;
        String variantYes = renderTranslation("ms.discord.skin.variant.yes");
        String variantNo = renderTranslation("ms.discord.skin.variant.no");

        if (playerFile.containsSkin(skinName)) {
            this.waitingReplyTask = () -> {
                if (this.messageString.equalsIgnoreCase(variantYes)) {
                    Skin skin = Skin.create(this.messageString, link);

                    if (skin == null) {
                        this.reply(renderTranslation("ms.discord.skin.service_unavailable"));
                        return;
                    }

                    playerFile.setSkin(playerFile.getSkinIndex(skinName), skin);
                    this.replyEmbed(
                            renderTranslation(
                                    translatable(
                                            "ms.discord.skin.successfully_edited",
                                            text(skinName),
                                            this.playerInfo.getDefaultName(),
                                            text(this.playerInfo.getNickname())
                                    )
                            )
                    );

                    this.waitingReplyTask = null;
                } else if (this.messageString.equalsIgnoreCase(variantNo)) {
                    this.reply(renderTranslation("ms.discord.skin.variant.no.reply"));
                    this.waitingReplyTask = null;
                } else {
                    this.reply(renderTranslation("ms.discord.unknown_command"));
                }
            };

            this.replyEmbed(
                    renderTranslation(
                            translatable(
                                    "ms.discord.skin.already_set",
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );
            return;
        }

        if (playerFile.hasAvailableSkinSlot()) {
            Skin skin = Skin.create(this.messageString, link);

            if (skin == null) {
                this.reply(renderTranslation("ms.discord.skin.service_unavailable"));
                return;
            }

            playerFile.addSkin(skin);
            this.replyEmbed(
                    renderTranslation(
                            translatable(
                                    "ms.discord.skin.successfully_added",
                                    text(skinName),
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );
        } else {
            this.waitingReplyTask = () -> {
                if (this.messageString.equalsIgnoreCase(variantYes)) {
                    var skins = playerFile.getSkins();
                    StringBuilder skinList = new StringBuilder();

                    for (int i = 0; i < skins.size(); i++) {
                        String name = skins.get(i).getName();
                        skinList.append("\n").append(i + 1).append(" : \"").append(name).append("\"");
                    }

                    this.replyEmbed(
                            renderTranslation(
                                    translatable(
                                            "ms.discord.skin.list_of_skins",
                                            text(skinList.toString())
                                    )
                            )
                    );
                    //TODO
                    this.waitingReplyTask = null;
                } else if (this.messageString.equalsIgnoreCase(variantNo)) {
                    this.reply(renderTranslation("ms.discord.skin.variant.no.reply"));
                    this.waitingReplyTask = null;
                } else {
                    this.reply(renderTranslation("ms.discord.unknown_command"));
                }
            };

            this.replyEmbed(
                    renderTranslation(
                            translatable(
                                    "ms.discord.skin.too_many_skins",
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );
        }
    }

    private boolean isValidSkinImg(@NotNull String link) {
        if (!link.endsWith(".png")) return false;
        try {
            BufferedImage image = ImageIO.read(new URL(link));
            return image.getWidth() == 64 && image.getHeight() == 64;
        } catch (IOException e) {
            return false;
        }
    }

    private record Attempt(
            int count,
            long time
    ) {
        public static final Attempt ZERO_ATTEMPT = new Attempt(0, 0L);
    }
}
