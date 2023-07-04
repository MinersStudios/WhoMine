package com.github.minersstudios.msessentials.listeners.chat;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.DiscordMap;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordPrivateMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslation;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class DiscordPrivateMessageReceivedListener {
    private Message message;

    @Subscribe
    public void onDiscordPrivateMessageReceived(@NotNull DiscordPrivateMessageReceivedEvent event) {
        this.message = event.getMessage();
        String messageString = this.message.getContentDisplay();
        User author = event.getAuthor();
        long authorId = author.getIdLong();
        PlayerInfo playerInfo = PlayerInfo.fromDiscord(authorId);
        short code = 0;

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
                this.reply(renderTranslation("ms.discord.already_linked"));
            } else {
                playerInfo = fromCode;

                discordMap.removeCode(code);
                fromCode.linkDiscord(authorId);
                this.reply(
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
}
