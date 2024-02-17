package com.minersstudios.msessentials.discord;

import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.command.impl.discord.EditSkinCommand;
import com.minersstudios.msessentials.command.impl.discord.RemoveSkinCommand;
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

import static com.minersstudios.mscore.locale.Translations.*;
import static com.minersstudios.mscore.utility.ChatUtils.serializePlainComponent;
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

    private static final String MENU_TITLE = "§f" + MENU_DISCORD_TITLE.asString();
    private static final String VARIANT_YES = DISCORD_SKIN_VARIANT_YES.asString().toLowerCase(Locale.ROOT);
    private static final String VARIANT_NO = DISCORD_SKIN_VARIANT_NO.asString().toLowerCase(Locale.ROOT);

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

        if (!this.plugin.getCache().getDiscordManager().isVerified(this.user)) {
            this.reply(DISCORD_NOT_A_USER.asString());

            return;
        }

        if (this.isFlooding()) {
            this.reply(DISCORD_MESSAGE_ATTEMPTS_LIMIT_REACHED.asString());

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
                this.reply(DISCORD_INVALID_CODE.asString());

                return;
            }

            if (
                    code < 1000
                    || code > 9999
            ) {
                this.reply(DISCORD_INVALID_CODE.asString());

                return;
            }

            if (this.isCodeFlooding()) {
                this.reply(DISCORD_CODE_ATTEMPTS_LIMIT_REACHED.asString());

                return;
            }
        }

        if (
                code == 0
                && this.playerInfo == null
        ) {
            this.reply(DISCORD_NOT_LINKED.asString());

            return;
        }

        if (code > 0) {
            this.handleCode(code);
            return;
        }

        if (attachmentSize > 1) {
            this.reply(DISCORD_SKIN_ONLY_ONE_IMG.asString());

            return;
        } else if (attachmentSize == 1) {
            final Message.Attachment attachment = attachments.get(0);
            final String link = attachment.getUrl();

            try {
                this.handleSkin(link);
            } catch (final IllegalArgumentException ignored) {
                this.reply(DISCORD_SKIN_INVALID_IMG.asString());
            }

            return;
        }

        this.reply(DISCORD_UNKNOWN_COMMAND.asString());
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
            this.reply(DISCORD_NO_CODE.asString());
        } else if (fromCode.equals(this.playerInfo)) {
            this.replyEmbed(DISCORD_ALREADY_LINKED.asString());
        } else {
            this.playerInfo = fromCode;
            final Player onlinePlayer = fromCode.getOnlinePlayer();

            discordMap.removeCode(code);
            fromCode.linkDiscord(this.userId);
            this.replyEmbed(
                    serializePlainComponent(
                            DISCORD_SUCCESSFULLY_LINKED
                            .asComponent(
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
                        COMMAND_DISCORD_LINK_SUCCESS.asTranslatable()
                        .arguments(text(this.user.getName()))
                );
            }
        }
    }

    private void handleSkin(final @NotNull String link) throws IllegalArgumentException {
        final String skinName = this.messageString;

        if (skinName.isEmpty()) {
            this.reply(DISCORD_SKIN_NO_NAME.asString());

            return;
        }

        if (!Skin.matchesNameRegex(skinName)) {
            this.reply(DISCORD_SKIN_INVALID_NAME_REGEX.asString());

            return;
        }

        final PlayerFile playerFile = this.playerInfo.getPlayerFile();

        if (playerFile.containsSkin(skinName)) {
            this.plugin.runTask(() -> this.handleEditTask(link, skinName));
            this.replyEmbed(
                    serializePlainComponent(
                            DISCORD_SKIN_ALREADY_SET
                            .asComponent(
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
                this.reply(DISCORD_SKIN_SERVICE_UNAVAILABLE.asString());

                return;
            }

            playerFile.addSkin(skin);
            this.replyEmbed(
                    serializePlainComponent(
                            DISCORD_SKIN_SUCCESSFULLY_ADDED
                            .asComponent(
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
                        DISCORD_SKIN_SUCCESSFULLY_ADDED_MINECRAFT.asTranslatable()
                        .arguments(text(skinName))
                );
            }
        } else {
            this.plugin.runTask(this::handleShowSkinListTask);

            this.replyEmbed(
                    serializePlainComponent(
                            DISCORD_SKIN_TOO_MANY_SKINS
                            .asComponent(
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
                            DISCORD_SKIN_SUCCESSFULLY_EDITED_MINECRAFT.asTranslatable()
                            .arguments(text(skinName))
                    );
                }
            } else if (reply.equals(VARIANT_NO)) {
                this.reply(DISCORD_SKIN_VARIANT_NO_REPLY.asString());
            } else {
                this.reply(DISCORD_UNKNOWN_COMMAND.asString());

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

                for (int i = 0; i < skins.size(); ++i) {
                    skinList
                    .append("\n")
                    .append(i + 1)
                    .append(" : \"")
                    .append(skins.get(i).getName())
                    .append("\"");
                }

                this.replyEmbed(serializePlainComponent(
                        DISCORD_SKIN_LIST_OF_SKINS
                        .asComponent(text(skinList.toString()))
                ));
                this.plugin.runTask(this::handleSkinListTask);
            } else if (reply.equals(VARIANT_NO)) {
                this.reply(DISCORD_SKIN_VARIANT_NO_REPLY.asString());
            } else {
                this.reply(DISCORD_UNKNOWN_COMMAND.asString());

                return false;
            }

            return true;
        };
    }

    private void handleSkinListTask() {
        this.waitingReplyTask = () -> {
            if (!LIST_INDEX_PATTERN.matcher(this.messageString).matches()) {
                this.reply(DISCORD_SKIN_VARIANT_NO_REPLY.asString());

                return true;
            }

            final byte skinIndex;
            try {
                skinIndex = Byte.parseByte(this.messageString);
            } catch (final NumberFormatException ignored) {
                this.reply(DISCORD_SKIN_INVALID_INDEX.asString());

                return false;
            }

            final Skin skin = this.playerInfo.getPlayerFile().getSkin(skinIndex - 1);

            if (skin == null) {
                this.reply(DISCORD_SKIN_INVALID_INDEX.asString());

                return false;
            }

            this.plugin.runTask(() -> this.handleSkinTask(skin));

            return true;
        };
    }

    private void handleSkinTask(final @NotNull Skin skin) {
        this.replyEmbed(DISCORD_SKIN_LIST_OF_SKIN_ACTIONS.asString());

        this.waitingReplyTask = () -> {
            if (!LIST_INDEX_PATTERN.matcher(this.messageString).matches()) {
                this.reply(DISCORD_SKIN_VARIANT_NO_REPLY.asString());

                return true;
            }

            final byte actionIndex;
            try {
                actionIndex = Byte.parseByte(this.messageString);
            } catch (final NumberFormatException ignored) {
                this.reply(DISCORD_SKIN_INVALID_INDEX.asString());

                return false;
            }

            switch (actionIndex) {
                case 1 -> this.plugin.runTask(() -> this.handleEditImageTask(skin));
                case 2 -> this.plugin.runTask(() -> this.handleEditNameTask(skin));
                case 3 -> this.plugin.runTask(() -> RemoveSkinCommand.remove(playerInfo, skin, message, null));
                default -> {
                    this.reply(DISCORD_SKIN_INVALID_INDEX.asString());

                    return false;
                }
            }

            return true;
        };
    }

    private void handleEditImageTask(final @NotNull Skin editableSkin) {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String skinName = editableSkin.getName();

        this.replyEmbed(DISCORD_SKIN_ACTION_EDIT_INFO.asString());

        this.waitingReplyTask = () -> {
            final var attachments = this.message.getAttachments();

            switch (attachments.size()) {
                case 0 -> {
                    this.reply(DISCORD_SKIN_VARIANT_NO_REPLY.asString());

                    return true;
                }
                case 1 -> {
                    try {
                        final Skin skin = Skin.create(
                                this.plugin,
                                skinName,
                                attachments.get(0).getUrl()
                        );

                        return skin != null
                                && EditSkinCommand.edit(
                                        this.playerInfo,
                                        playerFile.getSkinIndex(editableSkin),
                                        skin,
                                        this.message,
                                        null
                                );
                    } catch (final Throwable ignored) {
                        this.reply(DISCORD_SKIN_INVALID_IMG.asString());

                        return false;
                    }
                }
                default -> {
                    this.reply(DISCORD_SKIN_ONLY_ONE_IMG.asString());

                    return false;
                }
            }
        };
    }

    private void handleEditNameTask(final @NotNull Skin editableSkin) {
        final PlayerFile playerFile = this.playerInfo.getPlayerFile();
        final String oldName = editableSkin.getName();

        this.replyEmbed(DISCORD_SKIN_ACTION_RENAME_INFO.asString());

        this.waitingReplyTask = () -> {
            final String newName = this.messageString;

            if (!Skin.matchesNameRegex(newName)) {
                this.reply(DISCORD_SKIN_INVALID_NAME_REGEX.asString());

                return false;
            }

            if (playerFile.containsSkin(newName)) {
                this.replyEmbed(
                        serializePlainComponent(
                                DISCORD_SKIN_ALREADY_SET
                                .asComponent(
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
                    serializePlainComponent(
                            DISCORD_SKIN_SUCCESSFULLY_RENAMED
                            .asComponent(
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
                        DISCORD_SKIN_SUCCESSFULLY_RENAMED_MINECRAFT.asTranslatable()
                        .arguments(
                                text(oldName),
                                text(newName)
                        )
                );
            }

            return true;
        };
    }

    private boolean editSkin(
            @NotNull String link,
            @NotNull String skinName,
            @NotNull PlayerFile playerFile,
            int skinIndex
    ) {
        final Skin skin = Skin.create(this.plugin, skinName, link);

        if (skin == null) {
            this.reply(DISCORD_SKIN_SERVICE_UNAVAILABLE.asString());

            return true;
        }

        playerFile.setSkin(skinIndex, skin);
        this.replyEmbed(
                serializePlainComponent(
                        DISCORD_SKIN_SUCCESSFULLY_EDITED
                        .asComponent(
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
                DISCORD_EMBED_TITLE.asString(),
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
