package com.minersstudios.msessentials.resourcepack;

import com.minersstudios.msessentials.resourcepack.data.ResourcePackData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public final class ResourcePackApplier {
    private static final Map<UUID, Map<ResourcePack, CompletableFuture<Status>>> STATUS_MAP = new Object2ObjectOpenHashMap<>();

    @Contract(" -> fail")
    private ResourcePackApplier() {
        throw new AssertionError("Utility class");
    }

    public static @NotNull @UnmodifiableView Set<UUID> statusUniqueIdSet() {
        return Collections.unmodifiableSet(STATUS_MAP.keySet());
    }

    public static @NotNull Optional<@UnmodifiableView Map<ResourcePack, CompletableFuture<Status>>> getRemainingPackStatuses(final @NotNull Player player) {
        final var map = STATUS_MAP.get(player.getUniqueId());

        if (map == null) {
            return Optional.empty();
        }

        return Optional.of(Collections.unmodifiableMap(map));
    }

    public static @NotNull Optional<@UnmodifiableView Set<ResourcePack>> getRemainingPacks(final @NotNull Player player) {
        final var map = STATUS_MAP.get(player.getUniqueId());

        if (map == null) {
            return Optional.empty();
        }

        return Optional.of(Collections.unmodifiableSet(map.keySet()));
    }

    public static @NotNull Optional<@UnmodifiableView Collection<CompletableFuture<Status>>> getRemainingStatuses(final @NotNull Player player) {
        final var map = STATUS_MAP.get(player.getUniqueId());

        if (map == null) {
            return Optional.empty();
        }

        return Optional.of(Collections.unmodifiableCollection(map.values()));
    }

    public static @NotNull Optional<CompletableFuture<Status>> getRemainingPackStatus(
            final @NotNull Player player,
            final @NotNull ResourcePack pack
    ) {
        final var map = STATUS_MAP.get(player.getUniqueId());

        if (map == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(map.get(pack));
    }

    public static boolean isApplying(final @NotNull Player player) {
        return STATUS_MAP.containsKey(player.getUniqueId());
    }

    public static boolean isApplying(
            final @NotNull Player player,
            final @NotNull ResourcePack pack
    ) {
        final var map = STATUS_MAP.get(player.getUniqueId());

        return map != null
                && map.containsKey(pack);
    }

    public static @NotNull CompletableFuture<Status> apply(
            final @NotNull Player player,
            final @NotNull ResourcePack pack
    ) {
        if (pack.isDisabled()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Pack is disabled"));
        }

        final UUID uuid = player.getUniqueId();
        final var map = STATUS_MAP.computeIfAbsent(uuid, k -> new Object2ObjectOpenHashMap<>());
        final var future = new CompletableFuture<Status>();

        map.put(pack, future);
        sendResourcePacks(
                player,
                Collections.singletonList(pack.getData())
        );

        return future;
    }

    public static @NotNull CompletableFuture<List<ResourcePack>> applyAll(
            final @NotNull Player player,
            final @NotNull BiConsumer<ResourcePack, Status> onStatusUpdate,
            final @NotNull Collection<ResourcePack> packs
    ) {
        return applyAll(
                player,
                onStatusUpdate,
                packs.toArray(ResourcePack[]::new)
        );
    }

    public static @NotNull CompletableFuture<List<ResourcePack>> applyAll(
            final @NotNull Player player,
            final @NotNull BiConsumer<ResourcePack, Status> onStatusUpdate,
            final @NotNull ResourcePack first,
            final ResourcePack @NotNull ... rest
    ) {
        final ResourcePack[] packs = new ResourcePack[rest.length + 1];

        System.arraycopy(rest, 0, packs, 1, rest.length);
        packs[0] = first;

        return applyAll(
                player,
                onStatusUpdate,
                packs
        );
    }

    public static @NotNull CompletableFuture<List<ResourcePack>> applyAll(
            final @NotNull Player player,
            final @NotNull BiConsumer<ResourcePack, Status> onStatusUpdate,
            final ResourcePack @NotNull [] packs
    ) {
        final UUID uuid = player.getUniqueId();
        final var map = STATUS_MAP.computeIfAbsent(uuid, k -> new Object2ObjectOpenHashMap<>());
        final var acceptedList = new ObjectArrayList<ResourcePackData>();
        final var declinedList = new ObjectArrayList<ResourcePack>();

        for (final var pack : packs) {
            if (pack.isDisabled()) {
                declinedList.add(pack);

                continue;
            }

            acceptedList.add(pack.getData());
            map.put(
                    pack,
                    new CompletableFuture<Status>()
                    .whenComplete(
                            (status, throwable) ->
                                    onStatusUpdate.accept(pack, status)
                    )
            );
        }

        sendResourcePacks(player, acceptedList);

        return CompletableFuture
                .allOf(map.values().toArray(CompletableFuture[]::new))
                .thenApply(ignored -> declinedList);
    }

    public static void complete(
            final @NotNull Player player,
            final @NotNull ResourcePack pack,
            final @NotNull Status status
    ) {
        final var map = STATUS_MAP.get(player.getUniqueId());

        if (map != null) {
            final var future = map.remove(pack);

            if (future != null) {
                future.complete(status);
            }

            if (map.isEmpty()) {
                STATUS_MAP.remove(player.getUniqueId());
            }
        }
    }

    private static void sendResourcePacks(
            final @NotNull Player player,
            final @NotNull Collection<ResourcePackData> packs
    ) {
        player.sendResourcePacks(
                ResourcePackRequest
                .resourcePackRequest()
                .packs(packs)
                .required(true)
                .build()
        );
    }
}
