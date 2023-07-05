package com.github.minersstudios.msessentials.listeners.chat;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.DiscordMap;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.utils.MessageUtils;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordPrivateMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslation;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class DiscordPrivateMessageReceivedListener {
    private final Map<Long, Attempt> messageAttempts = new HashMap<>();
    private final Map<Long, Attempt> codeAttempts = new HashMap<>();
    private Message message;

    private static final Attempt ZERO_ATTEMPT = new Attempt(0, 0L);
    private static final int MESSAGE_MAX_ATTEMPTS = 2;
    private static final long MESSAGE_FLOOD_TIMEOUT = 1000L;
    private static final int CODE_MAX_ATTEMPTS = 10;
    private static final long CODE_FLOOD_TIMEOUT = 3000L;

    @Subscribe
    public void onDiscordPrivateMessageReceived(@NotNull DiscordPrivateMessageReceivedEvent event) {
        this.message = event.getMessage();
        String messageString = this.message.getContentDisplay();
        User author = event.getAuthor();
        long authorId = author.getIdLong();
        PlayerInfo playerInfo = PlayerInfo.fromDiscord(authorId);
        short code = 0;

        if (this.isFlooding(authorId)) {
            this.reply(renderTranslation("ms.discord.message_attempts_limit_reached"));
            return;
        }

        if (messageString.matches("\\d+")) {
            String invalidCode = renderTranslation("ms.discord.invalid_code");

            try {
                code = Short.parseShort(messageString);
            } catch (NumberFormatException e) {
                this.reply(invalidCode);
                return;
            }

            if (code < 1000 || code > 9999) {
                this.reply(invalidCode);
                return;
            }

            if (this.isCodeFlooding(authorId)) {
                this.reply(renderTranslation("ms.discord.code_attempts_limit_reached"));
                return;
            }
        }

        if (code == 0 && playerInfo == null) {
            this.reply(renderTranslation("ms.discord.not_linked"));
            return;
        }

        if (code > 0) {
            DiscordMap discordMap = MSEssentials.getCache().discordMap;
            PlayerInfo fromCode = discordMap.validateCode(code);

            if (fromCode == null) {
                this.reply(renderTranslation("ms.discord.no_code"));
            } else if (fromCode.equals(playerInfo)) {
                this.replyEmbed(renderTranslation("ms.discord.already_linked"));
            } else {
                playerInfo = fromCode;

                discordMap.removeCode(code);
                fromCode.linkDiscord(authorId);
                this.replyEmbed(
                        renderTranslation(
                                translatable(
                                        "ms.discord.successfully_linked",
                                        playerInfo.getDefaultName(),
                                        text(playerInfo.getNickname())
                                )
                        )
                );

                Player onlinePlayer = fromCode.getOnlinePlayer();
                if (onlinePlayer != null) {
                    if (
                            onlinePlayer.getOpenInventory().getTopInventory() instanceof CustomInventory customInventory
                            && customInventory.getTitle().startsWith("§f" + renderTranslation("ms.menu.discord.title"))
                    ) {
                        Bukkit.getScheduler().runTask(MSEssentials.getInstance(), () -> onlinePlayer.closeInventory());
                    }

                    ChatUtils.sendFine(
                            onlinePlayer,
                            translatable(
                                    "ms.command.discord.link.success",
                                    Component.text(author.getName())
                            )
                    );
                }
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

    private boolean isCodeFlooding(long authorId) {
        long currentTime = System.currentTimeMillis();
        Attempt attempt = this.codeAttempts.getOrDefault(authorId, ZERO_ATTEMPT);
        int attemptCount = attempt.count;
        long lastAttempt = attempt.time;
        Attempt newAttempt = new Attempt(++attemptCount, currentTime);

        if (lastAttempt == 0L) {
            this.codeAttempts.put(authorId, newAttempt);
            return false;
        }

        if (currentTime - lastAttempt <= CODE_FLOOD_TIMEOUT) {
            if (attemptCount < CODE_MAX_ATTEMPTS) {
                this.codeAttempts.put(authorId, newAttempt);
            }

            return attemptCount >= CODE_MAX_ATTEMPTS;
        }

        this.codeAttempts.remove(authorId);
        return false;
    }

    private boolean isFlooding(long authorId) {
        long currentTime = System.currentTimeMillis();
        Attempt attempt = this.messageAttempts.getOrDefault(authorId, ZERO_ATTEMPT);
        int attemptCount = attempt.count;
        long lastAttempt = attempt.time;
        Attempt newAttempt = new Attempt(++attemptCount, currentTime);

        if (lastAttempt == 0L) {
            this.messageAttempts.put(authorId, newAttempt);
            return false;
        }

        if (currentTime - lastAttempt <= MESSAGE_FLOOD_TIMEOUT) {
            if (attemptCount < MESSAGE_MAX_ATTEMPTS) {
                this.messageAttempts.put(authorId, newAttempt);
            }

            return attemptCount >= MESSAGE_MAX_ATTEMPTS;
        }

        this.messageAttempts.remove(authorId);
        return false;
    }

    private record Attempt(int count, long time) {}
}
