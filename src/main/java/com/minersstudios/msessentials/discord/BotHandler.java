package com.minersstudios.msessentials.discord;

import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import com.minersstudios.msessentials.util.MessageUtils;
import github.scarsz.discordsrv.api.events.DiscordPrivateMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.msessentials.MSEssentials.getInstance;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class BotHandler {
    private final User user;
    private final long userId;
    private Message message;
    private String messageString;
    private WaitingReplyTask waitingReplyTask;
    private PlayerInfo playerInfo;

    private Attempt messageAttempt = Attempt.ZERO_ATTEMPT;
    private Attempt codeAttempt = Attempt.ZERO_ATTEMPT;

    private static final int MESSAGE_MAX_ATTEMPTS = 2;
    private static final long MESSAGE_FLOOD_TIMEOUT = 1500L; // 1.5 seconds
    private static final int CODE_MAX_ATTEMPTS = 10;
    private static final long CODE_FLOOD_TIMEOUT = 300000L; // 5 minutes

    public BotHandler(final @NotNull DiscordPrivateMessageReceivedEvent event) {
        this.user = event.getAuthor();
        this.userId = this.user.getIdLong();
        this.playerInfo = PlayerInfo.fromDiscord(this.userId);
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

    public void setPlayerInfo(final @Nullable PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public @Nullable WaitingReplyTask getWaitingReplyTask() {
        return this.waitingReplyTask;
    }

    public void setWaitingReplyTask(final @Nullable WaitingReplyTask waitingReplyTask) {
        this.waitingReplyTask = waitingReplyTask;
    }

    public void handleMessage(final @NotNull Message message) {
        this.message = message;
        this.messageString = this.message.getContentDisplay();

        final var attachments = this.message.getAttachments();
        final int attachmentSize = attachments.size();
        short code = 0;

        if (this.isFlooding()) {
            this.reply(LanguageFile.renderTranslation("ms.discord.message_attempts_limit_reached"));
            return;
        }

        if (this.waitingReplyTask != null) {
            if (this.waitingReplyTask.run()) {
                this.waitingReplyTask = null;
            }
            return;
        }

        if (this.messageString.matches("\\d+") && attachmentSize == 0) {
            final String invalidCode = LanguageFile.renderTranslation("ms.discord.invalid_code");

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
                this.reply(LanguageFile.renderTranslation("ms.discord.code_attempts_limit_reached"));
                return;
            }
        }

        if (code == 0 && this.playerInfo == null) {
            this.reply(LanguageFile.renderTranslation("ms.discord.not_linked"));
            return;
        }

        if (code > 0) {
            this.handleCode(code);
            return;
        }

        if (attachmentSize > 1) {
            this.reply(LanguageFile.renderTranslation("ms.discord.skin.only_one_img"));
            return;
        } else if (attachmentSize == 1) {
            final Message.Attachment attachment = attachments.get(0);
            final String link = attachment.getUrl();

            if (this.messageString.isEmpty()) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.no_name"));
                return;
            }

            if (!Skin.matchesNameRegex(this.messageString)) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.invalid_name_regex"));
                return;
            }

            try {
                this.handleSkin(link);
            } catch (IllegalArgumentException e) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.invalid_img"));
            }

            return;
        }

        this.reply(LanguageFile.renderTranslation("ms.discord.unknown_command"));
    }

    private void reply(@NotNull String reply) {
        this.message.reply(reply).queue();
    }

    private void replyEmbed(@NotNull String reply) {
        this.message.replyEmbeds(MessageUtils.craftEmbed(reply)).queue();
    }

    private boolean isCodeFlooding() {
        final long currentTime = System.currentTimeMillis();
        int attemptCount = this.codeAttempt.count();
        final long lastAttempt = this.codeAttempt.time();
        final Attempt newAttempt = new Attempt(++attemptCount, currentTime);

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
        final long currentTime = System.currentTimeMillis();

        int attemptCount = this.messageAttempt.count();
        final long lastAttempt = this.messageAttempt.time();
        final Attempt newAttempt = new Attempt(++attemptCount, currentTime);

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

    private void handleCode(final short code) {
        final DiscordMap discordMap = MSEssentials.getCache().discordMap;
        final PlayerInfo fromCode = discordMap.validateCode(code);

        if (fromCode == null) {
            this.reply(LanguageFile.renderTranslation("ms.discord.no_code"));
        } else if (fromCode.equals(this.playerInfo)) {
            this.replyEmbed(LanguageFile.renderTranslation("ms.discord.already_linked"));
        } else {
            this.playerInfo = fromCode;
            final Player onlinePlayer = fromCode.getOnlinePlayer();

            discordMap.removeCode(code);
            fromCode.linkDiscord(this.userId);
            this.replyEmbed(
                    LanguageFile.renderTranslation(
                            translatable(
                                    "ms.discord.successfully_linked",
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );

            if (onlinePlayer != null) {
                if (
                        onlinePlayer.getOpenInventory().getTopInventory() instanceof CustomInventory customInventory
                        && customInventory.getTitle().startsWith("§f" + LanguageFile.renderTranslation("ms.menu.discord.title"))
                ) {
                    getInstance().runTask(() -> onlinePlayer.closeInventory());
                }

                MSLogger.fine(
                        onlinePlayer,
                        translatable(
                                "ms.command.discord.link.success",
                                text(this.user.getName())
                        )
                );
            }
        }
    }

    private void handleSkin(final @NotNull String link) throws IllegalArgumentException {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String skinName = this.messageString;

        if (playerFile.containsSkin(skinName)) {
            getInstance().runTask(() -> this.handleEditTask(link, skinName));

            this.replyEmbed(
                    LanguageFile.renderTranslation(
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
            final Skin skin = Skin.create(skinName, link);

            if (skin == null) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.service_unavailable"));
                return;
            }

            playerFile.addSkin(skin);
            this.replyEmbed(
                    LanguageFile.renderTranslation(
                            translatable(
                                    "ms.discord.skin.successfully_added",
                                    text(skinName),
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );

            final Player player = this.playerInfo.getOnlinePlayer();

            if (player != null) {
                MSLogger.fine(
                        player,
                        translatable(
                                "ms.discord.skin.successfully_added.minecraft",
                                text(skinName)
                        )
                );
            }
        } else {
            getInstance().runTask(this::handleShowSkinListTask);

            this.replyEmbed(
                    LanguageFile.renderTranslation(
                            translatable(
                                    "ms.discord.skin.too_many_skins",
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );
        }
    }

    private void handleEditTask(
            final @NotNull String link,
            final @NotNull String skinName
    ) {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final Player player = this.playerInfo.getOnlinePlayer();
        final String variantYes = LanguageFile.renderTranslation("ms.discord.skin.variant.yes");
        final String variantNo = LanguageFile.renderTranslation("ms.discord.skin.variant.no");

        this.waitingReplyTask = () -> {
            if (this.messageString.equalsIgnoreCase(variantYes)) {
                final Skin skin = Skin.create(skinName, link);

                if (skin == null) {
                    this.reply(LanguageFile.renderTranslation("ms.discord.skin.service_unavailable"));
                    return true;
                }

                playerFile.setSkin(playerFile.getSkinIndex(skinName), skin);
                this.replyEmbed(
                        LanguageFile.renderTranslation(
                                translatable(
                                        "ms.discord.skin.successfully_edited",
                                        text(skinName),
                                        this.playerInfo.getDefaultName(),
                                        text(this.playerInfo.getNickname())
                                )
                        )
                );

                if (player != null) {
                    MSLogger.fine(
                            player,
                            translatable(
                                    "ms.discord.skin.successfully_edited.minecraft",
                                    text(skinName)
                            )
                    );
                }
            } else if (this.messageString.equalsIgnoreCase(variantNo)) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.variant.no.reply"));
            } else {
                this.reply(LanguageFile.renderTranslation("ms.discord.unknown_command"));
                return false;
            }

            return true;
        };
    }

    private void handleShowSkinListTask() {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String variantYes = LanguageFile.renderTranslation("ms.discord.skin.variant.yes");
        final String variantNo = LanguageFile.renderTranslation("ms.discord.skin.variant.no");

        this.waitingReplyTask = () -> {
            if (this.messageString.equalsIgnoreCase(variantYes)) {
                final var skins = playerFile.getSkins();
                final StringBuilder skinList = new StringBuilder();

                for (int i = 0; i < skins.size(); i++) {
                    final String name = skins.get(i).getName();
                    skinList.append("\n").append(i + 1).append(" : \"").append(name).append("\"");
                }

                this.replyEmbed(
                        LanguageFile.renderTranslation(
                                translatable(
                                        "ms.discord.skin.list_of_skins",
                                        text(skinList.toString())
                                )
                        )
                );

                getInstance().runTask(this::handleSkinListTask);
            } else if (this.messageString.equalsIgnoreCase(variantNo)) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.variant.no.reply"));
            } else {
                this.reply(LanguageFile.renderTranslation("ms.discord.unknown_command"));
                return false;
            }

            return true;
        };
    }

    private void handleSkinListTask() {
        this.waitingReplyTask = () -> {
            if (!this.messageString.matches("[\\d]{1,2}")) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.variant.no.reply"));
                return true;
            }

            final byte skinIndex;
            try {
                skinIndex = Byte.parseByte(this.messageString);
            } catch (NumberFormatException e) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.invalid_index"));
                return false;
            }

            final Skin skin = this.playerInfo.getPlayerFile().getSkin(skinIndex - 1);

            if (skin == null) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.invalid_index"));
                return false;
            }

            getInstance().runTask(() -> this.handleSkinTask(skin));
            return true;
        };
    }

    private void handleSkinTask(final @NotNull Skin skin) {
        this.replyEmbed(LanguageFile.renderTranslation(translatable("ms.discord.skin.list_of_skin_actions")));

        this.waitingReplyTask = () -> {
            if (!this.messageString.matches("[\\d]{1,2}")) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.variant.no.reply"));
                return true;
            }

            final byte actionIndex;
            try {
                actionIndex = Byte.parseByte(this.messageString);
            } catch (NumberFormatException e) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.invalid_index"));
                return false;
            }

            switch (actionIndex) {
                case 1 -> getInstance().runTask(() -> this.handleEditImageTask(skin));
                case 2 -> getInstance().runTask(() -> this.handleEditNameTask(skin));
                case 3 -> getInstance().runTask(() -> this.handleDeleteTask(skin));
                default -> {
                    this.reply(LanguageFile.renderTranslation("ms.discord.skin.invalid_index"));
                    return false;
                }
            }

            return true;
        };
    }

    private void handleEditImageTask(final @NotNull Skin editableSkin) {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final Player player = this.playerInfo.getOnlinePlayer();
        final String skinName = editableSkin.getName();

        this.replyEmbed(LanguageFile.renderTranslation(translatable("ms.discord.skin.action.edit.info")));

        this.waitingReplyTask = () -> {
            final var attachments = this.message.getAttachments();
            final int attachmentSize = attachments.size();

            if (attachmentSize > 1) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.only_one_img"));
                return false;
            } else if (attachmentSize == 1) {
                final Message.Attachment attachment = attachments.get(0);
                final String link = attachment.getUrl();

                try {
                    final Skin skin = Skin.create(skinName, link);

                    if (skin == null) {
                        this.reply(LanguageFile.renderTranslation("ms.discord.skin.service_unavailable"));
                        return true;
                    }

                    playerFile.setSkin(playerFile.getSkinIndex(editableSkin), skin);
                    this.replyEmbed(
                            LanguageFile.renderTranslation(
                                    translatable(
                                            "ms.discord.skin.successfully_edited",
                                            text(skinName),
                                            this.playerInfo.getDefaultName(),
                                            text(this.playerInfo.getNickname())
                                    )
                            )
                    );

                    if (player != null) {
                        MSLogger.fine(
                                player,
                                translatable(
                                        "ms.discord.skin.successfully_edited.minecraft",
                                        text(skinName)
                                )
                        );
                    }
                } catch (IllegalArgumentException e) {
                    this.reply(LanguageFile.renderTranslation("ms.discord.skin.invalid_img"));
                    return false;
                }
            } else {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.variant.no.reply"));
            }
            return true;
        };
    }

    private void handleEditNameTask(final @NotNull Skin editableSkin) {
        final Player player = this.playerInfo.getOnlinePlayer();
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String oldName = editableSkin.getName();

        this.replyEmbed(LanguageFile.renderTranslation(translatable("ms.discord.skin.action.rename.info")));

        this.waitingReplyTask = () -> {
            final String newName = this.messageString;

            if (!Skin.matchesNameRegex(newName)) {
                this.reply(LanguageFile.renderTranslation("ms.discord.skin.invalid_name_regex"));
                return false;
            }

            if (playerFile.containsSkin(newName)) {
                this.replyEmbed(
                        LanguageFile.renderTranslation(
                                translatable(
                                        "ms.discord.skin.edit.already_set",
                                        this.playerInfo.getDefaultName(),
                                        text(this.playerInfo.getNickname())
                                )
                        )
                );
                return false;
            }

            playerFile.setSkin(
                    playerFile.getSkinIndex(editableSkin),
                    Skin.create(newName, editableSkin.getValue(), editableSkin.getSignature())
            );
            this.replyEmbed(
                    LanguageFile.renderTranslation(
                            translatable(
                                    "ms.discord.skin.successfully_renamed",
                                    text(oldName),
                                    text(newName),
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );

            if (player != null) {
                MSLogger.fine(
                        player,
                        translatable(
                                "ms.discord.skin.successfully_renamed.minecraft",
                                text(oldName),
                                text(newName)
                        )
                );
            }

            return true;
        };
    }

    private void handleDeleteTask(final @NotNull Skin skin) {
        final Player player = this.playerInfo.getOnlinePlayer();
        final String skinName = skin.getName();

        this.playerInfo.getPlayerFile().removeSkin(skin);
        this.replyEmbed(LanguageFile.renderTranslation(
                translatable(
                        "ms.discord.skin.successfully_removed",
                        text(skinName),
                        this.playerInfo.getDefaultName(),
                        text(this.playerInfo.getNickname())
                )
        ));

        if (player != null) {
            MSLogger.fine(
                    player,
                    translatable(
                            "ms.discord.skin.successfully_removed.minecraft",
                            text(skinName)
                    )
            );
        }
    }
}
