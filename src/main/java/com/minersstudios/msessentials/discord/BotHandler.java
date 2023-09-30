package com.minersstudios.msessentials.discord;

import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import com.minersstudios.msessentials.util.DiscordUtil;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.minersstudios.mscore.plugin.config.LanguageFile.renderTranslation;
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

    private static final String LIST_INDEX_REGEX = "\\d{1,2}";
    private static final Pattern LIST_INDEX_PATTERN = Pattern.compile(LIST_INDEX_REGEX);

    private static final String NOT_A_USER = renderTranslation("ms.discord.not_a_user");
    private static final String MESSAGE_ATTEMPTS_LIMIT_REACHED = renderTranslation("ms.discord.message_attempts_limit_reached");
    private static final String CODE_ATTEMPTS_LIMIT_REACHED = renderTranslation("ms.discord.code_attempts_limit_reached");
    private static final String INVALID_CODE = renderTranslation("ms.discord.invalid_code");
    private static final String NO_CODE = renderTranslation("ms.discord.no_code");
    private static final String ALREADY_LINKED = renderTranslation("ms.discord.already_linked");
    private static final String NOT_LINKED = renderTranslation("ms.discord.not_linked");
    private static final TranslatableComponent SUCCESSFULLY_LINKED = translatable("ms.discord.successfully_linked");
    private static final TranslatableComponent SUCCESSFULLY_LINKED_MINE = translatable( "ms.command.discord.link.success");
    private static final String ONLY_ONE_IMG = renderTranslation("ms.discord.skin.only_one_img");
    private static final String INVALID_IMG = renderTranslation("ms.discord.skin.invalid_img");
    private static final TranslatableComponent TOO_MANY_SKINS = translatable("ms.discord.skin.too_many_skins");
    private static final String SKIN_NO_NAME = renderTranslation("ms.discord.skin.no_name");
    private static final String INVALID_NAME = renderTranslation("ms.discord.skin.invalid_name_regex");
    private static final TranslatableComponent ALREADY_SET = translatable("ms.discord.skin.already_set");
    private static final TranslatableComponent SUCCESSFULLY_RENAMED = translatable("ms.discord.skin.successfully_renamed");
    private static final TranslatableComponent SUCCESSFULLY_RENAMED_MINE = translatable("ms.discord.skin.successfully_renamed.minecraft");
    private static final TranslatableComponent SUCCESSFULLY_REMOVED = translatable("ms.discord.skin.successfully_removed");
    private static final TranslatableComponent SUCCESSFULLY_REMOVED_MINE = translatable("ms.discord.skin.successfully_removed.minecraft");
    private static final TranslatableComponent SUCCESSFULLY_EDITED = translatable("ms.discord.skin.successfully_edited");
    private static final TranslatableComponent SUCCESSFULLY_EDITED_MINE = translatable("ms.discord.skin.successfully_edited.minecraft");
    private static final String UNKNOWN_COMMAND = renderTranslation("ms.discord.unknown_command");
    private static final String MENU_TITLE = "§f" + renderTranslation("ms.menu.discord.title");
    private static final String INVALID_INDEX = renderTranslation("ms.discord.skin.invalid_index");
    private static final String SERVICE_UNAVAILABLE = renderTranslation("ms.discord.skin.service_unavailable");
    private static final TranslatableComponent SKIN_SUCCESSFULLY_ADDED = translatable("ms.discord.skin.successfully_added");
    private static final TranslatableComponent SKIN_SUCCESSFULLY_ADDED_MINE = translatable("ms.discord.skin.successfully_added.minecraft");
    private static final String VARIANT_YES = renderTranslation("ms.discord.skin.variant.yes").toLowerCase(Locale.ROOT);
    private static final String VARIANT_NO = renderTranslation("ms.discord.skin.variant.no").toLowerCase(Locale.ROOT);
    private static final String VARIANT_NO_REPLY = renderTranslation("ms.discord.skin.variant.no.reply");
    private static final String ACTION_RENAME_INFO = renderTranslation("ms.discord.skin.action.rename.info");
    private static final String ACTION_EDIT_INFO = renderTranslation("ms.discord.skin.action.edit.info");
    private static final String LIST_OF_SKIN_ACTIONS = renderTranslation("ms.discord.skin.list_of_skin_actions");
    private static final TranslatableComponent LIST_OF_SKINS = translatable("ms.discord.skin.list_of_skins");

    public BotHandler(final @NotNull MessageReceivedEvent event) {
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

        if (!DiscordUtil.isVerified(this.user)) {
            this.reply(NOT_A_USER);
            return;
        }

        if (this.isFlooding()) {
            this.reply(MESSAGE_ATTEMPTS_LIMIT_REACHED);
            return;
        }

        if (this.waitingReplyTask != null) {
            if (this.waitingReplyTask.run()) {
                this.waitingReplyTask = null;
            }
            return;
        }

        if (this.messageString.matches("\\d+") && attachmentSize == 0) {
            try {
                code = Short.parseShort(this.messageString);
            } catch (NumberFormatException e) {
                this.reply(INVALID_CODE);
                return;
            }

            if (code < 1000 || code > 9999) {
                this.reply(INVALID_CODE);
                return;
            }

            if (this.isCodeFlooding()) {
                this.reply(CODE_ATTEMPTS_LIMIT_REACHED);
                return;
            }
        }

        if (code == 0 && this.playerInfo == null) {
            this.reply(NOT_LINKED);
            return;
        }

        if (code > 0) {
            this.handleCode(code);
            return;
        }

        if (attachmentSize > 1) {
            this.reply(ONLY_ONE_IMG);
            return;
        } else if (attachmentSize == 1) {
            final Message.Attachment attachment = attachments.get(0);
            final String link = attachment.getUrl();

            if (this.messageString.isEmpty()) {
                this.reply(SKIN_NO_NAME);
                return;
            }

            if (!Skin.matchesNameRegex(this.messageString)) {
                this.reply(INVALID_NAME);
                return;
            }

            try {
                this.handleSkin(link);
            } catch (IllegalArgumentException e) {
                this.reply(INVALID_IMG);
            }

            return;
        }

        this.reply(UNKNOWN_COMMAND);
    }

    private void reply(@NotNull String reply) {
        this.message.reply(reply).queue();
    }

    private void replyEmbed(@NotNull String reply) {
        this.message.replyEmbeds(craftEmbed(reply)).queue();
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
            this.reply(NO_CODE);
        } else if (fromCode.equals(this.playerInfo)) {
            this.replyEmbed(ALREADY_LINKED);
        } else {
            this.playerInfo = fromCode;
            final Player onlinePlayer = fromCode.getOnlinePlayer();

            discordMap.removeCode(code);
            fromCode.linkDiscord(this.userId);
            this.replyEmbed(
                    renderTranslation(
                            SUCCESSFULLY_LINKED.args(
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );

            if (onlinePlayer != null) {
                if (
                        onlinePlayer.getOpenInventory().getTopInventory() instanceof CustomInventory customInventory
                        && customInventory.getTitle().startsWith(MENU_TITLE)
                ) {
                    getInstance().runTask(() -> onlinePlayer.closeInventory());
                }

                MSLogger.fine(onlinePlayer, SUCCESSFULLY_LINKED_MINE.args(text(this.user.getName())));
            }
        }
    }

    private void handleSkin(final @NotNull String link) throws IllegalArgumentException {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String skinName = this.messageString;

        if (playerFile.containsSkin(skinName)) {
            getInstance().runTask(() -> this.handleEditTask(link, skinName));

            this.replyEmbed(
                    renderTranslation(
                            ALREADY_SET.args(
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
                this.reply(SERVICE_UNAVAILABLE);
                return;
            }

            playerFile.addSkin(skin);
            this.replyEmbed(
                    renderTranslation(
                            SKIN_SUCCESSFULLY_ADDED.args(
                                    text(skinName),
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );

            final Player player = this.playerInfo.getOnlinePlayer();

            if (player != null) {
                MSLogger.fine(player, SKIN_SUCCESSFULLY_ADDED_MINE.args(text(skinName)));
            }
        } else {
            getInstance().runTask(this::handleShowSkinListTask);

            this.replyEmbed(
                    renderTranslation(
                            TOO_MANY_SKINS.args(
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

        this.waitingReplyTask = () -> {
            final String reply = this.messageString.toLowerCase(Locale.ROOT);

            if (reply.equals(VARIANT_YES)) {
                if (this.editSkin(link, skinName, playerFile, playerFile.getSkinIndex(skinName))) return true;

                if (player != null) {
                    MSLogger.fine(player, SUCCESSFULLY_EDITED_MINE.args(text(skinName)));
                }
            } else if (reply.equals(VARIANT_NO)) {
                this.reply(VARIANT_NO_REPLY);
            } else {
                this.reply(UNKNOWN_COMMAND);
                return false;
            }

            return true;
        };
    }

    private void handleShowSkinListTask() {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();

        this.waitingReplyTask = () -> {
            final String reply = this.messageString.toLowerCase(Locale.ROOT);

            if (reply.equals(VARIANT_YES)) {
                final var skins = playerFile.getSkins();
                final StringBuilder skinList = new StringBuilder();

                for (int i = 0; i < skins.size(); i++) {
                    skinList
                    .append("\n")
                    .append(i + 1)
                    .append(" : \"")
                    .append(skins.get(i).getName())
                    .append("\"");
                }

                this.replyEmbed(renderTranslation(LIST_OF_SKINS.args(text(skinList.toString()))));
                getInstance().runTask(this::handleSkinListTask);
            } else if (reply.equals(VARIANT_NO)) {
                this.reply(VARIANT_NO_REPLY);
            } else {
                this.reply(UNKNOWN_COMMAND);
                return false;
            }

            return true;
        };
    }

    private void handleSkinListTask() {
        this.waitingReplyTask = () -> {
            if (!LIST_INDEX_PATTERN.matcher(this.messageString).matches()) {
                this.reply(VARIANT_NO_REPLY);
                return true;
            }

            final byte skinIndex;
            try {
                skinIndex = Byte.parseByte(this.messageString);
            } catch (NumberFormatException e) {
                this.reply(INVALID_INDEX);
                return false;
            }

            final Skin skin = this.playerInfo.getPlayerFile().getSkin(skinIndex - 1);

            if (skin == null) {
                this.reply(INVALID_INDEX);
                return false;
            }

            getInstance().runTask(() -> this.handleSkinTask(skin));
            return true;
        };
    }

    private void handleSkinTask(final @NotNull Skin skin) {
        this.replyEmbed(LIST_OF_SKIN_ACTIONS);

        this.waitingReplyTask = () -> {
            if (!LIST_INDEX_PATTERN.matcher(this.messageString).matches()) {
                this.reply(VARIANT_NO_REPLY);
                return true;
            }

            final byte actionIndex;
            try {
                actionIndex = Byte.parseByte(this.messageString);
            } catch (NumberFormatException e) {
                this.reply(INVALID_INDEX);
                return false;
            }

            switch (actionIndex) {
                case 1 -> getInstance().runTask(() -> this.handleEditImageTask(skin));
                case 2 -> getInstance().runTask(() -> this.handleEditNameTask(skin));
                case 3 -> getInstance().runTask(() -> this.handleDeleteTask(skin));
                default -> {
                    this.reply(INVALID_INDEX);
                    return false;
                }
            }

            return true;
        };
    }

    private void handleEditImageTask(final @NotNull Skin editableSkin) {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String skinName = editableSkin.getName();

        this.replyEmbed(ACTION_EDIT_INFO);

        this.waitingReplyTask = () -> {
            final var attachments = this.message.getAttachments();

            switch (attachments.size()) {
                case 0 -> {
                    this.reply(VARIANT_NO_REPLY);
                    return true;
                }
                case 1 -> {
                    final Message.Attachment attachment = attachments.get(0);
                    final String link = attachment.getUrl();

                    try {
                        if (editSkin(link, skinName, playerFile, playerFile.getSkinIndex(editableSkin))) return true;

                        final Player player = this.playerInfo.getOnlinePlayer();

                        if (player != null) {
                            MSLogger.fine(player, SUCCESSFULLY_EDITED_MINE.args(text(skinName)));
                        }
                    } catch (Exception e) {
                        this.reply(INVALID_IMG);
                        return false;
                    }
                }
                default -> {
                    this.reply(ONLY_ONE_IMG);
                    return false;
                }
            }

            return true;
        };
    }

    private void handleEditNameTask(final @NotNull Skin editableSkin) {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String oldName = editableSkin.getName();

        this.replyEmbed(ACTION_RENAME_INFO);

        this.waitingReplyTask = () -> {
            final String newName = this.messageString;

            if (!Skin.matchesNameRegex(newName)) {
                this.reply(INVALID_NAME);
                return false;
            }

            if (playerFile.containsSkin(newName)) {
                this.replyEmbed(
                        renderTranslation(
                                ALREADY_SET.args(
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
                            SUCCESSFULLY_RENAMED.args(
                                    text(oldName),
                                    text(newName),
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );

            final Player player = this.playerInfo.getOnlinePlayer();

            if (player != null) {
                MSLogger.fine(
                        player,
                        SUCCESSFULLY_RENAMED_MINE.args(
                                text(oldName),
                                text(newName)
                        )
                );
            }

            return true;
        };
    }

    private void handleDeleteTask(final @NotNull Skin skin) {
        final String skinName = skin.getName();
        final Player player = this.playerInfo.getOnlinePlayer();

        this.playerInfo.getPlayerFile().removeSkin(skin);
        this.replyEmbed(renderTranslation(
                SUCCESSFULLY_REMOVED.args(
                        text(skinName),
                        this.playerInfo.getDefaultName(),
                        text(this.playerInfo.getNickname())
                )
        ));

        if (player != null) {
            MSLogger.fine(player, SUCCESSFULLY_REMOVED_MINE.args(text(skinName)));
        }
    }

    private boolean editSkin(
            @NotNull String link,
            @NotNull String skinName,
            @NotNull PlayerFile playerFile,
            int skinIndex
    ) {
        final Skin skin = Skin.create(skinName, link);

        if (skin == null) {
            this.reply(SERVICE_UNAVAILABLE);
            return true;
        }

        playerFile.setSkin(skinIndex, skin);
        this.replyEmbed(
                renderTranslation(
                        SUCCESSFULLY_EDITED.args(
                                text(skinName),
                                this.playerInfo.getDefaultName(),
                                text(this.playerInfo.getNickname())
                        )
                )
        );
        return false;
    }

    public static @NotNull MessageEmbed craftEmbed(final @NotNull String description) {
        return new MessageEmbed(
                null,
                renderTranslation("ms.discord.embed.title"),
                description,
                EmbedType.RICH,
                null,
                0x3368cb,
                new MessageEmbed.Thumbnail(
                        "https://github.com/MinersStudios/WhoMine/blob/release/assets/logo/text_logo.png?raw=true",
                        null,
                        0,
                        0
                ),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
