package com.minersstudios.msessentials.discord;

import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.minersstudios.mscore.language.LanguageFile.renderTranslation;
import static com.minersstudios.mscore.language.LanguageRegistry.Strings.*;
import static net.kyori.adventure.text.Component.text;

public final class BotHandler {
    private final MSEssentials plugin;
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

    private static final String MENU_TITLE = "§f" + MENU_DISCORD_TITLE;
    private static final String VARIANT_YES = DISCORD_SKIN_VARIANT_YES.toLowerCase(Locale.ROOT);
    private static final String VARIANT_NO = DISCORD_SKIN_VARIANT_NO.toLowerCase(Locale.ROOT);

    public BotHandler(
            final @NotNull MSEssentials plugin,
            final @NotNull MessageReceivedEvent event
    ) {
        this.plugin = plugin;
        this.user = event.getAuthor();
        this.userId = this.user.getIdLong();
        this.playerInfo = PlayerInfo.fromDiscord(plugin, this.userId);
    }

    public @NotNull MSEssentials getPlugin() {
        return this.plugin;
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

        if (!this.plugin.getCache().getDiscordHandler().isVerified(this.user)) {
            this.reply(DISCORD_NOT_A_USER);
            return;
        }

        if (this.isFlooding()) {
            this.reply(DISCORD_MESSAGE_ATTEMPTS_LIMIT_REACHED);
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
            } catch (final NumberFormatException ignored) {
                this.reply(DISCORD_INVALID_CODE);
                return;
            }

            if (code < 1000 || code > 9999) {
                this.reply(DISCORD_INVALID_CODE);
                return;
            }

            if (this.isCodeFlooding()) {
                this.reply(DISCORD_CODE_ATTEMPTS_LIMIT_REACHED);
                return;
            }
        }

        if (code == 0 && this.playerInfo == null) {
            this.reply(DISCORD_NOT_LINKED);
            return;
        }

        if (code > 0) {
            this.handleCode(code);
            return;
        }

        if (attachmentSize > 1) {
            this.reply(DISCORD_SKIN_ONLY_ONE_IMG);
            return;
        } else if (attachmentSize == 1) {
            final Message.Attachment attachment = attachments.get(0);
            final String link = attachment.getUrl();

            if (this.messageString.isEmpty()) {
                this.reply(DISCORD_SKIN_NO_NAME);
                return;
            }

            if (!Skin.matchesNameRegex(this.messageString)) {
                this.reply(DISCORD_SKIN_INVALID_NAME_REGEX);
                return;
            }

            try {
                this.handleSkin(link);
            } catch (final IllegalArgumentException ignored) {
                this.reply(DISCORD_SKIN_INVALID_IMG);
            }

            return;
        }

        this.reply(DISCORD_UNKNOWN_COMMAND);
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
        final DiscordMap discordMap = this.plugin.getCache().getDiscordMap();
        final PlayerInfo fromCode = discordMap.validateCode(code);

        if (fromCode == null) {
            this.reply(DISCORD_NO_CODE);
        } else if (fromCode.equals(this.playerInfo)) {
            this.replyEmbed(DISCORD_ALREADY_LINKED);
        } else {
            this.playerInfo = fromCode;
            final Player onlinePlayer = fromCode.getOnlinePlayer();

            discordMap.removeCode(code);
            fromCode.linkDiscord(this.userId);
            this.replyEmbed(
                    renderTranslation(
                            LanguageRegistry.Components.DISCORD_SUCCESSFULLY_LINKED
                            .args(
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
                    this.plugin.runTask(() -> onlinePlayer.closeInventory());
                }

                MSLogger.fine(
                        onlinePlayer,
                        LanguageRegistry.Components.COMMAND_DISCORD_LINK_SUCCESS
                        .args(text(this.user.getName()))
                );
            }
        }
    }

    private void handleSkin(final @NotNull String link) throws IllegalArgumentException {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String skinName = this.messageString;

        if (playerFile.containsSkin(skinName)) {
            this.plugin.runTask(() -> this.handleEditTask(link, skinName));

            this.replyEmbed(
                    renderTranslation(
                            LanguageRegistry.Components.DISCORD_SKIN_ALREADY_SET
                            .args(
                                    this.playerInfo.getDefaultName(),
                                    text(this.playerInfo.getNickname())
                            )
                    )
            );
            return;
        }

        if (playerFile.hasAvailableSkinSlot()) {
            final Skin skin = Skin.create(this.plugin, skinName, link);

            if (skin == null) {
                this.reply(DISCORD_SKIN_SERVICE_UNAVAILABLE);
                return;
            }

            playerFile.addSkin(skin);
            this.replyEmbed(
                    renderTranslation(
                            LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_ADDED
                            .args(
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
                        LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_ADDED_MINECRAFT
                        .args(text(skinName))
                );
            }
        } else {
            this.plugin.runTask(this::handleShowSkinListTask);

            this.replyEmbed(
                    renderTranslation(
                            LanguageRegistry.Components.DISCORD_SKIN_TOO_MANY_SKINS
                            .args(
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
                if (this.editSkin(link, skinName, playerFile, playerFile.getSkinIndex(skinName))) {
                    return true;
                }

                if (player != null) {
                    MSLogger.fine(
                            player,
                            LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_EDITED_MINECRAFT
                            .args(text(skinName))
                    );
                }
            } else if (reply.equals(VARIANT_NO)) {
                this.reply(DISCORD_SKIN_VARIANT_NO_REPLY);
            } else {
                this.reply(DISCORD_UNKNOWN_COMMAND);
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

                this.replyEmbed(renderTranslation(
                        LanguageRegistry.Components.DISCORD_SKIN_LIST_OF_SKINS
                        .args(text(skinList.toString()))
                ));
                this.plugin.runTask(this::handleSkinListTask);
            } else if (reply.equals(VARIANT_NO)) {
                this.reply(DISCORD_SKIN_VARIANT_NO_REPLY);
            } else {
                this.reply(DISCORD_UNKNOWN_COMMAND);
                return false;
            }

            return true;
        };
    }

    private void handleSkinListTask() {
        this.waitingReplyTask = () -> {
            if (!LIST_INDEX_PATTERN.matcher(this.messageString).matches()) {
                this.reply(DISCORD_SKIN_VARIANT_NO_REPLY);
                return true;
            }

            final byte skinIndex;
            try {
                skinIndex = Byte.parseByte(this.messageString);
            } catch (final NumberFormatException ignored) {
                this.reply(DISCORD_SKIN_INVALID_INDEX);
                return false;
            }

            final Skin skin = this.playerInfo.getPlayerFile().getSkin(skinIndex - 1);

            if (skin == null) {
                this.reply(DISCORD_SKIN_INVALID_INDEX);
                return false;
            }

            this.plugin.runTask(() -> this.handleSkinTask(skin));
            return true;
        };
    }

    private void handleSkinTask(final @NotNull Skin skin) {
        this.replyEmbed(DISCORD_SKIN_LIST_OF_SKIN_ACTIONS);

        this.waitingReplyTask = () -> {
            if (!LIST_INDEX_PATTERN.matcher(this.messageString).matches()) {
                this.reply(DISCORD_SKIN_VARIANT_NO_REPLY);
                return true;
            }

            final byte actionIndex;
            try {
                actionIndex = Byte.parseByte(this.messageString);
            } catch (final NumberFormatException ignored) {
                this.reply(DISCORD_SKIN_INVALID_INDEX);
                return false;
            }

            switch (actionIndex) {
                case 1 -> this.plugin.runTask(() -> this.handleEditImageTask(skin));
                case 2 -> this.plugin.runTask(() -> this.handleEditNameTask(skin));
                case 3 -> this.plugin.runTask(() -> this.handleDeleteTask(skin));
                default -> {
                    this.reply(DISCORD_SKIN_INVALID_INDEX);
                    return false;
                }
            }

            return true;
        };
    }

    private void handleEditImageTask(final @NotNull Skin editableSkin) {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String skinName = editableSkin.getName();

        this.replyEmbed(DISCORD_SKIN_ACTION_EDIT_INFO);

        this.waitingReplyTask = () -> {
            final var attachments = this.message.getAttachments();

            switch (attachments.size()) {
                case 0 -> {
                    this.reply(DISCORD_SKIN_VARIANT_NO_REPLY);
                    return true;
                }
                case 1 -> {
                    final Message.Attachment attachment = attachments.get(0);
                    final String link = attachment.getUrl();

                    try {
                        if (editSkin(link, skinName, playerFile, playerFile.getSkinIndex(editableSkin))) {
                            return true;
                        }

                        final Player player = this.playerInfo.getOnlinePlayer();

                        if (player != null) {
                            MSLogger.fine(
                                    player,
                                    LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_EDITED_MINECRAFT
                                    .args(text(skinName))
                            );
                        }
                    } catch (final Exception ignored) {
                        this.reply(DISCORD_SKIN_INVALID_IMG);
                        return false;
                    }
                }
                default -> {
                    this.reply(DISCORD_SKIN_ONLY_ONE_IMG);
                    return false;
                }
            }

            return true;
        };
    }

    private void handleEditNameTask(final @NotNull Skin editableSkin) {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String oldName = editableSkin.getName();

        this.replyEmbed(DISCORD_SKIN_ACTION_RENAME_INFO);

        this.waitingReplyTask = () -> {
            final String newName = this.messageString;

            if (!Skin.matchesNameRegex(newName)) {
                this.reply(DISCORD_SKIN_INVALID_NAME_REGEX);
                return false;
            }

            if (playerFile.containsSkin(newName)) {
                this.replyEmbed(
                        renderTranslation(
                                LanguageRegistry.Components.DISCORD_SKIN_ALREADY_SET
                                .args(
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
                            LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_RENAMED
                            .args(
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
                        LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_RENAMED_MINECRAFT
                        .args(
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
                LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_REMOVED
                .args(
                        text(skinName),
                        this.playerInfo.getDefaultName(),
                        text(this.playerInfo.getNickname())
                )
        ));

        if (player != null) {
            MSLogger.fine(
                    player,
                    LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_REMOVED_MINECRAFT
                    .args(text(skinName))
            );
        }
    }

    private boolean editSkin(
            @NotNull String link,
            @NotNull String skinName,
            @NotNull PlayerFile playerFile,
            int skinIndex
    ) {
        final Skin skin = Skin.create(this.plugin, skinName, link);

        if (skin == null) {
            this.reply(DISCORD_SKIN_SERVICE_UNAVAILABLE);
            return true;
        }

        playerFile.setSkin(skinIndex, skin);
        this.replyEmbed(
                renderTranslation(
                        LanguageRegistry.Components.DISCORD_SKIN_SUCCESSFULLY_EDITED
                        .args(
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
                DISCORD_EMBED_TITLE,
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
