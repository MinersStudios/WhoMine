package com.minersstudios.msessentials.chat;

import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

public final class ChatBuffer {
    private final @NotNull MSEssentials plugin;
    private final @NotNull Map<UUID, Queue<String>> chatQueue;

    public ChatBuffer(final @NotNull MSEssentials plugin) {
        this.plugin = plugin;
        this.chatQueue = new HashMap<>();
    }

    public @NotNull @UnmodifiableView Map<UUID, Queue<String>> getChatQueue() {
        return Collections.unmodifiableMap(this.chatQueue);
    }

    public void receiveMessage(
            final @NotNull Player player,
            @NotNull String message
    ) {
        if (message.length() <= 30) {
            queueMessage(player, message + "\n");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        int delimPos;
        int lineCount = 0;

        while (!message.isEmpty()) {
            delimPos = message.lastIndexOf(' ', 30);

            if (delimPos < 0) {
                delimPos = message.indexOf(' ', 30);
            }

            if (delimPos < 0) {
                delimPos = message.length();
            }

            stringBuilder.append(message, 0, delimPos);
            message = message.substring(delimPos + 1);
            lineCount++;

            if (
                    lineCount % 15 == 0
                    || message.isEmpty()
            ) {
                this.queueMessage(player, stringBuilder + (message.isEmpty() ? "\n" : "...\n"));
                stringBuilder = new StringBuilder();
            } else {
                stringBuilder.append("\n");
            }
        }
    }

    public static int spawnMessage(
            final @NotNull Player player,
            final @NotNull String message
    ) {
        final String[] chatLines = message.split("\n");
        final Location spawnLocation = player.getLocation().add(0.0d, 1.0d, 0.0d);
        final int duration = (message.length() + (17 * chatLines.length)) * 1200 / 800;
        Entity vehicle = player;

        for (int i = chatLines.length - 1; i >= 0; i--) {
            vehicle = spawnNameTag(
                    vehicle,
                    chatLines[i],
                    spawnLocation,
                    duration,
                    i == 0
            );
        }

        return duration;
    }

    private void queueMessage(
            final @NotNull Player player,
            final @NotNull String message
    ) {
        final UUID uuid = player.getUniqueId();

        if (!this.chatQueue.containsKey(uuid)) {
            this.chatQueue.put(uuid, new LinkedList<>());
            scheduleMessageUpdate(player, uuid, 0);
        }

        this.chatQueue.get(uuid).add(message);
    }

    private void scheduleMessageUpdate(
            final @NotNull Player player,
            final @NotNull UUID uuid,
            final int delay
    ) {
        this.plugin.runTaskLater(() -> {
            if (
                    !player.isOnline()
                    || this.chatQueue.get(uuid).isEmpty()
            ) {
                this.chatQueue.remove(uuid);
            } else {
                final String message = this.chatQueue.get(uuid).poll();

                if (message != null) {
                    this.scheduleMessageUpdate(
                            player,
                            uuid,
                            spawnMessage(player, message) + 5
                    );
                }
            }
        }, delay);
    }

    private static @NotNull AreaEffectCloud spawnNameTag(
            final @NotNull Entity vehicle,
            final @NotNull String text,
            final @NotNull Location spawnLocation,
            final int duration,
            final boolean firstLine
    ) {
        return spawnLocation.getWorld().spawn(
                spawnLocation,
                AreaEffectCloud.class,
                entity -> {
                    entity.customName(
                            (
                                    firstLine
                                    ? Font.Components.SPEECH
                                    : Component.empty()
                            )
                            .append(text(text))
                            .append(space())
                            .color(NamedTextColor.WHITE)
                    );
                    entity.setParticle(Particle.TOWN_AURA);
                    entity.setRadius(0);
                    entity.setCustomNameVisible(true);
                    entity.setWaitTime(0);
                    entity.setDuration(duration);
                    vehicle.addPassenger(entity);
                });
    }
}
