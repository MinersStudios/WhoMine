package com.minersstudios.mscore.utility;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.minersstudios.mscore.inventory.ShulkerBoxMenu;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.biome.BiomeManager;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerShowEntityEvent;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Utility class for {@link Player}
 */
public final class PlayerUtils {

    @Contract(" -> fail")
    private PlayerUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Sets player to a seated position underneath him
     *
     * @param plugin MSEssentials plugin
     * @param player Player that will sit
     * @see #setSitting(MSEssentials, Player, Location, Component)
     */
    public static void setSitting(
            final @NotNull MSEssentials plugin,
            final @NotNull Player player
    ) {
        setSitting(plugin, player, player.getLocation(), null);
    }

    /**
     * Sets player to a seated position in specified location
     *
     * @param plugin   MSEssentials plugin
     * @param player   Player that will sit
     * @param location Location where the player will sit
     * @see #setSitting(MSEssentials, Player, Location, Component)
     */
    public static void setSitting(
            final @NotNull MSEssentials plugin,
            final @NotNull Player player,
            final @NotNull Location location
    ) {
        setSitting(plugin, player, location, null);
    }

    /**
     * Sets player to a seated position in specified location with a message
     *
     * @param plugin   MSEssentials plugin
     * @param player   Player that will sit
     * @param location Location where the player will sit
     * @param message  Message that will be sent to the players around the
     *                 player who is sitting
     * @see PlayerInfo#setSitting(Location, Component)
     */
    public static void setSitting(
            final @NotNull MSEssentials plugin,
            final @NotNull Player player,
            final @NotNull Location location,
            final @Nullable Component message
    ) {
        PlayerInfo
        .fromOnlinePlayer(plugin, player)
        .setSitting(location, message);
    }

    /**
     * Unsets the sitting position of the player
     *
     * @param plugin MSEssentials plugin
     * @param player Player, who is currently sitting and will be unset
     * @see #unsetSitting(MSEssentials, Player, Component)
     */
    public static void unsetSitting(
            final @NotNull MSEssentials plugin,
            final @NotNull Player player
    ) {
        unsetSitting(plugin, player, null);
    }

    /**
     * Unsets the sitting position of the player with a message
     *
     * @param plugin  MSEssentials plugin
     * @param player  Player, who is currently sitting and will be unset
     * @param message Message that will be sent to the players around the player
     *                who is sitting
     * @see PlayerInfo#unsetSitting(Component)
     */
    public static void unsetSitting(
            final @NotNull MSEssentials plugin,
            final @NotNull Player player,
            final @Nullable Component message
    ) {
        PlayerInfo
        .fromOnlinePlayer(plugin, player)
        .unsetSitting(message);
    }

    /**
     * Loads the players current location, health, inventory, motion, and other
     * information from the [uuid].dat file, in [level-name]/playerdata folder.
     * <br>
     * <b>NOTE:</b> This will overwrite the players' current inventory, health,
     * motion, etc., with the state of the saved dat file.
     *
     * @param offlinePlayer Offline player whose data will be loaded
     * @return Online player from offline player
     */
    public static @Nullable Player loadPlayer(final @NotNull OfflinePlayer offlinePlayer) {
        if (!offlinePlayer.hasPlayedBefore()) {
            return null;
        }

        final MinecraftServer server = MinecraftServer.getServer();
        final CraftPlayer online = new ServerPlayer(
                server,
                server.overworld(),
                new GameProfile(
                        offlinePlayer.getUniqueId(),
                        offlinePlayer.getName() != null
                                ? offlinePlayer.getName()
                                : offlinePlayer.getUniqueId().toString()
                ),
                ((CraftPlayer) offlinePlayer).getHandle().clientInformation()
        ).getBukkitEntity();

        online.loadData();

        return online;
    }

    /**
     * Sets player's skin with specified value and signature. Also updates the
     * skin for all players on the server. If wanted to reset the skin, set both
     * value and signature to null.
     *
     * @param player    Player, whose skin will be set
     * @param value     Value of the skin
     * @param signature Signature of the skin
     */
    public static void setSkin(
            final @NotNull Player player,
            final @Nullable String value,
            final @Nullable String signature
    ) {
        final MinecraftServer minecraftServer = MinecraftServer.getServer();
        final Entity vehicle = player.getVehicle();
        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        final GameProfile gameProfile = serverPlayer.getGameProfile();
        final PropertyMap propertyMap = gameProfile.getProperties();

        if (vehicle != null) {
            vehicle.eject();
        }

        if (propertyMap.containsKey("textures")) {
            final Property oldProperty = propertyMap.get("textures").iterator().next();

            propertyMap.remove("textures", oldProperty);
        }

        if (
                ChatUtils.isNotBlank(value)
                && ChatUtils.isNotBlank(signature)
        ) {
            propertyMap.put("textures", new Property("textures", value, signature));
        }

        if (!serverPlayer.sentListPacket) {
            serverPlayer.gameProfile = gameProfile;
            return;
        }

        final ServerGamePacketListenerImpl connection = serverPlayer.connection;
        final ServerLevel serverLevel = serverPlayer.serverLevel();
        final ServerPlayerGameMode gameMode = serverPlayer.gameMode;
        final var players = minecraftServer.getPlayerList().players;

        for (final var forWho : players) {
            if (forWho.getBukkitEntity().canSee(player)) {
                unregisterEntity(forWho, serverPlayer);
            }
        }

        serverPlayer.gameProfile = gameProfile;

        for (final var forWho : players) {
            if (forWho.getBukkitEntity().canSee(player)) {
                trackAndShowEntity(forWho, serverPlayer);
            }
        }

        connection.send(
                new ClientboundRespawnPacket(
                        new CommonPlayerSpawnInfo(
                                serverLevel.dimensionTypeId(),
                                serverLevel.dimension(),
                                BiomeManager.obfuscateSeed(serverLevel.getSeed()),
                                gameMode.getGameModeForPlayer(),
                                gameMode.getPreviousGameModeForPlayer(),
                                serverLevel.isDebug(),
                                serverLevel.isFlat(),
                                serverPlayer.getLastDeathLocation(),
                                serverPlayer.getPortalCooldown()
                        ),
                        ClientboundRespawnPacket.KEEP_ALL_DATA
                )
        );
        serverPlayer.onUpdateAbilities();
        connection.teleport(player.getLocation());
        minecraftServer.getPlayerList().sendAllPlayerInfo(serverPlayer);
        connection.send(
                new ClientboundSetExperiencePacket(
                        serverPlayer.experienceProgress,
                        serverPlayer.totalExperience,
                        serverPlayer.experienceLevel
                )
        );

        for (final var mobEffect : serverPlayer.getActiveEffects()) {
            connection.send(new ClientboundUpdateMobEffectPacket(serverPlayer.getId(), mobEffect));
        }

        if (player.isOp()) {
            player.setOp(false);
            player.setOp(true);
        }
    }

    /**
     * Creates an offline player based on their UUID and name
     *
     * @param uuid The UUID of the player
     * @param name The name of the player
     * @return The offline player corresponding to the UUID and name
     */
    public static @NotNull OfflinePlayer getOfflinePlayer(
            final @NotNull UUID uuid,
            final @NotNull String name
    ) {
        return ((CraftServer) Bukkit.getServer()).getOfflinePlayer(new GameProfile(uuid, name));
    }

    /**
     * Creates a player profile based on the provided UUID and nickname
     *
     * @param uuid     The UUID of the player
     * @param nickname The nickname of the player
     * @return New player profile object
     * @throws IllegalArgumentException If uuid and nickname are both null
     *                                  or empty
     */
    @Contract("_, _ -> new")
    public static @NotNull PlayerProfile craftProfile(
            final @NotNull UUID uuid,
            final @NotNull String nickname
    ) throws IllegalArgumentException {
        return new CraftPlayerProfile(uuid, nickname);
    }

    /**
     * Opens the shulker box for the player without animation and block updates
     *
     * @param player     The player to open the shulker box
     * @param shulkerBox The shulker box to open
     */
    public static void openShulkerBoxSilent(
            final @NotNull Player player,
            final @NotNull ShulkerBox shulkerBox,
            final boolean playSound
    ) {
        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        final Container inventory = ((CraftInventory) shulkerBox.getInventory()).getInventory();

        if (
                inventory != null
                && !serverPlayer.isSpectator()
        ) {
            final int syncId = serverPlayer.nextContainerCounter();
            final AbstractContainerMenu container = CraftEventFactory.callInventoryOpenEventWithTitle(
                    serverPlayer,
                    new ShulkerBoxMenu(syncId, serverPlayer.getInventory(), inventory)
            ).getSecond();

            container.setTitle(((MenuProvider) inventory).getDisplayName());

            serverPlayer.containerMenu = container;

            if (playSound) {
                final Location location = shulkerBox.getLocation();

                serverPlayer.level().playSound(
                        null,
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        SoundEvents.SHULKER_BOX_OPEN,
                        SoundSource.BLOCKS,
                        0.5f,
                        serverPlayer.getRandom().nextFloat() * 0.1f + 0.9f
                );
            }

            if (!serverPlayer.isImmobile()) {
                serverPlayer.connection.send(
                        new ClientboundOpenScreenPacket(syncId, MenuType.SHULKER_BOX, container.getTitle())
                );
            }

            serverPlayer.initMenu(container);
        }
    }


    /**
     * @param player The player to get the target block from
     * @return The target block
     */
    public static @Nullable Block getTargetBlock(final @NotNull Player player) {
        final Location eyeLocation = player.getEyeLocation();
        final RayTraceResult rayTraceResult = player.getWorld().rayTraceBlocks(
                eyeLocation,
                eyeLocation.getDirection(),
                4.5d,
                FluidCollisionMode.NEVER,
                false
        );
        return rayTraceResult != null
                ? rayTraceResult.getHitBlock()
                : null;
    }

    /**
     * @param player      The player to get the target entity from
     * @param targetBlock The target block
     * @return The target entity
     */
    public static @Nullable Entity getTargetEntity(
            final @NotNull Player player,
            final @Nullable Block targetBlock
    ) {
        if (targetBlock == null) {
            return null;
        }

        final Location eyeLocation = player.getEyeLocation();
        final Predicate<Entity> filter =
                entity ->
                        entity != player
                        && !isIgnorableEntity(entity.getType());
        final RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(eyeLocation, eyeLocation.getDirection(), 4.5d, filter);

        if (rayTraceResult == null) {
            return null;
        }

        final Entity targetEntity = rayTraceResult.getHitEntity();

        return targetEntity != null
                && eyeLocation.distance(targetBlock.getLocation()) <= eyeLocation.distance(targetEntity.getLocation())
                ? null
                : targetEntity;
    }

    private static boolean isIgnorableEntity(final @NotNull EntityType entityType) {
        return switch (entityType) {
            //<editor-fold desc="Ignorable entities" defaultstate="collapsed">
            case DROPPED_ITEM,
                    ARROW,
                    SPECTRAL_ARROW,
                    AREA_EFFECT_CLOUD,
                    DRAGON_FIREBALL,
                    EGG,
                    FISHING_HOOK,
                    WITHER_SKULL,
                    TRIDENT,
                    SNOWBALL,
                    SMALL_FIREBALL,
                    FIREBALL,
                    FIREWORK,
                    SPLASH_POTION,
                    THROWN_EXP_BOTTLE,
                    EXPERIENCE_ORB,
                    LLAMA_SPIT,
                    LIGHTNING -> true;
            //</editor-fold>
            default -> false;
        };
    }

    private static void unregisterEntity(
            final @NotNull ServerPlayer forWho,
            final @NotNull ServerPlayer serverPlayer
    ) {
        final ChunkMap.TrackedEntity entry = forWho.serverLevel().getChunkSource().chunkMap.entityMap.get(serverPlayer.getId());

        if (entry != null) {
            entry.removePlayer(forWho);
        }

        if (serverPlayer.sentListPacket) {
            forWho.connection.send(
                    new ClientboundPlayerInfoRemovePacket(Collections.singletonList(serverPlayer.getUUID()))
            );
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void trackAndShowEntity(
            final @NotNull ServerPlayer forWho,
            final @NotNull ServerPlayer serverPlayer
    ) {
        final ChunkMap.TrackedEntity trackedEntity = forWho.serverLevel().getChunkSource().chunkMap.entityMap.get(serverPlayer.getId());

        forWho.connection.send(
                ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(Collections.singletonList(serverPlayer))
        );

        if (
                trackedEntity != null
                && !trackedEntity.seenBy.contains(forWho.connection)
        ) {
            trackedEntity.updatePlayer(forWho);
        }

        serverPlayer.getBukkitEntity().getServer().getPluginManager().callEvent(
                new PlayerShowEntityEvent(
                        forWho.getBukkitEntity(),
                        serverPlayer.getBukkitEntity()
                )
        );
    }
}
