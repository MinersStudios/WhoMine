package com.minersstudios.mscore.util;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.minersstudios.mscore.util.menu.ShulkerBoxMenu;
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
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.biome.BiomeManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerShowEntityEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Utility class for {@link Player}
 */
public final class PlayerUtils {
    private static final ImmutableSet<EntityType> MOB_FILTER = Sets.immutableEnumSet(
            //<editor-fold desc="Ignorable mob types">
            EntityType.DROPPED_ITEM,
            EntityType.ARROW,
            EntityType.SPECTRAL_ARROW,
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.DRAGON_FIREBALL,
            EntityType.EGG,
            EntityType.FISHING_HOOK,
            EntityType.WITHER_SKULL,
            EntityType.TRIDENT,
            EntityType.SNOWBALL,
            EntityType.SMALL_FIREBALL,
            EntityType.FIREBALL,
            EntityType.FIREWORK,
            EntityType.SPLASH_POTION,
            EntityType.THROWN_EXP_BOTTLE,
            EntityType.EXPERIENCE_ORB,
            EntityType.LLAMA_SPIT,
            EntityType.LIGHTNING
            //</editor-fold>
    );

    @Contract(value = " -> fail")
    private PlayerUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Sets player to a seated position underneath him
     *
     * @param player Player that will sit
     * @see #setSitting(Player, Location, Component)
     */
    public static void setSitting(@NotNull Player player) {
        setSitting(player, player.getLocation(), null);
    }

    /**
     * Sets player to a seated position in specified location
     *
     * @param player   Player that will sit
     * @param location Location where the player will sit
     * @see #setSitting(Player, Location, Component)
     */
    public static void setSitting(
            @NotNull Player player,
            @NotNull Location location
    ) {
        setSitting(player, location, null);
    }

    /**
     * Sets player to a seated position in specified location
     * with message
     *
     * @param player   Player that will sit
     * @param location Location where the player will sit
     * @param message  Message that will be sent to the players
     *                 around the player who is sitting
     * @see PlayerInfo#setSitting(Location, Component)
     */
    public static void setSitting(
            @NotNull Player player,
            @NotNull Location location,
            @Nullable Component message
    ) {
        PlayerInfo.fromOnlinePlayer(player).setSitting(location, message);
    }

    /**
     * Unsets the sitting position of the player
     *
     * @param player Player who is currently sitting
     *               and will be unset
     * @see #unsetSitting(Player, Component)
     */
    public static void unsetSitting(@NotNull Player player) {
        unsetSitting(player, null);
    }

    /**
     * Unsets the sitting position of the player with message
     *
     * @param player  Player who is currently sitting
     *                and will be unset
     * @param message Message that will be sent to the players
     *                around the player who is sitting
     * @see PlayerInfo#unsetSitting(Component)
     */
    public static void unsetSitting(
            @NotNull Player player,
            @Nullable Component message
    ) {
        PlayerInfo.fromOnlinePlayer(player).unsetSitting(message);
    }

    /**
     * Loads the players current location, health, inventory, motion, and
     * other information from the [uuid].dat file, in the
     * [level-name]/playerdata/ folder.
     * <p>
     * Note: This will overwrite the players current inventory, health,
     * motion, etc., with the state from the saved dat file.
     *
     * @param offlinePlayer Offline player whose data will be loaded
     * @return Online player from offline player
     */
    public static @Nullable Player loadPlayer(@NotNull OfflinePlayer offlinePlayer) {
        if (!offlinePlayer.hasPlayedBefore()) return null;

        GameProfile profile = new GameProfile(
                offlinePlayer.getUniqueId(),
                offlinePlayer.getName() != null
                ? offlinePlayer.getName()
                : offlinePlayer.getUniqueId().toString()
        );
        MinecraftServer server = MinecraftServer.getServer();
        ServerLevel worldServer = server.overworld();
        Player online = new ServerPlayer(server, worldServer, profile).getBukkitEntity();

        online.loadData();
        return online;
    }

    /**
     * Sets player's skin with specified value and signature.
     * Also updates the skin for all players on the server.
     * If wanted to reset the skin, set both value and signature
     * to null.
     *
     * @param player    Player whose skin will be set
     * @param value     Value of the skin
     * @param signature Signature of the skin
     */
    public static void setSkin(
            @NotNull Player player,
            @Nullable String value,
            @Nullable String signature
    ) {
        MinecraftServer minecraftServer = MinecraftServer.getServer();
        Entity vehicle = player.getVehicle();
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        GameProfile gameProfile = serverPlayer.getGameProfile();
        PropertyMap propertyMap = gameProfile.getProperties();

        if (vehicle != null) {
            vehicle.eject();
        }

        if (propertyMap.containsKey("textures")) {
            Property oldProperty = propertyMap.get("textures").iterator().next();
            propertyMap.remove("textures", oldProperty);
        }

        if (
                !StringUtils.isBlank(value)
                && !StringUtils.isBlank(signature)
        ) {
            propertyMap.put("textures", new Property("textures", value, signature));
        }

        if (!serverPlayer.sentListPacket) {
            serverPlayer.gameProfile = gameProfile;
            return;
        }

        Location location = player.getLocation();
        ServerGamePacketListenerImpl connection = serverPlayer.connection;
        ServerLevel serverLevel = serverPlayer.serverLevel();
        ServerPlayerGameMode gameMode = serverPlayer.gameMode;
        var players = minecraftServer.getPlayerList().players;

        ClientboundRespawnPacket respawnPacket = new ClientboundRespawnPacket(
                serverLevel.dimensionTypeId(),
                serverLevel.dimension(),
                BiomeManager.obfuscateSeed(serverLevel.getSeed()),
                gameMode.getGameModeForPlayer(),
                gameMode.getPreviousGameModeForPlayer(),
                serverLevel.isDebug(),
                serverLevel.isFlat(),
                ClientboundRespawnPacket.KEEP_ALL_DATA,
                serverPlayer.getLastDeathLocation(),
                serverPlayer.getPortalCooldown()
        );
        ClientboundSetExperiencePacket experiencePacket = new ClientboundSetExperiencePacket(
                serverPlayer.experienceProgress,
                serverPlayer.totalExperience,
                serverPlayer.experienceLevel
        );

        players.stream()
        .filter(forWho -> forWho.getBukkitEntity().canSee(player))
        .forEach(forWho -> unregisterEntity(forWho, serverPlayer));

        serverPlayer.gameProfile = gameProfile;

        players.stream()
        .filter(forWho -> forWho.getBukkitEntity().canSee(player))
        .forEach(forWho -> trackAndShowEntity(forWho, serverPlayer));

        connection.send(respawnPacket);
        serverPlayer.onUpdateAbilities();
        connection.teleport(location);
        minecraftServer.getPlayerList().sendAllPlayerInfo(serverPlayer);
        connection.send(experiencePacket);

        for (var mobEffect : serverPlayer.getActiveEffects()) {
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
            @NotNull UUID uuid,
            @NotNull String name
    ) {
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        GameProfile gameProfile = new GameProfile(uuid, name);

        return craftServer.getOfflinePlayer(gameProfile);
    }

    /**
     * Creates a player profile based on the provided UUID and nickname
     *
     * @param uuid     The UUID of the player
     * @param nickname The nickname of the player
     * @return New player profile object
     * @throws IllegalArgumentException If uuid and nickname are both null or empty
     */
    @Contract("_, _ -> new")
    public static @NotNull PlayerProfile craftProfile(
            @Nullable UUID uuid,
            @Nullable String nickname
    ) throws IllegalArgumentException {
        return new CraftPlayerProfile(uuid, nickname);
    }

    /**
     * Opens the shulker box for the player without animation
     * and block updates
     *
     * @param player     The player to open the shulker box
     * @param shulkerBox The shulker box to open
     */
    public static void openShulkerBoxSilent(
            @NotNull Player player,
            @NotNull ShulkerBox shulkerBox,
            boolean playSound
    ) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        Container inventory = ((CraftInventory) shulkerBox.getInventory()).getInventory();

        if (inventory != null && !serverPlayer.isSpectator()) {
            int syncId = serverPlayer.nextContainerCounter();
            ShulkerBoxMenu shulkerBoxMenu = new ShulkerBoxMenu(syncId, serverPlayer.getInventory(), inventory);
            AbstractContainerMenu container = CraftEventFactory.callInventoryOpenEvent(serverPlayer, shulkerBoxMenu, false);

            container.setTitle(((MenuProvider) inventory).getDisplayName());

            serverPlayer.containerMenu = container;

            if (playSound) {
                shulkerBox.getWorld().playSound(shulkerBox.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5f, serverPlayer.getRandom().nextFloat() * 0.1f + 0.9f);
            }

            if (!serverPlayer.isImmobile()) {
                ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(syncId, MenuType.SHULKER_BOX, container.getTitle());
                serverPlayer.connection.send(packet);
            }

            serverPlayer.initMenu(container);
        }
    }


    /**
     * @param player The player to get the target block
     *               from
     * @return The target block
     */
    public static @Nullable Block getTargetBlock(@NotNull Player player) {
        Location eyeLocation = player.getEyeLocation();
        RayTraceResult rayTraceResult = player.getWorld().rayTraceBlocks(
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
     * @param player      The player to get the target entity
     *                    from
     * @param targetBlock The target block
     * @return The target entity
     */
    public static @Nullable Entity getTargetEntity(
            @NotNull Player player,
            @Nullable Block targetBlock
    ) {
        Location eyeLocation = player.getEyeLocation();
        Predicate<Entity> filter = entity -> entity != player && !MOB_FILTER.contains(entity.getType());
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(eyeLocation, eyeLocation.getDirection(), 4.5d, filter);

        if (rayTraceResult == null) return null;

        Entity targetEntity = rayTraceResult.getHitEntity();

        return targetBlock != null
                && targetEntity != null
                && eyeLocation.distance(targetBlock.getLocation()) <= eyeLocation.distance(targetEntity.getLocation())
                ? null
                : targetEntity;
    }

    private static void unregisterEntity(
            @NotNull ServerPlayer forWho,
            @NotNull ServerPlayer serverPlayer
    ) {
        ChunkMap tracker = forWho.serverLevel().getChunkSource().chunkMap;
        ChunkMap.TrackedEntity entry = tracker.entityMap.get(serverPlayer.getId());
        ClientboundPlayerInfoRemovePacket packet = new ClientboundPlayerInfoRemovePacket(List.of(serverPlayer.getUUID()));

        if (entry != null) {
            entry.removePlayer(forWho);
        }

        if (serverPlayer.sentListPacket) {
            forWho.connection.send(packet);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void trackAndShowEntity(
            @NotNull ServerPlayer forWho,
            @NotNull ServerPlayer serverPlayer
    ) {
        PluginManager pluginManager = serverPlayer.getBukkitEntity().getServer().getPluginManager();
        ChunkMap tracker = forWho.serverLevel().getChunkSource().chunkMap;
        ChunkMap.TrackedEntity trackedEntity = tracker.entityMap.get(serverPlayer.getId());
        ClientboundPlayerInfoUpdatePacket packet = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(serverPlayer));
        Event event = new PlayerShowEntityEvent(forWho.getBukkitEntity(), serverPlayer.getBukkitEntity());

        forWho.connection.send(packet);

        if (
                trackedEntity != null
                && !trackedEntity.seenBy.contains(forWho.connection)
        ) {
            trackedEntity.updatePlayer(forWho);
        }

        pluginManager.callEvent(event);
    }
}
