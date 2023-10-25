package com.minersstudios.msessentials.chat;

import com.minersstudios.mscore.util.Badges;
import com.minersstudios.msessentials.MSEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.UUID;

import static com.minersstudios.msessentials.MSEssentials.getCache;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

public final class ChatBuffer {

    private ChatBuffer() {
        throw new AssertionError("Utility class");
    }

    public static void receiveMessage(
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

            if (lineCount % 15 == 0 || message.isEmpty()) {
                queueMessage(player, stringBuilder + (message.isEmpty() ? "\n" : "...\n"));
                stringBuilder = new StringBuilder();
            } else {
                stringBuilder.append("\n");
            }
        }
    }

    private static void queueMessage(
            final @NotNull Player player,
            final @NotNull String message
    ) {
        final var chatQueue = getCache().chatQueue;
        final UUID uuid = player.getUniqueId();

        if (!chatQueue.containsKey(uuid)) {
            chatQueue.put(uuid, new LinkedList<>());
            scheduleMessageUpdate(player, uuid, 0);
        }

        chatQueue.get(uuid).add(message);
    }

    private static void scheduleMessageUpdate(
            final @NotNull Player player,
            final @NotNull UUID uuid,
            final int delay
    ) {
        MSEssentials.getInstance().runTaskLater(() -> {
            final var chatQueue = getCache().chatQueue;

            if (
                    !player.isOnline()
                    || chatQueue.get(uuid).isEmpty()
            ) {
                chatQueue.remove(uuid);
            } else {
                final String message = chatQueue.get(uuid).poll();
                if (message == null) return;
                scheduleMessageUpdate(player, uuid, spawnMessage(player, message) + 5);
            }
        }, delay);
    }

    public static int spawnMessage(
            final @NotNull Player player,
            final @NotNull String message
    ) {
        final String[] chatLines = message.split("\n");
        final int duration = (message.length() + (17 * chatLines.length)) * 1200 / 800;
        Entity vehicle = player;

        for (int i = chatLines.length - 1; i >= 0; i--) {
            vehicle = spawnNameTag(vehicle, chatLines[i], player.getLocation().add(0.0d, 1.0d, 0.0d), duration, i == 0);
        }

        return duration;
    }

    private static @NotNull AreaEffectCloud spawnNameTag(
            final @NotNull Entity vehicle,
            final @NotNull String text,
            final @NotNull Location spawnPoint,
            final int duration,
            final boolean firstLine
    ) {
        return spawnPoint.getWorld().spawn(spawnPoint, AreaEffectCloud.class, entity -> {
            entity.customName(
                    (firstLine ? Badges.SPEECH : Component.empty())
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
