package com.github.minersstudios.msessentials.discord;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.PlayerFile;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.skin.Skin;
import com.github.minersstudios.msessentials.utils.MessageUtils;
import github.scarsz.discordsrv.api.events.DiscordPrivateMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslation;
import static com.github.minersstudios.msessentials.MSEssentials.getInstance;
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

    public BotHandler(@NotNull DiscordPrivateMessageReceivedEvent event) {
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

    public void setPlayerInfo(@Nullable PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public @Nullable WaitingReplyTask getWaitingReplyTask() {
        return this.waitingReplyTask;
    }

    public void setWaitingReplyTask(@Nullable WaitingReplyTask waitingReplyTask) {
        this.waitingReplyTask = waitingReplyTask;
    }

    public void handleMessage(@NotNull Message message) {
        this.message = message;
        this.messageString = this.message.getContentDisplay();
        var attachments = this.message.getAttachments();
        int attachmentSize = attachments.size();
        short code = 0;

        if (this.isFlooding()) {
            this.reply(renderTranslation("ms.discord.message_attempts_limit_reached"));
            return;
        }

        if (this.waitingReplyTask != null) {
            if (this.waitingReplyTask.run()) {
                this.waitingReplyTask = null;
            }
            return;
        }

        if (this.messageString.matches("\\d+") && attachmentSize == 0) {
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

            if (!Skin.matchesNameRegex(this.messageString)) {
                this.reply(renderTranslation("ms.discord.skin.invalid_name_regex"));
                return;
            }

            try {
                this.handleSkin(link);
            } catch (IllegalArgumentException e) {
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
        int attemptCount = this.codeAttempt.count();
        long lastAttempt = this.codeAttempt.time();
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
        int attemptCount = this.messageAttempt.count();
        long lastAttempt = this.messageAttempt.time();
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
                    getInstance().runTask(() -> onlinePlayer.closeInventory());
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

    private void handleSkin(@NotNull String link) throws IllegalArgumentException {
        PlayerFile playerFile = this.playerInfo.getPlayerFile();
        String skinName = this.messageString;

        if (playerFile.containsSkin(skinName)) {
            getInstance().runTask(() -> this.handleEditTask(link, skinName));

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
            Skin skin = Skin.create(skinName, link);

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

            Player player = this.playerInfo.getOnlinePlayer();
            if (player != null) {
                ChatUtils.sendFine(
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

    private void handleEditTask(
            @NotNull String link,
            @NotNull String skinName
    ) {
        PlayerFile playerFile = this.playerInfo.getPlayerFile();
        Player player = this.playerInfo.getOnlinePlayer();
        String variantYes = renderTranslation("ms.discord.skin.variant.yes");
        String variantNo = renderTranslation("ms.discord.skin.variant.no");

        this.waitingReplyTask = () -> {
            if (this.messageString.equalsIgnoreCase(variantYes)) {
                Skin skin = Skin.create(skinName, link);

                if (skin == null) {
                    this.reply(renderTranslation("ms.discord.skin.service_unavailable"));
                    return true;
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

                if (player != null) {
                    ChatUtils.sendFine(
                            player,
                            translatable(
                                    "ms.discord.skin.successfully_edited.minecraft",
                                    text(skinName)
                            )
                    );
                }
            } else if (this.messageString.equalsIgnoreCase(variantNo)) {
                this.reply(renderTranslation("ms.discord.skin.variant.no.reply"));
            } else {
                this.reply(renderTranslation("ms.discord.unknown_command"));
                return false;
            }
            return true;
        };
    }

    private void handleShowSkinListTask() {
        PlayerFile playerFile = this.playerInfo.getPlayerFile();
        String variantYes = renderTranslation("ms.discord.skin.variant.yes");
        String variantNo = renderTranslation("ms.discord.skin.variant.no");

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

                getInstance().runTask(this::handleSkinListTask);
            } else if (this.messageString.equalsIgnoreCase(variantNo)) {
                this.reply(renderTranslation("ms.discord.skin.variant.no.reply"));
            } else {
                this.reply(renderTranslation("ms.discord.unknown_command"));
                return false;
            }
            return true;
        };
    }

    private void handleSkinListTask() {
        this.waitingReplyTask = () -> {
            if (!this.messageString.matches("[\\d]{1,2}")) {
                this.reply(renderTranslation("ms.discord.skin.variant.no.reply"));
                return true;
            }

            byte skinIndex;
            try {
                skinIndex = Byte.parseByte(this.messageString);
            } catch (NumberFormatException e) {
                this.reply(renderTranslation("ms.discord.skin.invalid_index"));
                return false;
            }

            Skin skin = this.playerInfo.getPlayerFile().getSkin(skinIndex - 1);

            if (skin == null) {
                this.reply(renderTranslation("ms.discord.skin.invalid_index"));
                return false;
            }

            getInstance().runTask(() -> this.handleSkinTask(skin));
            return true;
        };
    }

    private void handleSkinTask(@NotNull Skin skin) {
        this.replyEmbed(renderTranslation(translatable("ms.discord.skin.list_of_skin_actions")));

        this.waitingReplyTask = () -> {
            if (!this.messageString.matches("[\\d]{1,2}")) {
                this.reply(renderTranslation("ms.discord.skin.variant.no.reply"));
                return true;
            }

            byte actionIndex;
            try {
                actionIndex = Byte.parseByte(this.messageString);
            } catch (NumberFormatException e) {
                this.reply(renderTranslation("ms.discord.skin.invalid_index"));
                return false;
            }

            switch (actionIndex) {
                case 1 -> getInstance().runTask(() -> this.handleEditImageTask(skin));
                case 2 -> getInstance().runTask(() -> this.handleEditNameTask(skin));
                case 3 -> getInstance().runTask(() -> this.handleDeleteTask(skin));
                default -> {
                    this.reply(renderTranslation("ms.discord.skin.invalid_index"));
                    return false;
                }
            }
            return true;
        };
    }

    private void handleEditImageTask(@NotNull Skin editableSkin) {
        PlayerFile playerFile = this.playerInfo.getPlayerFile();
        Player player = this.playerInfo.getOnlinePlayer();
        String skinName = editableSkin.getName();

        this.replyEmbed(renderTranslation(translatable("ms.discord.skin.action.edit.info")));

        this.waitingReplyTask = () -> {
            var attachments = this.message.getAttachments();
            int attachmentSize = attachments.size();

            if (attachmentSize > 1) {
                this.reply(renderTranslation("ms.discord.skin.only_one_img"));
                return false;
            } else if (attachmentSize == 1) {
                Message.Attachment attachment = attachments.get(0);
                String link = attachment.getUrl();

                try {
                    Skin skin = Skin.create(skinName, link);

                    if (skin == null) {
                        this.reply(renderTranslation("ms.discord.skin.service_unavailable"));
                        return true;
                    }

                    playerFile.setSkin(playerFile.getSkinIndex(editableSkin), skin);
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

                    if (player != null) {
                        ChatUtils.sendFine(
                                player,
                                translatable(
                                        "ms.discord.skin.successfully_edited.minecraft",
                                        text(skinName)
                                )
                        );
                    }
                } catch (IllegalArgumentException e) {
                    this.reply(renderTranslation("ms.discord.skin.invalid_img"));
                    return false;
                }
            } else {
                this.reply(renderTranslation("ms.discord.skin.variant.no.reply"));
            }
            return true;
        };
    }

    private void handleEditNameTask(@NotNull Skin editableSkin) {
        Player player = this.playerInfo.getOnlinePlayer();
        PlayerFile playerFile = this.playerInfo.getPlayerFile();
        String oldName = editableSkin.getName();

        this.replyEmbed(renderTranslation(translatable("ms.discord.skin.action.rename.info")));

        this.waitingReplyTask = () -> {
            String newName = this.messageString;

            if (!Skin.matchesNameRegex(newName)) {
                this.reply(renderTranslation("ms.discord.skin.invalid_name_regex"));
                return false;
            }

            if (playerFile.containsSkin(newName)) {
                this.replyEmbed(
                        renderTranslation(
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
                    renderTranslation(
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
                ChatUtils.sendFine(
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

    private void handleDeleteTask(@NotNull Skin skin) {
        Player player = this.playerInfo.getOnlinePlayer();
        String skinName = skin.getName();

        this.playerInfo.getPlayerFile().removeSkin(skin);
        this.replyEmbed(renderTranslation(
                translatable(
                        "ms.discord.skin.successfully_removed",
                        text(skinName),
                        this.playerInfo.getDefaultName(),
                        text(this.playerInfo.getNickname())
                )
        ));

        if (player != null) {
            ChatUtils.sendFine(
                    player,
                    translatable(
                            "ms.discord.skin.successfully_removed.minecraft",
                            text(skinName)
                    )
            );
        }
    }
}
