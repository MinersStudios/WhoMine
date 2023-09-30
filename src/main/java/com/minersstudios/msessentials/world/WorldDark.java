package com.minersstudios.msessentials.world;

import com.google.common.collect.ImmutableList;
import com.minersstudios.msessentials.MSEssentials;
import net.kyori.adventure.util.TriState;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * World dark singleton.
 * This world is used for safe registration and login.
 * Moving, interacting and other actions are not allowed in this world.
 * All players teleported to this world must spectate {@link #darkEntity}.
 * {@link #darkEntity} is invisible {@link ItemFrame} and has no collision.
 * Use {@link #teleportToDarkWorld(Player)} to teleport player to world dark.
 *
 * @see #init()
 */
public class WorldDark extends CraftWorld {
    private static WorldDark singleton = null;
    private static ItemFrame darkEntity = null;

    private static final String WORLD_NAME = "world_dark";
    private static final ChunkGenerator CHUNK_GENERATOR = new ChunkGenerator() {};
    private static final BiomeProvider BIOME_PROVIDER = new BiomeProvider() {
        private static final List<Biome> BIOMES = ImmutableList.of(Biome.THE_VOID);

        @Override
        public @NotNull Biome getBiome(
                final @NotNull WorldInfo worldInfo,
                final int x,
                final int y,
                final int z
        ) {
            return Biome.THE_VOID;
        }

        @Override
        public @NotNull @Unmodifiable List<Biome> getBiomes(final @NotNull WorldInfo worldInfo) {
            return BIOMES;
        }
    };
    private static final Environment ENVIRONMENT = Environment.NORMAL;

    private WorldDark() {
        super(create(), CHUNK_GENERATOR, BIOME_PROVIDER, ENVIRONMENT);
    }

    /**
     * @return World dark singleton
     * @throws UnsupportedOperationException If world dark singleton is not initialized
     */
    public static @NotNull WorldDark getInstance() throws UnsupportedOperationException {
        if (singleton == null) {
            throw new UnsupportedOperationException("World dark singleton not initialized");
        }

        return singleton;
    }

    /**
     * @return Dark entity instance
     * @throws UnsupportedOperationException If dark entity is not initialized
     */
    public static @NotNull Entity getDarkEntity() throws UnsupportedOperationException {
        if (darkEntity == null) {
            throw new UnsupportedOperationException("Dark entity not initialized");
        }

        return darkEntity;
    }

    /**
     * @param world World to check
     * @return True if world is world dark, false otherwise
     */
    @Contract("null -> false")
    public static boolean isWorldDark(final @Nullable World world) {
        return world != null && world.getName().equals(WORLD_NAME);
    }

    /**
     * @param location Location to check
     * @return True if location is in world dark, false otherwise
     */
    @Contract("null -> false")
    public static boolean isInWorldDark(final @Nullable Location location) {
        return location != null && location.getWorld().getName().equals(WORLD_NAME);
    }

    /**
     * @param entity Entity to check
     * @return True if entity is in world dark, false otherwise
     */
    @Contract("null -> false")
    public static boolean isInWorldDark(final @Nullable Entity entity) {
        return entity != null && entity.getWorld().getName().equals(WORLD_NAME);
    }

    /**
     * Teleports player to world dark and sets spectating dark entity
     *
     * @param player Player to teleport
     */
    public static @NotNull CompletableFuture<Boolean> teleportToDarkWorld(final @NotNull Player player) {
        return player.teleportAsync(singleton.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN).thenApply(bool -> {
            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(darkEntity);
            return bool;
        });
    }

    /**
     * Initializes world dark instance.
     * Called once in {@link MSEssentials#enable()} method.
     */
    public static void init() {
        if (singleton != null) {
            throw new UnsupportedOperationException("Cannot redefine world dark instance");
        }

        singleton = new WorldDark();
        darkEntity = singleton.getEntitiesByClass(ItemFrame.class).stream().findFirst().orElseGet(() ->
                singleton.spawn(singleton.getSpawnLocation(), ItemFrame.class, entity -> {
                    entity.setGravity(false);
                    entity.setFixed(true);
                    entity.setVisible(false);
                    entity.setInvulnerable(true);
                })
        );
    }

    /**
     * Loads or creates world dark instance.
     *
     * @return Newly created or loaded world dark instance
     * @throws UnsupportedOperationException If world dark is not successfully created
     */
    private static @NotNull ServerLevel create() throws UnsupportedOperationException {
        final World world = new WorldCreator(WORLD_NAME)
                .generator(CHUNK_GENERATOR)
                .biomeProvider(BIOME_PROVIDER)
                .environment(ENVIRONMENT)
                .type(WorldType.FLAT)
                .generateStructures(false)
                .hardcore(false)
                .keepSpawnLoaded(TriState.TRUE)
                .createWorld();

        if (world == null) {
            throw new UnsupportedOperationException("Failed to create world");
        }

        world.setTime(18000L);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRule(GameRule.FALL_DAMAGE, false);
        world.setGameRule(GameRule.FIRE_DAMAGE, false);
        world.setGameRule(GameRule.DROWNING_DAMAGE, false);
        world.setGameRule(GameRule.FREEZE_DAMAGE, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);

        return ((CraftWorld) world).getHandle();
    }
}
